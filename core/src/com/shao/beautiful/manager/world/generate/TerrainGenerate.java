package com.shao.beautiful.manager.world.generate;
import com.shao.beautiful.manager.world.Chunk;
import com.shao.beautiful.manager.world.DataSaver;

public interface TerrainGenerate {
    Chunk getChunk(DataSaver dataSaver, int x, int y, int z);
    
    
}
