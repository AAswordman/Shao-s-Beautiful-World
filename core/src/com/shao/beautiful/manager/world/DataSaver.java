package com.shao.beautiful.manager.world;

import com.shao.beautiful.manager.WorldManager;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import bms.helper.tools.LOG;

import com.shao.beautiful.tools.LeveldbUtil;
import com.shao.beautiful.tools.LruCache;
import bms.quote.Ldb;
import com.shao.beautiful.config.GameConfig;

public class DataSaver {

	private WorldManager worldManager;
	private HashMap<KeyGetter, Chunk> cacheEdit = new HashMap<>();
	// private
	public LruCache<KeyGetter, Chunk> cache = new LruCache<KeyGetter, Chunk>(
			(int) Math.pow(GameConfig.chunkLoadNum * 2 + 3, 3) + 1) {
		private static final long serialVersionUID = 12353252L;

		@Override
		protected void removeEldest(Chunk c) {
			c.release();
			super.removeEldest(c);
		}
	};
	private LeveldbUtil ldb;

	public DataSaver(WorldManager worldManager) {
		this.worldManager = worldManager;
		ldb = new LeveldbUtil(Gdx.files.local(bms.helper.Global.dir + "/world").file());

	}

	public Chunk getExistingChunk(int x, int y, int z) {
		KeyGetter key = KeyGetter.getChunkKeyGetter(x, y, z, KeyGetter.WORLD_OVERWORLD);
		if (cacheEdit.containsKey(key)) {
			return cacheEdit.get(key);
		}
		Chunk c;

		if ((c = cache.get(key)) != null) {
			return c;
		}
		return c;
	}

	public byte[] get(byte[] key) {
		return ldb.get(key);
	}

	public boolean hasChunk(int x, int y, int z) {
		KeyGetter key = KeyGetter.getChunkKeyGetter(x, y, z, KeyGetter.WORLD_OVERWORLD);
		return cache.containsKey(key) || cacheEdit.containsKey(key);
	}

	public void destroy() {
		ldb.close();
	}

	public void postEditChunk(int x, int y, int z) {
		Chunk c;
		KeyGetter key = KeyGetter.getChunkKeyGetter(x, y, z, KeyGetter.WORLD_OVERWORLD);
		if (cacheEdit.containsKey(key)) {
			return;
		}

		if ((c = cache.get(key)) != null) {
			cache.remove(key);
			c.isEdit = true;
			cacheEdit.put(key, c);
			return;
		}
		c = new Chunk(this, ldb.get(key.getKey()), x, y, z);
		c.isEdit = true;
		cacheEdit.put(key, c);
	}

	public void putRenderFace(int x, int y, int z, byte[] data) {
		ldb.put(KeyGetter.getRenderFaceKey(x, y, z, KeyGetter.WORLD_OVERWORLD), data);
	}

	public Chunk getChunk(int x, int y, int z) {
		KeyGetter key = KeyGetter.getChunkKeyGetter(x, y, z, KeyGetter.WORLD_OVERWORLD);
		if (cacheEdit.containsKey(key)) {
			return cacheEdit.get(key);
		}
		Chunk c;

		if ((c = cache.get(key)) != null) {
			return c;
		}
		
		byte[] b = ldb.get(key.getKey());
		
		if (b != null) {
			c = new Chunk(this, b, x, y, z);
			cache.put(key, c);
			return c;
		}
		
		return null;
	}

	public void putChunk(int x, int y, int z, Chunk c) {
		if (c == null) {
			return;
		}
		KeyGetter key = KeyGetter.getChunkKeyGetter(x, y, z, KeyGetter.WORLD_OVERWORLD);
		cache.put(key, c);
		ldb.put(key.getKey(), c.toByteArray());
		// LOG.print("新的区块",x+"/"+y+"/"+z);
	}

	public void save() {

	}

	public static class KeyGetter {
		public static byte WORLD_OVERWORLD = 0x00, WORLD_ABYSSWORLD = 0x01;
		public static byte TYPE_CHUNKDATA = 0x00, TYPE_RENDERFACE = 0x01;

		private byte dimension;

		private int x;

		private int y;

		private int z;

		private int type;

		// 都是区块坐标
		public byte[] getKey() {
			return getChunkKey(x, y, z, dimension);
		}

		@Override
		public int hashCode() {
			return x * 1024 + y * 256 + z + dimension * 128 + type;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof KeyGetter) {
				KeyGetter another = (KeyGetter) obj;
				return another.x == x && another.y == y && another.z == z && another.dimension == dimension;
			}
			return false;
		}

		public static KeyGetter getChunkKeyGetter(int x, int y, int z, byte dimension) {
			return new KeyGetter(x, y, z, dimension, TYPE_CHUNKDATA);
		}

		public KeyGetter(int x, int y, int z, byte dimension, int type) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dimension = dimension;
			this.type = type;
		}

		public static byte[] getChunkKey(int x, int y, int z, byte dimension) {
			byte[] key = new byte[12];
			byte[] a = intTobyte(x);
			key[0] = a[0];
			key[1] = a[1];
			key[2] = a[2];
			key[3] = a[3];
			a = intTobyte(y);
			key[4] = a[2];
			key[5] = a[3];
			a = intTobyte(z);
			key[6] = a[0];
			key[7] = a[1];
			key[8] = a[2];
			key[9] = a[3];
			key[10] = dimension;
			key[11] = TYPE_CHUNKDATA;

			return key;
		}

		public static KeyGetter getRenderFaceKeyGetter(int x, int y, int z, byte dimension) {
			return new KeyGetter(x, y, z, dimension, TYPE_RENDERFACE);
		}

		public static byte[] getRenderFaceKey(int x, int y, int z, byte dimension) {
			byte[] key = new byte[12];
			byte[] a = intTobyte(x);
			key[0] = a[0];
			key[1] = a[1];
			key[2] = a[2];
			key[3] = a[3];
			a = intTobyte(y);
			key[4] = a[2];
			key[5] = a[3];
			a = intTobyte(z);
			key[6] = a[0];
			key[7] = a[1];
			key[8] = a[2];
			key[9] = a[3];
			key[10] = dimension;
			key[11] = TYPE_RENDERFACE;

			return key;
		}

		public static byte[] intTobyte(int i) {
			byte[] result = new byte[4];
			result[0] = (byte) ((i >> 24) & 0xFF);
			result[1] = (byte) ((i >> 16) & 0xFF);
			result[2] = (byte) ((i >> 8) & 0xFF);
			result[3] = (byte) (i & 0xFF);
			return result;

		}
		@Override
		public String toString() {
			return x+"/"+y+"/"+z;
		}
	}
}
