package com.shao.beautiful.gameObj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class NormalShaderProvider {
    
	private Config config;
	public NormalShaderProvider() {
	}
	protected Shader createShader(Renderable renderable) {
        
		return new NormalShader(renderable, config);
	}
}
