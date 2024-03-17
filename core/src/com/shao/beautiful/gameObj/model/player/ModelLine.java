package com.shao.beautiful.gameObj.model.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.shao.beautiful.gameObj.model.EntityModel;
import com.shao.beautiful.gameObj.model.ModelBone;
import com.shao.beautiful.gameObj.model.ModelGroup;

public class ModelLine extends EntityModel{
	public ModelLine() {
		super();
		ModelBuilder modelBuilder = new ModelBuilder();
		Texture texture = new Texture(new Pixmap(64, 64, Pixmap.Format.RGBA8888));
		add(new ModelGroup("body", 0, 0, 0).add(new ModelBone(modelBuilder, -6, 0, -6, 12, 12, 12, texture, 0, 0)));
	}
	
}
