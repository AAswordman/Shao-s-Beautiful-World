package com.shao.beautiful.manager.world;

import com.shao.beautiful.manager.WorldManager;
import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.gameObj.Block;
import com.shao.beautiful.manager.world.generate.FlatGenerate;
import bms.helper.tools.LOG;
import bms.helper.tools.TimeDelayer;

import com.shao.beautiful.manager.world.generate.NoiseGenerate;
import com.shao.beautiful.manager.world.generate.NoiseTestGenerate;
import com.shao.beautiful.manager.world.generate.TerrainGenerate;
import com.shao.beautiful.tools.BlockManager;

public class MapLoader {

	public WorldManager worldManager;

	private TerrainGenerate generate;

	public MapLoader(WorldManager worldManager) {
		this.worldManager = worldManager;
		this.generate = new NoiseTestGenerate(this);
	}

	public void destroy() {

	}

	public int getHigh(int x, int z) {
		return 0;
	}

	// 放置区块
	public Chunk putChunkIfNon(int chunkX, int chunkY, int chunkZ) {
		
		Chunk c = getChunk(chunkX, chunkY, chunkZ);

		if (c == null) {
			c = generateChunk(chunkX, chunkY, chunkZ,false);
		}
		return c;
	}

	// 获取区块，如果没有则放置
	public Chunk getChunkAndGenerate(int chunkX, int chunkY, int chunkZ) {
		Chunk c = getChunk(chunkX, chunkY, chunkZ);
		if (c == null) {
			c = putChunkIfNon(chunkX, chunkY, chunkZ);
		}
		return c;
	}

	// 生成区块
	public Chunk generateChunk(int chunkX, int chunkY, int chunkZ, boolean force) {
		if (!force) {
			if (chunkY > GameConfig.gameMaxChunkHeight || chunkY < GameConfig.gameMinChunkHeight) {
				return null;
			}
		}
		Chunk c;
	
		worldManager.saver.putChunk(chunkX, chunkY, chunkZ, (c = generate.getChunk(chunkX, chunkY, chunkZ)));
		return c;
	}

	// 获取已经生成地图中的方块
	public Block getBlock(double x, double y, double z) {
		int chunkX = (int) Math.floor(x / Chunk.width);
		int chunkY = (int) Math.floor(y / Chunk.width);
		int chunkZ = (int) Math.floor(z / Chunk.width);
		if (chunkY > GameConfig.gameMaxChunkHeight || chunkY < GameConfig.gameMinChunkHeight) {
			return BlockManager.get(Block.BlockTexture.AIR);
		}
		Chunk c = getChunk(chunkX, chunkY, chunkZ);
		if (c == null) {
			return BlockManager.get(-1);
		}
		return c.getBlock(mod(x, Chunk.width), mod(y, Chunk.width), mod(z, Chunk.width));
	}

	// 获取方块
	public Block getBlockAndGenerate(double x, double y, double z) {

		int chunkX = (int) Math.floor(x / Chunk.width);
		int chunkY = (int) Math.floor(y / Chunk.width);
		int chunkZ = (int) Math.floor(z / Chunk.width);
		if (chunkY > GameConfig.gameMaxChunkHeight || chunkY < GameConfig.gameMinChunkHeight) {
			return BlockManager.get(Block.BlockTexture.AIR);
		}
		Chunk c = getChunkAndGenerate(chunkX, chunkY, chunkZ);
		return c.getBlock(mod(x, Chunk.width), mod(y, Chunk.width), mod(z, Chunk.width));
	}

	// 获取已经加载到的方块
	public Block getExistingBlock(double x, double y, double z) {
		int chunkX = (int) Math.floor(x / Chunk.width);
		int chunkY = (int) Math.floor(y / Chunk.width);
		int chunkZ = (int) Math.floor(z / Chunk.width);
		if (chunkY > GameConfig.gameMaxChunkHeight || chunkY < GameConfig.gameMinChunkHeight) {
			return BlockManager.get(Block.BlockTexture.AIR);
		}
		Chunk c = getExistingChunk(chunkX, chunkY, chunkZ);
		if (c != null) {
			return c.getBlock(mod(x, Chunk.width), mod(y, Chunk.width), mod(z, Chunk.width));
		}
		return BlockManager.get(-1);
	}

	public Chunk getExistingChunk(int chunkX, int chunkY, int chunkZ) {
		Chunk c = worldManager.saver.getExistingChunk(chunkX, chunkY, chunkZ);
		return c;
	}

	public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
		if (chunkY > GameConfig.gameMaxChunkHeight || chunkY < GameConfig.gameMinChunkHeight) {
			return null;
		}
		Chunk c = worldManager.saver.getChunk(chunkX, chunkY, chunkZ);

		return c;
	}

	public void putBlockPlayer(int x, int y, int z) {

	}

	private int mod(double a, double b) {
		// LOG.print(a+"",b+"");
		return (int) (a - Math.floor(a / b) * b);
	}
}
