package com.shao.beautiful.gameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.manager.world.Chunk;
import com.shao.beautiful.tools.MathPatch;

import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShader;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.Attributes;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.Mixer;

public class RenderToolManager {

	public static final int RENDER_BLOCK_DEFAULT = 0, RENDER_ENTITY = 10, RENDER_BLOCK_CUSTOM = 1;

	public static int useFlag = 0;
	public static float globalTime = 0;
	public static Vector3 sunPos = new Vector3();
	private RenderBatch manager;

	private DirectionalShadowLight shadowLight;

	private Vector3 center;

	public final static BaseShader.Uniform useFlagUniform = new BaseShader.Uniform("u_renderFlag");
	public final static BaseShader.Setter useFlagSetter = new BaseShader.LocalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, useFlag);
		}
	};

	public final static BaseShader.Uniform globalTimeUniform = new BaseShader.Uniform("u_globalTime");
	public final static BaseShader.Setter globalTimeSetter = new BaseShader.LocalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, globalTime);
		}
	};

	public final static BaseShader.Uniform blockTextureSizeUniform = new BaseShader.Uniform("u_blockTextureSize");
	public final static BaseShader.Setter blockTextureSizeSetter = new BaseShader.LocalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, 1f / ((float) BlockNature.BLOCK_TEXTURE_SIZE));
		}
	};
	public final static BaseShader.Uniform sunPosUniform = new BaseShader.Uniform("u_sun_pos");
	public final static BaseShader.Setter sunPosSetter = new BaseShader.LocalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, sunPos);
		}
	};
	public final static BaseShader.Uniform seaLevelUniform = new BaseShader.Uniform("u_seaLevel");
	public final static BaseShader.Setter seaLeveSetter = new BaseShader.LocalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			shader.set(inputID, GameConfig.gameSeaLevel);
		}
	};

	public RenderToolManager() {
		PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
		config.numBones = 60;
		config.numDirectionalLights = 1;
		config.numPointLights = 0;
		if (Gdx.files.external("shader/test.fragment.glsl").exists()) {
			config.fragmentShader = Gdx.files.external("shader/test.fragment.glsl").readString();
		} else {
			config.fragmentShader = Gdx.files.internal("shader/test.fragment.glsl").readString();
		}
		if (Gdx.files.external("shader/test.vertex.glsl").exists()) {
			config.vertexShader = Gdx.files.external("shader/test.vertex.glsl").readString();
		} else {
			config.vertexShader = Gdx.files.internal("shader/test.vertex.glsl").readString();
		}

		PBRShaderConfig configWater = PBRShaderProvider.createDefaultConfig();
		configWater.numBones = 60;
		configWater.numDirectionalLights = 1;
		configWater.numPointLights = 0;
		if (Gdx.files.external("shader/water.fragment.glsl").exists()) {
			configWater.fragmentShader = Gdx.files.external("shader/water.fragment.glsl").readString();
		} else {
			configWater.fragmentShader = Gdx.files.internal("shader/water.fragment.glsl").readString();
		}
		if (Gdx.files.external("shader/water.vertex.glsl").exists()) {
			configWater.vertexShader = Gdx.files.external("shader/water.vertex.glsl").readString();
		} else {
			configWater.vertexShader = Gdx.files.internal("shader/water.vertex.glsl").readString();
		}

		PBRDepthShader.Config depthConfig = PBRShaderProvider.createDefaultDepthConfig();
		depthConfig.numBones = 60;
		depthConfig.numDirectionalLights = 1;

		manager = new RenderBatch(new PBRShaderProvider(config) {
			private HashMap<Shader, ShaderData> map = new HashMap<>();

			@Override
			public Shader getShader(Renderable renderable) {

				DefaultShader shader = (DefaultShader) super.getShader(renderable);

				return shader;
			}

			@Override
			protected Shader createShader(Renderable renderable) {
				DefaultShader shader = (DefaultShader) super.createShader(renderable);
				shader.register(useFlagUniform, useFlagSetter);
				shader.register(globalTimeUniform, globalTimeSetter);
				shader.register(blockTextureSizeUniform, blockTextureSizeSetter);
				shader.register(sunPosUniform, sunPosSetter);
				shader.register(seaLevelUniform, seaLeveSetter);
				return shader;
			}

		}, new PBRShaderProvider(configWater) {
			@Override
			public Shader getShader(Renderable renderable) {
				DefaultShader shader = (DefaultShader) super.getShader(renderable);
				return shader;
			}

			@Override
			protected Shader createShader(Renderable renderable) {
				DefaultShader shader = (DefaultShader) super.createShader(renderable);
				shader.register(useFlagUniform, useFlagSetter);
				shader.register(globalTimeUniform, globalTimeSetter);
				shader.register(blockTextureSizeUniform, blockTextureSizeSetter);
				shader.register(sunPosUniform, sunPosSetter);
				shader.register(seaLevelUniform, seaLeveSetter);
				return shader;
			}

		}, new PBRDepthShaderProvider(depthConfig));

		manager.setAmbientLight(0.35f);
		// manager.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f,
		// 0.4f, 0.4f, 0.01f));
		// manager.environment.add(new DirectionalLightEx().set(Color.WHITE, new
		// Vector3(-1f, -0.8f, -0.2f), 5));
		manager.environment.add(((shadowLight = new DirectionalShadowLight(1024, 1024, 80f, 80f, .1f, 80f))
				.set(Color.BLUE, new Vector3(-0.5f, -0.3f, -0.2f), 15)));
		// manager.environment.add(((shadowLight = new DirectionalShadowLight(1024,
		// 1024, 80f, 80f, .1f, 80f)).set(Color.WHITE, new Vector3(-0f, -1f, -0f),
		// 15)));
		manager.environment.set(
				FogAttribute.createFog(2f, GameConfig.chunkLoadNum * Chunk.width * 1 + Chunk.width * 0.5f, .99999f));
		manager.environment.set(new ColorAttribute(ColorAttribute.Fog, 0.73f, 0.88f, 1f, 0.6f));

		manager.environment.shadowMap = shadowLight;
		Cubemap cubemap = new Cubemap(Gdx.files.internal("cubemap/cubemap_1.png"),
				Gdx.files.internal("cubemap/cubemap_3.png"), Gdx.files.internal("cubemap/cubemap_4.png"),
				Gdx.files.internal("cubemap/cubemap_5.png"), Gdx.files.internal("cubemap/cubemap_0.png"),
				Gdx.files.internal("cubemap/cubemap_2.png"));
		manager.setSkyBox(new SkyCubeBox(cubemap));
	}

	private Vector3 tmpVector3 = new Vector3();

	public void setTime(float time) {
		globalTime = time;
		float day = 24f * 60f;
		float persent = MathPatch.mod(globalTime, (day)) / day;

		sunPos.set((float) MathPatch.cos(360 * persent), (float) MathPatch.sin(360 * persent), 0.2f);
		shadowLight.set(getSunLight(persent), tmpVector3.set(sunPos).scl(-1), 15);
		manager.environment.set(new ColorAttribute(ColorAttribute.Fog, getfogColor(persent)));
		// System.out.println(getSunLight(persent));
	}

	private static ArrayList<Float[]> sunLight = new ArrayList<Float[]>();
	static {
		sunLight.add(new Float[] { 0.8f, 0.4f, 0.3f, 1f });
		sunLight.add(new Float[] { 1f, 1f, 0.7f, 1f });
		sunLight.add(new Float[] { 0.8f, 0.4f, 0.3f, 1f });
		sunLight.add(new Float[] { 0f, 0f, 0f, 1f });
	}

	private Color getSunLight(float time) {
		try {
			Float[] a = sunLight.get((int) (Math.floor(time * sunLight.size())%sunLight.size()));
			Float[] b = sunLight.get((int) (Math.ceil(time * sunLight.size())%sunLight.size()));
			Float[] mix = mix(a, b,
					(time * sunLight.size() - (Math.floor(time * sunLight.size()) % sunLight.size())) / 1f);
			return new Color(mix[0], mix[1], mix[2], 1.0f);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new Color(1, 1, 1, 1);
		}

	}

	private static ArrayList<Float[]> fogColor = new ArrayList<Float[]>();
	static {
		fogColor.add(new Float[] { 0.73f, 0.6f, 0.5f, 0.3f });
		fogColor.add(new Float[] { 0.73f, 0.88f, 1f, 0.4f });
		fogColor.add(new Float[] { 0.73f, 0.88f, 1f, 0.4f });
		fogColor.add(new Float[] { 0.73f, 0.88f, 1f, 0.4f });
		fogColor.add(new Float[] { 0.73f, 0.6f, 0.5f, 0.3f });
		fogColor.add(new Float[] { 0f, 0f, 0f, 0.6f });
		fogColor.add(new Float[] { 0f, 0f, 0f, 0.6f });
		fogColor.add(new Float[] { 0f, 0f, 0f, 0.6f });
	}

	private Color getfogColor(float time) {
		try {
			Float[] a = fogColor.get((int) (Math.floor(time * fogColor.size())%sunLight.size()));
			Float[] b = fogColor.get((int) (Math.ceil(time * fogColor.size())%sunLight.size()));
			Float[] mix = mix(a, b,
					(time * fogColor.size() - (Math.floor(time * fogColor.size()) % fogColor.size())) / 1f);
			return new Color(mix[0], mix[1], mix[2], mix[3]);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new Color(1, 1, 1, 1);
		}

	}

	private Float[] mix(Float[] a, Float[] b, double d) {
		return new Float[] { MathUtils.lerp(a[0], b[0], (float) d), MathUtils.lerp(a[1], b[1], (float) d),
				MathUtils.lerp(a[2], b[2], (float) d), MathUtils.lerp(a[3], b[3], (float) d) };
	}

	public void upDate(float t) {
		globalTime += t;
		manager.update(t);
		setTime(globalTime);
		shadowLight.setCenter(center);

	}

	public void begin(int renderFlag) {
		useFlag = renderFlag;
		manager.begin();
	}

	public void setCamera(Camera c) {
		manager.camera = c;

	}

	public void render(RenderableProvider r) {
		manager.render(r);
	}

	public void end() {
		manager.end();
	}

	public static class ShaderData {
		public int u_renderFlag;
		public int u_globalTime;

		public int u_blockTextureSize;
	}

	public void setCenter(Vector3 position) {
		this.center = position;
	}
}
