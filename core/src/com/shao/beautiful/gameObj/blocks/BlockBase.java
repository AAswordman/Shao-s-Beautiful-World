package com.shao.beautiful.gameObj.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.shao.beautiful.tools.TextureManager;

public abstract class BlockBase {
	public abstract boolean shaderOpen();
	public abstract boolean dynamicUV();
	
	public abstract Material getMaterial();
	public abstract int getFrames();
	public static Texture getBlockTexture(String pic) {
		return TextureManager.get(Gdx.files.internal("block/"+pic));
	}
}
