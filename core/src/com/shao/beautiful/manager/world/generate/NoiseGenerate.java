package com.shao.beautiful.manager.world.generate;

import com.shao.beautiful.manager.world.DataSaver;
import com.shao.beautiful.manager.world.MapLoader;
import com.shao.beautiful.manager.world.Chunk;
import com.shao.beautiful.gameObj.Block;
import com.shao.beautiful.gameObj.Block.BlockTexture;
import com.mithrilmania.blocktopograph.util.Noise;
import bms.helper.tools.LOG;
import com.shao.beautiful.tools.BlockManager;

public class NoiseGenerate implements TerrainGenerate {

	public static Noise noise;

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
				int high = (int) ((noise.noise((tx/100f) - Math.sin(tx / 20f) * 0.1, (tz/100f) - Math.sin(tz / 20f) * 0.1)
						+ 0.1) * (double) 100);
				for (int y = 0; y < Chunk.width; y++) {
					if (chunkY * Chunk.width + y == high) {
						c.setBlock(x, y, z, BlockManager.get(BlockTexture.DIRT));
					} else if (chunkY * Chunk.width + y < 15) {
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

	public NoiseGenerate(MapLoader loader) {
		this.loader = loader;
		this.noise = new Noise(100);
	}

}
