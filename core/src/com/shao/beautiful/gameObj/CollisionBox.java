package com.shao.beautiful.gameObj;

import java.math.BigDecimal;

import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.tools.MathPatch;

public class CollisionBox {
	public Vector3 position;
	private float h;
	private float w;

	public CollisionBox(float x, float y, float z, float w, float h) {
		this.position = new Vector3(x, y, z);
		this.w = w;
		this.h = h;
	}

	public CollisionBox() {
		this.position = new Vector3(0, 0, 0);
	}

	// 返回低点和高点
	public Vector3[] getXremoveArea() {
		return new Vector3[] { new Vector3(position.x - w / 2, position.y, position.z + w / 2),
				new Vector3(position.x - w / 2, position.y + h, position.z + w / 2) };
	}

	public Vector3[] getXaddArea() {
		return new Vector3[] { new Vector3(position.x + w / 2, position.y, position.z + w / 2),
				new Vector3(position.x + w / 2, position.y + h, position.z + w / 2) };
	}

	public Vector3[] getZremoveArea() {
		return new Vector3[] { new Vector3(position.x - w / 2, position.y, position.z - w / 2),
				new Vector3(position.x + w / 2, position.y + h, position.z - w / 2) };
	}

	public Vector3[] getZaddArea() {
		return new Vector3[] { new Vector3(position.x - w / 2, position.y, position.z + w / 2),
				new Vector3(position.x + w / 2, position.y + h, position.z + w / 2) };
	}

	public Vector3[] getYremoveArea() {
		return new Vector3[] { new Vector3(position.x - w / 2, position.y, position.z - w / 2),
				new Vector3(position.x + w / 2, position.y, position.z + w / 2) };
	}

	public Vector3[] getYaddArea() {
		return new Vector3[] { new Vector3(position.x - w / 2, position.y + h, position.z - w / 2),
				new Vector3(position.x + w / 2, position.y + h, position.z + w / 2) };
	}

	public Vector3[] getArea() {
		return new Vector3[] {
				MathPatch.vec3AccuracyFix(new Vector3(position.x - w / 2, position.y, position.z - w / 2)),
				MathPatch.vec3AccuracyFix(new Vector3(position.x + w / 2, position.y + h, position.z + w / 2)) };
	}

	public Vector3 getCenter() {
		return new Vector3(position.x, position.y + getHalfHeight(), position.z);
	}

	public boolean isCrashing(CollisionBox box) {
		Vector3[] a = getArea();
		Vector3[] b = box.getArea();

		return a[0].x < b[1].x && a[0].y < b[1].y && a[0].z < b[1].z && a[1].x > b[0].x && a[1].y > b[0].y
				&& a[1].z > b[0].z;
	}

	public boolean isContact(CollisionBox box) {
		if (box == null) {
			return false;
		}

		Vector3[] a = getArea();
		Vector3[] b = box.getArea();

		return a[0].x <= b[1].x && a[0].y <= b[1].y && a[0].z <= b[1].z && a[1].x >= b[0].x && a[1].y >= b[0].y
				&& a[1].z >= b[0].z;
	}

	// use to Block
	/*
	 * public void generateResistance(CollisionBox box){ if(!isCrashing(box))return
	 * ; Vector3 pos=box.position; Vector3 target=box.getCenter(); Vector3
	 * center=getCenter();
	 * 
	 * float
	 * max=Math.max(Math.max(Math.abs(target.x-center.x),Math.abs(target.y-center.y)
	 * ),Math.abs(target.z-center.z));
	 * 
	 * if(Math.abs(target.x-center.x)==max){
	 * pos.x=target.x-center.x>0?Math.max(pos.x,center.x+getHalfWidth()+box.
	 * getHalfWidth()):Math.min(pos.x,center.x-getHalfWidth()-box.getHalfWidth()); }
	 * 
	 * if(Math.abs(target.y-center.y)==max){
	 * pos.y=target.y-center.y>0?Math.max(pos.y,center.y+getHalfHeight()):Math.min(
	 * pos.y,center.y-getHalfHeight()-box.h); }
	 * 
	 * if(Math.abs(target.z-center.z)==max){
	 * pos.z=target.z-center.z>0?Math.max(pos.z,center.z+getHalfWidth()+box.
	 * getHalfWidth()):Math.min(pos.z,center.z-getHalfWidth()-box.getHalfWidth()); }
	 * 
	 * }
	 */
	public void generateResistance(CollisionBox box, Vector3 speed) {
		Vector3 target = box.getCenter();
		Vector3 center = getCenter();
		Vector3 pos = box.position;
		float dx = MathPatch.floatAccuracyFix((target.x - center.x) / (getHalfWidth() + box.getHalfWidth())),
				dz = MathPatch.floatAccuracyFix((target.z - center.z) / (getHalfWidth() + box.getHalfWidth())),
				dy = MathPatch.floatAccuracyFix((target.y - center.y) / (getHalfHeight() + box.getHalfHeight()));

		float max = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));

		// 角落不受力

		if (Math.abs(dy) == max) {

			speed.y = dy > 0 ? Math.max(speed.y, 0) : Math.min(speed.y, 0);
			pos.y = dy > 0 ? Math.max(pos.y, center.y + getHalfHeight())
					: Math.min(pos.y, center.y - getHalfHeight() - box.h);
			MathPatch.vec3AccuracyFix(pos);
			return;
		}
		if (Math.abs(dx) == max) {
			speed.x = dx > 0 ? Math.max(speed.x, 0) : Math.min(speed.x, 0);
			pos.x = dx > 0 ? Math.max(pos.x, center.x + getHalfWidth() + box.getHalfWidth())
					: Math.min(pos.x, center.x - getHalfWidth() - box.getHalfWidth());
			MathPatch.vec3AccuracyFix(pos);

			return;
		}

		if (Math.abs(dz) == max) {
			speed.z = dz > 0 ? Math.max(speed.z, 0) : Math.min(speed.z, 0);
			pos.z = dz > 0 ? Math.max(pos.z, center.z + getHalfWidth() + box.getHalfWidth())
					: Math.min(pos.z, center.z - getHalfWidth() - box.getHalfWidth());
			MathPatch.vec3AccuracyFix(pos);
			return;
		}

		// MathPatch.vec3AccuracyFix(pos);

	}

	public CollisionBox set(CollisionBox box) {
		position = box.position.cpy();
		h = box.h;
		w = box.w;
		return this;
	}

	public CollisionBox set(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
		return this;
	}

	public CollisionBox copy() {
		return new CollisionBox().set(this);
	}

	public float getHalfWidth() {
		return w / 2;
	}

	public float getHalfHeight() {
		return h / 2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof CollisionBox) {
			CollisionBox another = (CollisionBox) obj;
			return position.equals(another.position) && w == another.w && h == another.h;
		}
		return false;
	}

	@Override
	public String toString() {
		Vector3[] vector3 = getArea();
		return "[" + position.toString() + "//w=" + w + "/h=" + h + "]," + vector3[0] + vector3[1];
	}

	public CollisionBox toAccuracy3() {
		return set(toAccuracy3(position.x), toAccuracy3(position.y), toAccuracy3(position.z));
	}

	public float toAccuracy3(float f) {
		return Math.round(f * 1000) / 1000f;
	}

	public float toAccuracy2(float f) {
		return Math.round(f * 1000) / 1000f;
	}

	public CollisionBox toAccuracy2() {
		return set(toAccuracy2(position.x), toAccuracy2(position.y), toAccuracy2(position.z));
	}

	public void testCrashing(CollisionBox box, Vector3 speed) {
		// TODO Auto-generated method stub

		Vector3 target = box.getCenter();
		Vector3 center = getCenter();
		Vector3 pos = box.position;
		float dx = (target.x - center.x) / (getHalfWidth() + box.getHalfWidth()),
				dz = (target.z - center.z) / (getHalfWidth() + box.getHalfWidth()),
				dy = (target.y - center.y) / (getHalfHeight() + box.getHalfHeight());

		float max = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));

		if (Math.abs(dx) == max) {
			speed.x = dx > 0 ? Math.max(speed.x, 0) : Math.min(speed.x, 0);

		}
		if (Math.abs(dy) == max) {
			speed.y = dy > 0 ? Math.max(speed.y, 0) : Math.min(speed.y, 0);

		}
		if (Math.abs(dz) == max) {
			speed.z = dz > 0 ? Math.max(speed.z, 0) : Math.min(speed.z, 0);

		}
		pos.x = dx > 0 ? Math.max(pos.x, center.x + getHalfWidth() + box.getHalfWidth())
				: Math.min(pos.x, center.x - getHalfWidth() - box.getHalfWidth());
		pos.y = dy > 0 ? Math.max(pos.y, center.y + getHalfHeight())
				: Math.min(pos.y, center.y - getHalfHeight() - box.h);
		pos.z = dz > 0 ? Math.max(pos.z, center.z + getHalfWidth() + box.getHalfWidth())
				: Math.min(pos.z, center.z - getHalfWidth() - box.getHalfWidth());
		MathPatch.vec3AccuracyFix(pos);

	}

	public void generateResistanceOnlySpeed(CollisionBox box, Vector3 speed) {
		Vector3 target = box.getCenter();
		Vector3 center = getCenter();
		float dx = (target.x - center.x) / (getHalfWidth() + box.getHalfWidth()),
				dz = (target.z - center.z) / (getHalfWidth() + box.getHalfWidth()),
				dy = (target.y - center.y) / (getHalfHeight() + box.getHalfHeight());

		float max = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));

		if (Math.abs(dx) == max) {
			speed.x = dx > 0 ? Math.max(speed.x, 0) : Math.min(speed.x, 0);

		}
		if (Math.abs(dy) == max) {
			speed.y = dy > 0 ? Math.max(speed.y, 0) : Math.min(speed.y, 0);

		}
		if (Math.abs(dz) == max) {
			speed.z = dz > 0 ? Math.max(speed.z, 0) : Math.min(speed.z, 0);

		}
		
	}

}
