package com.shao.beautiful.manager;
import com.shao.beautiful.manager.world.MapLoader;
import com.shao.beautiful.manager.world.DataSaver;
import com.shao.beautiful.manager.world.MapRenderer;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import bms.helper.tools.TimeDelayer;
import bms.helper.tools.LOG;
import bms.helper.app.EXPHelper;
import com.shao.beautiful.manager.world.EntityManager;
import com.shao.beautiful.gameObj.RenderToolManager;

public class WorldManager implements StepAble {
    public MapLoader loader;
    public DataSaver saver;
    public MapRenderer renderer;
    public EntityManager entities;
    public TimeDelayer timeDelayer=new TimeDelayer(100);
	RenderToolManager batch;
    @Override
    public void step(float delay) {
        entities.step(delay);
    }
    public WorldManager() {
        loader = new MapLoader(this);
        saver = new DataSaver(this);
        renderer = new MapRenderer(this);
        entities=new EntityManager(this);
    }
    
    public void render(RenderToolManager batch, UIManager ui) {
        //LOG.print("时间间隔（开始渲染）",timeDelayer.GetDelay()+"");
        renderer.render(batch, ui);
        entities.render(batch,ui);
        this.batch=batch;
        //LOG.print("时间间隔（结束渲染）",timeDelayer.GetDelay()+"");
        
        //LOG.print("时间间隔（结束渲染2）",timeDelayer.GetDelay()+"");
    }

    public void save() {
        saver.save();
    }
    public float getStandardLong() {
        return 0;
    }
    public void destroy() {
        loader.destroy();
        saver.destroy();
        entities.destroy();
        renderer.destroy();
    }
}
