package com.shao.beautiful.gameObj.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.shao.beautiful.tools.TextureManager;

import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class BlockWater extends BlockBase {
	private Material material;
	//private Texture normalTexture;
	public BlockWater(){
		Texture normalTexture = getBlockTexture("water_still.png");
		material=new Material();
		//material.set(PBRTextureAttribute.createSpecular(new Texture("cubemap/cubemap_2.png")));
		material.set(PBRTextureAttribute.createNormalTexture(normalTexture));
		material.set(PBRColorAttribute.createBaseColorFactor(new Color(0x99ddff77)));
		Pixmap pixmap=new Pixmap(16,16,Pixmap.Format.RGBA8888);
		pixmap.setColor(0xbb5522ff);
		pixmap.fill();
		material.set(PBRTextureAttribute.createOcclusionTexture(new Texture(pixmap)));
		material.set(PBRTextureAttribute.createMetallicRoughnessTexture(new Texture(pixmap)));
		material.set(new BlendingAttribute(true,1f));
		//System.out.println(getBlockTexture("water_still.png").getHeight());
	}
	@Override
	public boolean shaderOpen() {
		return true;
	}

	@Override
	public boolean dynamicUV() {
		return true;
	}
	@Override
	public Material getMaterial() {
		return material;
	
	}
	@Override
	public int getFrames() {
		return 32;
	}

}
