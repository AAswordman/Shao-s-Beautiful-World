package com.shao.beautiful.manager.world.generate;
import com.shao.beautiful.manager.world.Chunk;
import com.shao.beautiful.manager.world.MapLoader;
import com.shao.beautiful.gameObj.Block;

public class FlatGenerate implements TerrainGenerate {
    private MapLoader loader;
    public FlatGenerate(MapLoader loader) {
        this.loader = loader;
    }
    @Override
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {

        Chunk c=new Chunk(new byte[Chunk.width * Chunk.width * Chunk.width]);
        if (chunkY == 0) {
            for (int x=0;x < Chunk.width;x++) {
                for (int z=0;z < Chunk.width;z++) {
                    c.setBlock(x, 0, z, new Block(1));
                    c.setBlock(x, 1, z, new Block(1));
                }
            }
        }
        return c;

    }
}
