package com.shao.beautiful.gameObj.ani_controller;

import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.EntityQuery;
import com.shao.beautiful.tools.MathPatch;

public class WalkNormalAnimaltionCTL extends AnimaltionCTLBase {

	public WalkNormalAnimaltionCTL(EntityQuery query) {
		super(query);

	}

	@Override
	public void step(float delay) {
		super.step(delay);
		float rot = (float) ((MathPatch.cos(query.modified_distance_moved * 80.17)) * 
            Math.min(query.ground_speed, 3f) * 12);
		setRot("leftArm", new Vector3(rot, 0, 0));
		setRot("leftLeg", new Vector3(-rot * 1.4f, 0, 0));
		setRot("rightArm", new Vector3(-rot, 0, 0));
		setRot("rightLeg", new Vector3(rot * 1.4f, 0, 0));
	}
}
