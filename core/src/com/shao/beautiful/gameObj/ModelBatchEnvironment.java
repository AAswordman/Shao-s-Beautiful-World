package com.shao.beautiful.gameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.tools.EnvironmentCubemap;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class ModelBatchEnvironment extends ModelBatch {
    private DirectionalShadowLight shadowLight;
    private Environment environment;

    private ModelBatch shadowBatch;
    
    private EnvironmentCubemap envCubemap;

    
    private ShaderProgram effectShader;
    
    private FrameBuffer fbo;

    private SceneManager manager;
    public ModelBatchEnvironment(){
        super(getPbrShaderProvider());
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));//环境光
        environment.add((shadowLight= new DirectionalShadowLight(8192, 8192, 80f, 80f, .1f, 80f)).set(0.9f, 0.9f, 0.45f, -1f, -0.8f, -0.2f));//直线光源
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.8f, 0.95f, 1f, 1.0f));
        
        environment.shadowMap = shadowLight;
        /*
        envCubemap = new EnvironmentCubemap(new Pixmap(Gdx.files.internal("cubemap/cubemap_2.png")),
                                            new Pixmap(Gdx.files.internal("cubemap/cubemap_0.png")),
                                            new Pixmap(Gdx.files.internal("cubemap/cubemap_4.png")),
                                            new Pixmap(Gdx.files.internal("cubemap/cubemap_5.png")),
                                            new Pixmap(Gdx.files.internal("cubemap/cubemap_1.png")),
                                            new Pixmap(Gdx.files.internal("cubemap/cubemap_3.png")));
        
        
        */
        Cubemap cubemap = new Cubemap(Gdx.files.internal("cubemap/cubemap_2.png"), 
                                      Gdx.files.internal("cubemap/cubemap_0.png"), 
                                      Gdx.files.internal("cubemap/cubemap_4.png"), 
                                      Gdx.files.internal("cubemap/cubemap_5.png"), 
                                      Gdx.files.internal("cubemap/cubemap_1.png"), 
                                      Gdx.files.internal("cubemap/cubemap_3.png"));
        environment.set(new PBRCubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap));
        
        //normalBatch=new ModelBatch();
        // post processing
        shadowBatch=new ModelBatch(new DepthShaderProvider());
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
            
		
        
    }
    public static PBRShaderProvider getPbrShaderProvider(){
        PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
        config.numBones = 32;
        config.numDirectionalLights = 1;
        config.numPointLights = 0;
		config.numSpotLights = 0;
        
        return new PBRShaderProvider(config);
    }
    @Override
    public void render(RenderableProvider renderable) {
        shadowBatch.render(renderable);
        
        super.render(renderable,environment);
    }
    private void setEffectUniforms() {
        // template
        effectShader.setUniformf("u_effectLevel", 0.9f);
	}
    public void renderWithOutShadow(RenderableProvider renderable){
        shadowBatch.render(renderable);
        super.render(renderable);
    }
    
    @Override
    public void begin(Camera cam) {
        float delta = Gdx.graphics.getDeltaTime();
		manager.update(delta);
        //envCubemap.render(cam);
        //sceneManager.update(Gdx.graphics.getDeltaTime());
        
        shadowLight.update(cam);
        shadowLight.begin(cam.position, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
        //normalBatch.begin(cam);
        //super.begin(cam);
        
    }
    @Override
    public void render(RenderableProvider renderableProvider, Shader shader) {
    	shadowBatch.render(renderableProvider);
    	//super.render(renderableProvider, shader);
        manager.getRenderableProviders().add(renderableProvider);
    }
    @Override
    public void render(RenderableProvider renderableProvider, Environment environment, Shader shader) {
    	shadowBatch.render(renderableProvider);
        //super.render(renderableProvider,this.environment);
    	//normalBatch.render(renderableProvider,this.environment);
        manager.getRenderableProviders().add(renderableProvider);
        
    }
    
    @Override
    public void end() {
        shadowBatch.end();
        
        shadowLight.end();
        //super.end();
        manager.render();
        manager.getRenderableProviders().clear();
        
    }
    
}
