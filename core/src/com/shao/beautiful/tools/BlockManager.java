package com.shao.beautiful.tools;

import com.badlogic.gdx.files.FileHandle;
import com.shao.beautiful.gameObj.Block;
import com.shao.beautiful.gameObj.Block.BlockTexture;

import java.util.HashMap;

public class BlockManager {

    private static HashMap<Integer,Block> BLOCKONLY=new HashMap<>();
    public static Block get(int id) {
        if (BLOCKONLY.containsKey(id)) {
            return BLOCKONLY.get((id));
        } else {
            Block texx=new Block(id);
            BLOCKONLY.put(id, texx);
            return texx;
        }
	}
    public static Block get(BlockTexture bl) {
        return get(bl.id);
	}

}
