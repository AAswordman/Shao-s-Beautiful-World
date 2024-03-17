package com.shao.beautiful.gameObj.ani_controller;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.EntityQuery;

public class AnimaltionCTLBase {
	protected EntityQuery query;
	protected final HashMap<String, Vector3> boneRotMap = new HashMap<String, Vector3>();
	protected final HashMap<String, Vector3> bonePosMap = new HashMap<String, Vector3>();

	public AnimaltionCTLBase(EntityQuery query) {
		this.query = query;
	}

	public void step(float delay) {
		this.boneRotMap.clear();
		this.bonePosMap.clear();
		
	}
	public void setRot(String b, Vector3 r) {
		if (boneRotMap.containsKey(b)) {
			boneRotMap.put(b, boneRotMap.get(b).add(r));
		} else {
			boneRotMap.put(b, r);
		}
	}
	public void setPos(String b, Vector3 r) {
		if (bonePosMap.containsKey(b)) {
			bonePosMap.put(b, bonePosMap.get(b).add(r));
		} else {
			bonePosMap.put(b, r);
		}
	}
	public Vector3 getPos(String s) {
		if (bonePosMap.containsKey(s)) {
			return bonePosMap.get(s);
		}
		return Vector3.Zero;
	}
	public Vector3 getRot(String s) {
		if (boneRotMap.containsKey(s)) {
			return boneRotMap.get(s);
		}
		return Vector3.Zero;
	}
}
