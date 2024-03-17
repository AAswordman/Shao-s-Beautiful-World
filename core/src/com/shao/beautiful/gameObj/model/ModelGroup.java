package com.shao.beautiful.gameObj.model;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.ModelBatchEnvironment;
import com.shao.beautiful.gameObj.RenderToolManager;

public class ModelGroup {
	public String name;
	public ArrayList<ModelBone> bones;
	public ArrayList<ModelGroup> groups;
	public final Vector3 center = new Vector3();

	public ModelGroup(String name, float x, float y, float z) {
		bones = new ArrayList<>();

		groups = new ArrayList<>();
		this.name = name;
		this.center.set(x, y, z);

	}

	public ModelGroup(String s, Vector3 v) {
		bones = new ArrayList<>();
		groups = new ArrayList<>();
		this.name = s;
		this.center.set(v);
	}

	public ModelGroup add(ModelBone m) {
		bones.add(m);
		return this;
	}

	public ModelGroup add(ModelGroup m) {
		groups.add(m);
		return this;
	}

	public void render(RenderToolManager batch) {
		for (ModelGroup m : groups) {
			m.render(batch);
		}
		for (ModelBone b : bones) {
			batch.render(b.get());

		}

	}

	public ModelGroup copy() {
		ModelGroup m = new ModelGroup(name, center);
		for (ModelGroup g : groups) {
			m.add(g.copy());
		}
		for (ModelBone b : bones) {
			m.add(b.copy());
		}
		return m;
	}

	public void setPosition(float x, float y, float z) {
		for (ModelGroup g : groups) {
			g.setPosition(x, y, z);
		}
		for (ModelBone b : bones) {
			b.setPosition(x, y, z);
		}
	}

	public void addPosition(float x, float y, float z) {
		for (ModelGroup g : groups) {
			g.addPosition(x, y, z);
		}
		for (ModelBone b : bones) {
			b.addPosition(x, y, z);
		}
	}

	public void rotate(Vector3 center2, Vector3 degree) {
		for (ModelGroup g : groups) {
			g.rotate(center2, degree);
		}
		for (ModelBone b : bones) {
			b.rotate(center2, degree);
		}
		
	}

	public void addPosition(Vector3 pos) {
		// TODO Auto-generated method stub
		for (ModelGroup g : groups) {
			g.addPosition(pos);
		}
		for (ModelBone b : bones) {
			b.addPosition(pos);
		}
	}
}
