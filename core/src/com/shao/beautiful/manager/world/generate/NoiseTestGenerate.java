package com.shao.beautiful.manager.world.generate;

import com.mithrilmania.blocktopograph.util.Noise;
import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.gameObj.Block.BlockTexture;
import com.shao.beautiful.manager.world.Chunk;
import com.shao.beautiful.manager.world.DataSaver;
import com.shao.beautiful.manager.world.MapLoader;
import com.shao.beautiful.manager.world.generate.boimes.BoimeData;
import com.shao.beautiful.manager.world.generate.boimes.NoiseUtil;
import com.shao.beautiful.manager.world.generate.boimes.WorldBoime;
import com.shao.beautiful.tools.BlockManager;

public class NoiseTestGenerate implements TerrainGenerate {
	public static NoiseUtil noise;
	private WorldBoime boime;

	@Override
	public Chunk getChunk(DataSaver dataSaver, int chunkX, int chunkY, int chunkZ) {
		 
		Chunk c = new Chunk(loader.worldManager.saver, new byte[Chunk.width * Chunk.width * Chunk.width], chunkX,
				chunkY, chunkZ);
		if (chunkY < 0)
			return c;
		for (int x = 0; x < Chunk.width; x++) {
			for (int z = 0; z < Chunk.width; z++) {
				int tx = chunkX * Chunk.width + x;
				int tz = chunkZ * Chunk.width + z;
				BoimeData data=new BoimeData();
				boime.getBoime(tx, tz, data);
				int high=(int) (((1+noise.noise5(tx, tz))*data.altitudeMutiplier)*64f);
				for (int y = 0; y < Chunk.width; y++) {
					if (chunkY * Chunk.width + y == high) {
						c.setBlock(x, y, z, BlockManager.get(BlockTexture.GRASS));
					} else if (chunkY * Chunk.width + y < GameConfig.gameSeaLevel) {
						c.setBlock(x, y, z, BlockManager.get(BlockTexture.WATER));
					} else if (chunkY * Chunk.width + y < high) {
						c.setBlock(x, y, z, BlockManager.get(BlockTexture.DIRT));
					}
				}
			}
		}

		return c;
	}

	private MapLoader loader;

	public NoiseTestGenerate(MapLoader loader) {
		this.loader = loader;
		this.noise = new NoiseUtil(100);
		boime=new WorldBoime(noise);
	}
}
