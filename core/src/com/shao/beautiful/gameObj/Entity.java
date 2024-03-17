package com.shao.beautiful.gameObj;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.manager.world.EntityManager;
import com.shao.beautiful.gameObj.ani_controller.AnimaltionCTLBase;
import com.shao.beautiful.gameObj.ani_controller.RootAnimaltionCTL;
import com.shao.beautiful.gameObj.model.EntityModel;
import com.shao.beautiful.gameObj.model.ModelBone;
import com.shao.beautiful.gameObj.model.ModelGroup;
import bms.helper.tools.Mathbms;
import com.shao.beautiful.tools.LruCache;
import com.shao.beautiful.tools.MathPatch;

import bms.helper.tools.ArrayListSafe;
import java.util.ArrayList;

public class Entity extends GameObj {
	public Vector3 acceleration = new Vector3();
	public Vector3 maximumTraction = new Vector3(10, 0, 10);
	public Vector3 speedActive = new Vector3();
	// public Vector3 speedPassive = new Vector3();
	public Vector3 speed = new Vector3();
	protected EntityManager entityManager;
	protected CollisionBox box;
	protected EntityModel model;

	private LruCache<String, CollisionBox> blockCache = new LruCache<>(20);
	private ArrayList<AnimaltionCTLBase> aniCtlList = new ArrayList<AnimaltionCTLBase>();
	public EntityQuery query;

	public Entity(EntityManager entityManager) {
		this.entityManager = entityManager;
		query = new EntityQuery();
		addAnimaltionCTL(new RootAnimaltionCTL(query));
	}

	public void behavior(float delay) {
		Vector3 lastSpeedVector3 = speed.cpy();
		physicsMove(delay, 5, 5);
		acceleration = speed.cpy().sub(lastSpeedVector3).scl(1f / delay);
		upDateQuery(delay);
		for (AnimaltionCTLBase animaltionCTLBase : aniCtlList) {
			animaltionCTLBase.step(delay);
		}

	}

	private final Vector3 tmpV = new Vector3();
	private final Vector3 tmpP = new Vector3();
	private final Vector3 tmpQ = new Vector3();

	private void physicsMove(float delay, int times, int maxtimes) {
		if (times < 0) {

			return;
		}

		acceleration.setZero();
		// 设置空气阻力加速度
		float k = 0.06f;
		acceleration.x = (float) ((speed.x > 0 ? -1 : 1) * Math.pow(speed.x, 2) * k);
		acceleration.y = (float) ((speed.y > 0 ? -1 : 1) * Math.pow(speed.y, 2) * k);
		acceleration.z = (float) ((speed.z > 0 ? -1 : 1) * Math.pow(speed.z, 2) * k);

		// 设置重力加速度
		acceleration.y -= 10;

		// 加速度增加速度
		speed.add(acceleration.scl(delay));

		// 主动牵引力修改速度
		speed.sub(MathPatch.limit(tmpP.set(maximumTraction).scl(-delay), tmpV.set(speed).sub(speedActive),
				tmpQ.set(maximumTraction).scl(delay)));

		// 方块弹力瞬间加速度
		ArrayListSafe<CollisionBox> boxes = new ArrayListSafe<>(6);
		getRangBlock(boxes, box);

		for (CollisionBox c : boxes) {
			if (c == null)
				continue;
			if (c == nullBlockCollision) {
				//c.generateResistanceOnlySpeed(box, speed);
				speed.x=0;
				speed.y=0;
				speed.z=0;
				continue;
			}
			if (c.isContact(box)) {
				// c.generateResistance(box, speedPassive);

				c.generateResistance(box, speed);
				// 确保不会相撞
				if (c.isCrashing(box)) {
					System.out.println(c);
					System.out.println(box);
					throw new RuntimeException("Error crashing");
				}
			}
		}

		// 判断未来的时间中会不会有力突变，如果有则分段计算
		CollisionBox simulatePos = box.copy();
		CollisionBox lastSimulatePos = box.copy();
		final Vector3 stepPos = new Vector3();

		// T/(T*V/0.5)=0.5/Vmax
		float stepTime = (float) (0.3 / Math.max(Math.max(Math.abs(speed.x), Math.abs(speed.y)), Math.abs(speed.z)));
		float time = 0;
		float lastTime = 0;

		float actTime = 0;
		while (time < delay) {
			time = Math.min(time + stepTime, delay);
			float useTime = time - lastTime;
			simulatePos.position.add(stepPos.set(speed).scl(useTime));
			boxes = new ArrayListSafe<>(6);
			getRangBlock(boxes, simulatePos);

			float earliestCrashingTime = 999;
			for (CollisionBox c : boxes) {
				// air
				if (c == null)
					continue;
				
				float[] crashingTime = new float[] { 0, 0, 0 };
				if (simulatePos.isCrashing(c)) {
					if (Math.abs(lastSimulatePos.position.x - c.position.x) > c.getHalfWidth()
							+ lastSimulatePos.getHalfWidth()) {
						// (x动-x静-r1-r2)/-Vx=碰撞时刻
						crashingTime[0] = (Math.abs(lastSimulatePos.position.x - c.position.x)
								- lastSimulatePos.getHalfWidth() - c.getHalfWidth()) / (-speed.x);
					}

					if (Math.abs(lastSimulatePos.position.z - c.position.z) > c.getHalfWidth()
							+ lastSimulatePos.getHalfWidth()) {

						crashingTime[2] = (Math.abs(lastSimulatePos.position.z - c.position.z)
								- lastSimulatePos.getHalfWidth() - c.getHalfWidth()) / (-speed.z);
					}

					if (Math.abs(lastSimulatePos.position.y + lastSimulatePos.getHalfHeight() - c.position.y
							- c.getHalfHeight()) > c.getHalfHeight() + lastSimulatePos.getHalfHeight()) {

						crashingTime[1] = (Math.abs(lastSimulatePos.position.y + lastSimulatePos.getHalfHeight()
								- c.position.y - c.getHalfHeight()) - lastSimulatePos.getHalfHeight()
								- c.getHalfHeight()) / (-speed.y);
					}
					// System.out.println(crashingTime.toString());
					earliestCrashingTime = Math.min(earliestCrashingTime,
							Math.max(crashingTime[2], Math.max(crashingTime[0], crashingTime[1])));
				}
			}
			if (earliestCrashingTime != 999) {
				actTime = lastTime + earliestCrashingTime;
				break;
			}
			lastTime = time;
			lastSimulatePos = simulatePos.copy();
		}

		// 速度改变位移
		if (actTime == 0) {
			box.position.add(speed.cpy().scl(delay));
		} else {
			box.position.add(speed.cpy().scl(actTime));
			MathPatch.vec3AccuracyFix(box.position);
			physicsMove(delay - actTime, times - 1, maxtimes);
		}
	}

	public void getRangBlock(ArrayList<CollisionBox> arr, CollisionBox usebox) {
		Vector3[] vec = usebox.getArea();
		for (int x = (int) Math.floor(vec[0].x - 1); x <= vec[1].x + 1; x++) {
			for (int y = (int) Math.floor(vec[0].y - 1); y <= vec[1].y + 1; y++) {
				for (int z = (int) Math.floor(vec[0].z - 1); z <= vec[1].z + 1; z++) {
					CollisionBox b = getBlockCollisionBox(x, y, z);
					if (b == nullBlockCollision) {
						arr.add(b);
						continue;
					}
					if (arr.indexOf(b) == -1 && usebox.isContact(b)) {
						arr.add(b);
					}
				}
			}
		}
	}

	public static final CollisionBox nullBlockCollision = new CollisionBox();

	public CollisionBox getBlockCollisionBox(int x, int y, int z) {
		String key = x + "|" + y + "|" + z;
		if (blockCache.containsKey(key)) {
			return blockCache.get(key);
		}
		Block b = entityManager.getWorldManager().loader.getBlock(x, y, z);
		if (b.id == Block.BlockTexture.NULL.id) {
			return nullBlockCollision;
		}
		if (!BlockNature.isCanPass(b)) {
			CollisionBox box = b.getCollisionBox(x, y, z);
			blockCache.put(key, box);
			return box;
		}
		return null;
	}

	public void render(RenderToolManager batch) {
		ModelGroup e = model.copy();

		process(e);
		e.addPosition(box.position.x, box.position.y, box.position.z);

		e.render(batch);
	}

	private void process(ModelGroup g) {
		Vector3 degree = getBoneRot(g.name);
		Vector3 pos = getBonePos(g.name);
		g.rotate(g.center, degree);
		g.addPosition(pos);
		for (ModelGroup e : g.groups) {
			process(e);
		}

	}

	public void setPosition(float x, float y, float z) {
		box.position.set(x, y, z);
	}

	public Vector3 getPosition() {
		return box.position;
	}

	public void addAnimaltionCTL(AnimaltionCTLBase a) {
		this.aniCtlList.add(a);
	}

	public Vector3 getBoneRot(String b) {
		final Vector3 tempVector3 = new Vector3();
		for (AnimaltionCTLBase animaltionCTLBase : aniCtlList) {
			tempVector3.add(animaltionCTLBase.getRot(b));
		}

		return tempVector3;
	}

	public Vector3 getBonePos(String b) {
		final Vector3 tempVector3 = new Vector3();
		for (AnimaltionCTLBase animaltionCTLBase : aniCtlList) {
			tempVector3.add(animaltionCTLBase.getPos(b));
		}
		return tempVector3;
	}

	public void upDateQuery(float delay) {
		query.modified_move_speed = acceleration.len();
		query.gliding_speed_value = 1;
		query.modified_distance_moved += tmpV.set(speed).scl(delay).len();

		query.delta_time = delay;
		query.ground_speed = speed.len();
	}

	public void destroy() {

	}
}
