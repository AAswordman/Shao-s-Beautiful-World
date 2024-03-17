package com.shao.beautiful.gameObj;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.gameObj.Block.BlockTexture;
import com.shao.beautiful.tools.TextureManager;

import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;

public class BlockNature {
	private static Object value = new Object();
	private static HashMap<Integer, Object> canRenderBlock = new HashMap<Integer, Object>();
	private static HashMap<Integer, Object> canPass = new HashMap<Integer, Object>();
	private static HashMap<Integer, Object> cannotBeRender = new HashMap<Integer, Object>();

	private static Pixmap blockMap;
	private static Pixmap normalMap;
	private static HashMap<String, Integer[]> textureUV = new HashMap<String, Integer[]>();
	public static final int BLOCK_TEXTURE_SIZE = GameConfig.textureOneSize;
	public static final int MAP_TEXTURE_SIZE = GameConfig.textureMapSize;
	private static int drawIndex = 0;
	private static Texture blockTexture;
	private static Texture normalTexture;
	private static Texture pbrTexture;
	private static Pixmap pbrMap;
	private static Material material;

	static {
		addCanPass(BlockTexture.AIR);
		addCanPass(BlockTexture.VOID);

		addCanRenderBlock(BlockTexture.AIR);
		addCanRenderBlock(BlockTexture.WATER);

		addCannotBeRender(BlockTexture.AIR);
		addCannotBeRender(BlockTexture.NULL);
		addCannotBeRender(BlockTexture.VOID);
	}

	public static void init() {
		blockMap = new Pixmap(MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, Pixmap.Format.RGBA8888);
		normalMap = new Pixmap(MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, Pixmap.Format.RGBA8888);
		pbrMap = new Pixmap(MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, Pixmap.Format.RGBA8888);
	}

	/*
	 * public synchronized static Integer[] getTextureUV(String k) { return
	 * getTextureUV(k,false); }
	 */
	public synchronized static Integer[] getTextureUV(String k, boolean fourTexture) {
		if (textureUV.containsKey(k)) {
			return textureUV.get(k);
		}
		int x = drawIndex % (MAP_TEXTURE_SIZE / BLOCK_TEXTURE_SIZE) * BLOCK_TEXTURE_SIZE;
		int y = (int) Math.floor(drawIndex / MAP_TEXTURE_SIZE / BLOCK_TEXTURE_SIZE) * BLOCK_TEXTURE_SIZE;
		Pixmap pixmap = new Pixmap(Gdx.files.internal("block/" + k));
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		if (fourTexture) {
			normalMap.drawPixmap(pixmap, 0, height / 2, width / 2, height / 2, x, y, BLOCK_TEXTURE_SIZE,
					BLOCK_TEXTURE_SIZE);
			blockMap.drawPixmap(pixmap, 0, 0, width / 2, height / 2, x, y, BLOCK_TEXTURE_SIZE, BLOCK_TEXTURE_SIZE);
			pbrMap.drawPixmap(pixmap, width / 2, 0, width / 2, height / 2, x, y, BLOCK_TEXTURE_SIZE,
					BLOCK_TEXTURE_SIZE);
		} else {
			blockMap.drawPixmap(pixmap, 0, 0, width, height, x, y, BLOCK_TEXTURE_SIZE, BLOCK_TEXTURE_SIZE);
		}
		drawIndex++;
		textureUV.put(k, new Integer[] { x, y });
		pixmap.dispose();
		return new Integer[] { x, y };
	}

	public static boolean isCanRenderBlock(int id) {
		return canRenderBlock.containsKey(id);
	}

	public static boolean isCanPass(int id) {
		return canPass.containsKey(id);
	}

	public static boolean isCannotBeRender(int id) {
		return cannotBeRender.containsKey(id);
	}

	public static boolean isCanRenderBlock(Block id) {
		return isCanRenderBlock(id.id);
	}

	public static boolean isCanPass(Block id) {
		return isCanPass(id.id);
	}

	public static boolean isCannotBeRender(Block id) {
		return isCannotBeRender(id.id);
	}

	private static void addCanPass(BlockTexture b) {
		canPass.put(b.id, value);
	}

	private static void addCanRenderBlock(BlockTexture b) {
		canRenderBlock.put(b.id, value);
	}

	private static void addCannotBeRender(BlockTexture b) {
		cannotBeRender.put(b.id, value);
	}

	public static Texture getBlockMap() {
		if (blockTexture != null) {
			return blockTexture;
		} else {
			blockTexture = new Texture(blockMap);

		}
		return blockTexture;
	}

	public static Texture getNormalBlockMap() {
		if (normalTexture != null) {
			return normalTexture;
		} else {
			normalTexture = new Texture(normalMap);

		}
		return normalTexture;
	}

	public static Texture getPbrMap() {
		if (pbrTexture != null) {
			return pbrTexture;
		} else {
			pbrTexture = new Texture(pbrMap);

		}
		return pbrTexture;
	}

	public static Material getMaterial() {
		if (material != null) {
			return material;
		}
		material = new Material(PBRTextureAttribute.createBaseColorTexture(BlockNature.getBlockMap()),
				PBRTextureAttribute.createNormalTexture(BlockNature.getNormalBlockMap()),
				PBRTextureAttribute.createMetallicRoughnessTexture(BlockNature.getPbrMap()),
				PBRTextureAttribute.createOcclusionTexture(BlockNature.getPbrMap()));
		return material;
	}
}
