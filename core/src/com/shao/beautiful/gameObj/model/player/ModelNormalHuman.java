package com.shao.beautiful.gameObj.model.player;

import com.shao.beautiful.gameObj.model.EntityModel;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.shao.beautiful.gameObj.model.ModelGroup;
import com.shao.beautiful.gameObj.model.ModelBone;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class ModelNormalHuman extends EntityModel {
	public ModelNormalHuman() {
		super();
		ModelBuilder modelBuilder = new ModelBuilder();
		Texture texture = new Texture(Gdx.files.internal("intentions_first2.png"));
		add(new ModelGroup("body", -6, 15, 1).add(new ModelBone(modelBuilder, -4, 12, -2, 8, 12, 4, texture, 16, 16))
				.add(new ModelGroup("head", 0, 24, 0)
						.add(new ModelBone(modelBuilder, -4, 24, -4, 8, 8, 8, texture, 0, 0)))
				.add(new ModelGroup("leftArm", 5, 22, 0)
						.add(new ModelBone(modelBuilder, -8, 12, -2, 4, 12, 4, texture, 40, 16)))
				.add(new ModelGroup("rightArm", 5, 22, 0)
						.add(new ModelBone(modelBuilder, 4, 12, -2, 4, 12, 4, texture, 32, 48)))
				.add(new ModelGroup("leftLeg", 2, 12, 0)
						.add(new ModelBone(modelBuilder, 0, 0, -2, 4, 12, 4, texture, 16, 48)))
				.add(new ModelGroup("rightLeg", 5, 12, 0)
						.add(new ModelBone(modelBuilder, -4, 0, -2, 4, 12, 4, texture, 0, 16))));
	}
}
