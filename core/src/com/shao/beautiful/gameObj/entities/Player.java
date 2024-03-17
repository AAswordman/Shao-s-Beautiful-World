package com.shao.beautiful.gameObj.entities;
import com.shao.beautiful.gameObj.Entity;
import com.shao.beautiful.gameObj.ani_controller.WalkNormalAnimaltionCTL;
import com.shao.beautiful.manager.world.EntityManager;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.CollisionBox;
import com.shao.beautiful.gameObj.model.player.ModelNormalHuman;

public class Player extends Entity{
    public Player(EntityManager e){
        super(e);
        box=new CollisionBox(0,0,0,0.6f,1.8f);
        model=new ModelNormalHuman();
        maximumTraction = new Vector3(50, 0, 50);
        addAnimaltionCTL(new WalkNormalAnimaltionCTL(query));
    }

	
    
    
}
