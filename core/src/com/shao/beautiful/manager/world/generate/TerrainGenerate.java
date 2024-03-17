package com.shao.beautiful.manager.world.generate;
import com.shao.beautiful.manager.world.Chunk;

public interface TerrainGenerate {
    Chunk getChunk(int x,int y,int z);
    
    
}
