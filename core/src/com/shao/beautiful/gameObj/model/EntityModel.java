package com.shao.beautiful.gameObj.model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class EntityModel extends ModelGroup{
    public EntityModel(){
        super("__root__",0,0,0);
    }

    @Override
    public ModelGroup copy() {
        return super.copy();
    }
    
}
