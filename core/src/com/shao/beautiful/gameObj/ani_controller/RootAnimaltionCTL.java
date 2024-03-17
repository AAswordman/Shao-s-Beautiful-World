package com.shao.beautiful.gameObj.ani_controller;

import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.EntityQuery;

public class RootAnimaltionCTL extends AnimaltionCTLBase {
	public RootAnimaltionCTL(EntityQuery query) {
		super(query);
	}
	@Override
	public void step(float delay) {
		super.step(delay);
		setRot("__root__", new Vector3(query.body_x_rotation,query.body_y_rotation,0));
		//setPos("__root__", new Vector3(0,query.body_y_rotation,0));
	}
}
