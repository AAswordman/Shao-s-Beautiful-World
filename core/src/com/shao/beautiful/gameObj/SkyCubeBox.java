package com.shao.beautiful.gameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.scene.Updatable;

public class SkyCubeBox implements RenderableProvider, Updatable, Disposable {

	private DefaultShaderProvider shaderProvider;
	private Model boxModel;
	private Renderable box;

	
	
		
	public SkyCubeBox(Cubemap cubemap) {
		super();

		// create shader provider
		Config shaderConfig = new Config();
		String basePathName = "shader/skybox";
		shaderConfig.vertexShader = Gdx.files.internal(basePathName + ".vs.glsl").readString();
		shaderConfig.fragmentShader = Gdx.files.internal(basePathName + ".fs.glsl").readString();
		shaderProvider = new DefaultShaderProvider(shaderConfig) {
			@Override
			public Shader getShader(Renderable renderable) {
				DefaultShader shader = (DefaultShader) super.getShader(renderable);
				return shader;
			}

			@Override
			protected Shader createShader(Renderable renderable) {
				DefaultShader shader = (DefaultShader) super.createShader(renderable);
				shader.register(RenderToolManager.globalTimeUniform,RenderToolManager.globalTimeSetter);
				shader.register(RenderToolManager.sunPosUniform,RenderToolManager.sunPosSetter);
				return shader;
			}
		};

		// create box
		float boxScale = (float) (1.0 / Math.sqrt(2.0));
		boxModel = new ModelBuilder().createBox(boxScale, boxScale, boxScale, new Material(),
				VertexAttributes.Usage.Position);
		box = boxModel.nodes.first().parts.first().setRenderable(new Renderable());

		// assign environment
		Environment env = new Environment();
		env.set(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap));
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.WHITE));
		box.environment = env;

		// set hint to render last but before transparent ones
		box.userData = SceneRenderableSorter.Hints.OPAQUE_LAST;

		// set material options : preserve background depth
		box.material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
		box.material.set(new DepthTestAttribute(false));

		// assign shader
		box.shader = shaderProvider.getShader(box);
	}

	public SkyCubeBox set(Cubemap cubemap) {
		box.environment.set(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap));
		return this;
	}

	/**
	 * @return skybox material color to be modified (default is white)
	 */
	public Color getColor() {
		return box.material.get(ColorAttribute.class, ColorAttribute.Diffuse).color;
	}

	@Override
	public void update(Camera camera, float delta) {
		// scale skybox to camera range.
		float s = camera.far * (float) Math.sqrt(2.0);
		box.worldTransform.setToScaling(s, s, s);
		box.worldTransform.setTranslation(camera.position);
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		renderables.add(box);
	}

	@Override
	public void dispose() {
		shaderProvider.dispose();
		boxModel.dispose();
	}
}
