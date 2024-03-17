package com.shao.beautiful.manager.world;
import com.shao.beautiful.manager.WorldManager;
import com.shao.beautiful.gameObj.entities.Player;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.shao.beautiful.gameObj.RenderToolManager;
import com.shao.beautiful.manager.UIManager;

public class EntityManager {

    private WorldManager worldManager;
    private Player player;
    public EntityManager(WorldManager worldManager){
        this.worldManager=worldManager;
        this.player=new Player(this);
        player.setPosition(00,50,00);
    }
    public WorldManager getWorldManager(){
        return worldManager;
    }
    public Player getPlayer(){
        return player;
    }
    public void step(float delay){
        player.behavior(delay);
        
    }
    public void render(RenderToolManager m,UIManager ui){
        m.begin(RenderToolManager.RENDER_ENTITY);
        player.render(m);
        m.end();

    }
	public void destroy() {
		player.destroy();
	}
}
