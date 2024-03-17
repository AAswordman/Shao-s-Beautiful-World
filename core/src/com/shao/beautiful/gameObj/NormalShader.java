package com.shao.beautiful.gameObj;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import bms.helper.tools.LOG;
import com.badlogic.gdx.graphics.GL20;

public class NormalShader extends DefaultShader {

    private ShaderProgram shader;
    /*
    //our constants...
    public static final float DEFAULT_LIGHT_Z = 0.075f;
    public static final float AMBIENT_INTENSITY = 0.2f;
    public static final float LIGHT_INTENSITY = 1f;

    public static final Vector3 LIGHT_POS = new Vector3(3f,30f,DEFAULT_LIGHT_Z);

    //Light RGB and intensity (alpha)
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.8f, 0.6f);

    //Ambient RGB and intensity (alpha)
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.6f, 0.6f, 1f);

    //Attenuation coefficients for light falloff
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);
    */
	
	public NormalShader(Renderable renderable, Config config) {
		super(renderable, config);
		
		//init();
		// TODO Auto-generated constructor stub
        shader=super.program;
        
        //ensure it compiled
        if (!shader.isCompiled()){
            LOG.print("shader",shader.getLog());
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());
        }
            //print any warnings
        if (shader.getLog().length()!=0)
            LOG.print("shader",shader.getLog());
        
	}
    
    
    
    @Override
    public void render(Renderable renderable) {
        super.render(renderable);
    }

    
    
	

}
