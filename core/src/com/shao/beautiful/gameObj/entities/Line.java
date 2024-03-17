package com.shao.beautiful.gameObj.entities;

import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.CollisionBox;
import com.shao.beautiful.gameObj.Entity;
import com.shao.beautiful.gameObj.RenderToolManager;
import com.shao.beautiful.gameObj.model.ModelGroup;
import com.shao.beautiful.gameObj.model.player.ModelLine;
import com.shao.beautiful.manager.world.EntityManager;

import bms.helper.tools.StackWithMax;

public class Line extends Entity {
	public StackWithMax<LineData> datas = new StackWithMax<Line.LineData>(20);
	private Vector3 lastSpeed=new Vector3();
	private Vector3 lastPos=new Vector3();
	public Line(EntityManager entityManager) {
		super(entityManager);
		model = new ModelLine();
		box = new CollisionBox(0, 0, 0, 0.6f, 0.6f);
		maximumTraction = new Vector3(9999,0,9999);
	}
	@Override
	public void render(RenderToolManager batch) {
		super.render(batch);
		ModelGroup mg=model.copy();
		for (LineData lineData : datas) {
			
			mg.setPosition(lineData.pos.x, lineData.pos.y, lineData.pos.z);
			mg.render(batch);
		}
		
	}
	private final Vector3 tmpV=new Vector3();
	@Override
	public void behavior(float delay) {
		super.behavior(delay);
		
		if(!lastSpeed.equals(speed)) {
			datas.push(new LineData(box.position));
			lastSpeed.set(speed);
		}
		if (tmpV.set(box.position).sub(lastPos).len()>1) {
			datas.push(new LineData(box.position));
			lastPos.set(box.position);
		}
	}
	public static class LineData{
		public Vector3 pos;
		public LineData(Vector3 v){
			pos=new Vector3().set(v);
		}
	}
}
