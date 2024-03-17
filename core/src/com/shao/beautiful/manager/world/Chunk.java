package com.shao.beautiful.manager.world;

import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.gameObj.Block;
import java.util.HashMap;
import bms.helper.tools.LOG;
import com.shao.beautiful.tools.BlockManager;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import bms.helper.tools.ArrayListSafe;
import com.shao.beautiful.gameObj.FaceData;
import com.shao.beautiful.tools.FaceDataManager;
import java.nio.ByteBuffer;
import com.badlogic.gdx.graphics.Camera;
import com.shao.beautiful.gameObj.CollisionBox;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

public class Chunk implements Disposable{
	public static int width = GameConfig.chunkWidth;
	public transient ModelInstance mixModel;
	public volatile ByteBuffer blockData;
	public volatile ByteBuffer faceData;
	public boolean isEdit = true;
	public boolean isLoaded = false;
	public ArrayListSafe<Block.ModelFace> renderFace = new ArrayListSafe<>();

	private BoundingBox bound;
	private int chunkX;
	private int chunkY;
	private int chunkZ;

	private DataSaver dataSaver;

	public Chunk(DataSaver saver, int chunkX, int chunkY, int chunkZ) {
		this.dataSaver = saver;

		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.chunkZ = chunkZ;

		loadBlockData();
		loadFaceData();
		/*
		 * for(int x=0;x<width;x++){ for(int z=0;z<width;z++){ int high=0; } }
		 */
		bound = new BoundingBox(new Vector3(chunkX * Chunk.width, chunkY * Chunk.width, chunkZ * Chunk.width),
				new Vector3(chunkX * Chunk.width + Chunk.width, chunkY * Chunk.width + Chunk.width,
						chunkZ * Chunk.width + Chunk.width));
	}

	public Chunk(DataSaver saver, byte[] b, int chunkX, int chunkY, int chunkZ) {
		this.dataSaver = saver;

		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.chunkZ = chunkZ;

		blockData = ByteBuffer.wrap(b);

		bound = new BoundingBox(new Vector3(chunkX * Chunk.width, chunkY * Chunk.width, chunkZ * Chunk.width),
				new Vector3(chunkX * Chunk.width + Chunk.width, chunkY * Chunk.width + Chunk.width,
						chunkZ * Chunk.width + Chunk.width));
	}

	public void release() {
		if (this.blockData != null) {
			this.blockData.clear();
		}
		if (this.faceData != null) {
			this.faceData.clear();
		}
		this.renderFace.clear();
		blockData = null;
		faceData = null;
	}

	public void loadBlockData() {
		byte[] b = dataSaver
				.get(DataSaver.KeyGetter.getChunkKey(chunkX, chunkY, chunkZ, DataSaver.KeyGetter.WORLD_OVERWORLD));
		if (b != null) {
			blockData = ByteBuffer.wrap(b);
		}
	}

	public void loadFaceData() {
		byte[] b = dataSaver
				.get(DataSaver.KeyGetter.getRenderFaceKey(chunkX, chunkY, chunkZ, DataSaver.KeyGetter.WORLD_OVERWORLD));
		if (b != null) {
			faceData = ByteBuffer.wrap(b);
		}
	}

	public void setMixModel(ModelInstance m) {
		this.bound = new BoundingBox(new Vector3(chunkX * width, chunkY * width, chunkZ * width),
				new Vector3(chunkX * width + width, chunkY * width + width, chunkZ * width + width));
		mixModel = m;
	}
	public void disPoseMixModel() {
		if (mixModel==null) {
			return;
		}
		mixModel.model.dispose();
	}
	public byte[] toByteArray() {
		return blockData.array();
	}

	public void setRenderFace(byte[] renderData) {
		faceData = ByteBuffer.wrap(renderData);
	}

	public Block getBlock(int x, int y, int z) {
		if (blockData == null) {
			loadBlockData();
		}
		// LOG.print("x|y|z",x+"|"+y+"|"+z);
		return BlockManager.get(blockData.get(getBlockPosition(x, y, z)) & 0xff);
	}

	public FaceData getFaceData(int x, int y, int z) {
		if (faceData == null) {
			loadFaceData();
		}
		// LOG.print("x|y|z",x+"|"+y+"|"+z);
		return FaceDataManager.get(faceData.get(getBlockPosition(x, y, z)) & 0xff);
	}

	public void setFaceData(int x, int y, int z, FaceData f) {
		faceData.put(getBlockPosition(x, y, z), f.getData());
		// 不必重新刷新渲染的面

	}

	public void setBlock(int x, int y, int z, Block b) {
		blockData.put(getBlockPosition(x, y, z), (byte) b.id);
	}

	public static int getBlockPosition(int x, int y, int z) {
		return z * width * width + x * width + y;
	}

	@Override
	public String toString() {
		return "Chunk: ";
	}

	public boolean isVisible(final Camera camx) {
		return camx.frustum.boundsInFrustum(bound);
		// return true;
	}

	public boolean isLoadingComplete() {
		return !isEdit && isLoaded;
	}

	public int getBlock(int pos) {
		if (blockData == null) {
			loadBlockData();
		}
		// LOG.print("x|y|z",x+"|"+y+"|"+z);
		return blockData.get(pos) & 0xff;
	}

	@Override
	public void dispose() {
		release();
		disPoseMixModel();
	}
}
