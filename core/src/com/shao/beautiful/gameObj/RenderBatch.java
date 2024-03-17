package com.shao.beautiful.gameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.shao.beautiful.config.GameConfig;

import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.scene.Updatable;
import net.mgsx.gltf.scene3d.shaders.PBRCommon;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentCache;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;

/**
 * Convient manager class for: model instances, animators, camera, environment,
 * lights, batch/shaderProvider
 * 
 * @author mgsx
 *
 */
public class RenderBatch implements Disposable {

	private final Array<RenderableProvider> renderableProviders = new Array<RenderableProvider>();

	private ModelBatch batch;
	private ModelBatch batchRef;
	private ModelBatch depthBatch;
	public SkyCubeBox skyBox;

	/** Shouldn't be null. */
	public Environment environment = new Environment();
	protected final EnvironmentCache computedEnvironement = new EnvironmentCache();

	public Camera camera;
	public Camera cameraRef;
	private RenderableSorter renderableSorter;

	private PointLightsAttribute pointLights = new PointLightsAttribute();
	private SpotLightsAttribute spotLights = new SpotLightsAttribute();

	public RenderBatch() {
		this(24);
	}

	public RenderBatch(int maxBones) {
		this(PBRShaderProvider.createDefault(maxBones), PBRShaderProvider.createDefault(maxBones),
				PBRShaderProvider.createDefaultDepth(maxBones));
	}

	public RenderBatch(ShaderProvider shaderProvider, ShaderProvider shaderProviderWater,
			DepthShaderProvider depthShaderProvider) {
		this(shaderProvider, shaderProviderWater, depthShaderProvider, new SceneRenderableSorter());
	}

	public RenderBatch(ShaderProvider shaderProvider, ShaderProvider shaderProviderWater,
			DepthShaderProvider depthShaderProvider, RenderableSorter renderableSorter) {
		this.renderableSorter = renderableSorter;

		batch = new ModelBatch(shaderProvider, renderableSorter);
		batchRef = new ModelBatch(shaderProviderWater, renderableSorter);
		depthBatch = new ModelBatch(depthShaderProvider);
		
		final Vector3 tmp=new Vector3();
		
		cameraRef = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
			@Override
			public void update() {
				update(true);
			}

			@Override
			public void update(boolean updateFrustum) {
				float aspect = viewportWidth / viewportHeight;
				projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect).scale(-1, 1, 1);
				view.setToLookAt(position, tmp.set(position).add(direction), up);
				combined.set(projection);
				Matrix4.mul(combined.val, view.val);
				if (updateFrustum) {
					invProjectionView.set(combined);
					Matrix4.inv(invProjectionView.val);
					frustum.update(invProjectionView);
				}
			}
		};

		float lum = 1f;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, lum, lum, lum, 1));
	}

	public ModelBatch getBatch() {
		return batch;
	}

	public void setBatch(ModelBatch batch) {
		this.batch = batch;
	}

	public void setShaderProvider(ShaderProvider shaderProvider) {
		batch.dispose();
		batch = new ModelBatch(shaderProvider, renderableSorter);
	}

	public void setDepthShaderProvider(DepthShaderProvider depthShaderProvider) {
		depthBatch.dispose();
		depthBatch = new ModelBatch(depthShaderProvider);
	}

	public void addScene(Scene scene) {
		addScene(scene, true);
	}

	public void addScene(Scene scene, boolean appendLights) {
		renderableProviders.add(scene);
		if (appendLights) {
			for (Entry<Node, BaseLight> e : scene.lights) {
				environment.add(e.value);
			}
		}
	}

	/**
	 * should be called in order to perform light culling, skybox update and
	 * animations.
	 * 
	 * @param delta
	 */
	public void update(float delta) {
		if (camera != null) {
			updateEnvironment();
			for (RenderableProvider r : renderableProviders) {
				if (r instanceof Updatable) {
					((Updatable) r).update(camera, delta);
				}
			}
			if (skyBox != null)
				skyBox.update(camera, delta);
		}

		begin();
		if (skyBox != null)
			batch.render(skyBox);
		end();
	}

	protected void updateEnvironment() {
		computedEnvironement.setCache(environment);
		pointLights.lights.clear();
		spotLights.lights.clear();
		if (environment != null) {
			for (Attribute a : environment) {
				if (a instanceof PointLightsAttribute) {
					pointLights.lights.addAll(((PointLightsAttribute) a).lights);
					computedEnvironement.replaceCache(pointLights);
				} else if (a instanceof SpotLightsAttribute) {
					spotLights.lights.addAll(((SpotLightsAttribute) a).lights);
					computedEnvironement.replaceCache(spotLights);
				} else {
					computedEnvironement.set(a);
				}
			}
		}
		cullLights();
	}

	protected void cullLights() {
		PointLightsAttribute pla = environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
		if (pla != null) {
			for (PointLight light : pla.lights) {
				if (light instanceof PointLightEx) {
					PointLightEx l = (PointLightEx) light;
					if (l.range != null && !camera.frustum.sphereInFrustum(l.position, l.range)) {
						pointLights.lights.removeValue(l, true);
					}
				}
			}
		}
		SpotLightsAttribute sla = environment.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
		if (sla != null) {
			for (SpotLight light : sla.lights) {
				if (light instanceof SpotLightEx) {
					SpotLightEx l = (SpotLightEx) light;
					if (l.range != null && !camera.frustum.sphereInFrustum(l.position, l.range)) {
						spotLights.lights.removeValue(l, true);
					}
				}
			}
		}
	}

	public void begin() {
		DirectionalLight light = getFirstDirectionalLight();
		DirectionalShadowLight shadowLight = (DirectionalShadowLight) light;

		environment.shadowMap = shadowLight;
		shadowLight.begin();
		depthBatch.begin(shadowLight.getCamera());

		PBRCommon.enableSeamlessCubemaps();
		computedEnvironement.shadowMap = environment.shadowMap;
		batch.begin(camera);

		cameraRef.up.set(camera.up).scl(1, -1, 1);
		cameraRef.direction.set(camera.direction).scl(1, -1, 1);
		cameraRef.position.set(camera.position.x, 2*GameConfig.gameSeaLevel - camera.position.y, camera.position.z);

		cameraRef.update();

		//batchRef.begin(cameraRef);
	}

	public void render(RenderableProvider r) {
		depthBatch.render(r);
		batch.render(r, computedEnvironement);
		//batchRef.render(r, computedEnvironement);
	}

	public void end() {
		DirectionalLight light = getFirstDirectionalLight();
		DirectionalShadowLight shadowLight = (DirectionalShadowLight) light;
		depthBatch.end();
		shadowLight.end();
		//batchRef.end();

		batch.end();

	}

	public DirectionalLight getFirstDirectionalLight() {
		DirectionalLightsAttribute dla = environment.get(DirectionalLightsAttribute.class,
				DirectionalLightsAttribute.Type);
		if (dla != null) {
			for (DirectionalLight dl : dla.lights) {
				if (dl instanceof DirectionalLight) {
					return (DirectionalLight) dl;
				}
			}
		}
		return null;
	}

	public void setSkyBox(SkyCubeBox skyBox) {
		this.skyBox = skyBox;
	}

	public void setAmbientLight(float lum) {
		environment.get(ColorAttribute.class, ColorAttribute.AmbientLight).color.set(lum, lum, lum, 1);
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void removeScene(Scene scene) {
		renderableProviders.removeValue(scene, true);
		for (Entry<Node, BaseLight> e : scene.lights) {
			environment.remove(e.value);
		}
	}

	public Array<RenderableProvider> getRenderableProviders() {
		return renderableProviders;
	}

	public void updateViewport(float width, float height) {
		if (camera != null) {
			camera.viewportWidth = width;
			camera.viewportHeight = height;
			camera.update(true);
		}
	}

	public int getActiveLightsCount() {
		return EnvironmentUtil.getLightCount(computedEnvironement);
	}

	public int getTotalLightsCount() {
		return EnvironmentUtil.getLightCount(environment);
	}

	@Override
	public void dispose() {
		batch.dispose();
		depthBatch.dispose();
		batchRef.dispose();
	}
}
