package com.shao.beautiful.gameObj;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import com.shao.beautiful.gameObj.blocks.*;
import com.shao.beautiful.tools.BlockManager;

import java.util.HashMap;

public class Block extends GameObj {
	public int id;
	private boolean shaderOpen;
	public BlockBase blockData;
	public static int FACE_TOP = 3, FACE_BOTTOM = 2, FACE_BEHIND = 0, FACE_FRONT = 1, FACE_LEFT = 4, FACE_RIGHT = 5,
			FACE_RESTART = 6;
	private static final int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
			| VertexAttributes.Usage.TextureCoordinates;

	public Block(int id) {
		this.id = id;
		build(blockTextureGetter.get(id).texture);
		this.blockData = blockTextureGetter.get(id).block;
		this.shaderOpen = blockTextureGetter.get(id).shader;
	}

	public boolean hasOwnTexture() {
		return blockData != null;
	}

	public ModelInstance[] block;
	private BoundingBox bounds = new BoundingBox();
	private float r;
	private String[] useblockTexture;
	private static int[][] defface = new int[6][3];
	private static float[][][] point = new float[6][4][3];

	private static final float[][] p = new float[][] { new float[] { 0, 0, 0 }, new float[] { 1, 0, 0 },
			new float[] { 0, 1, 0 }, new float[] { 1, 1, 0 }, new float[] { 0, 0, 1 }, new float[] { 1, 0, 1 },
			new float[] { 0, 1, 1 }, new float[] { 1, 1, 1 } };

	private static void buildPart(int index, float[][] p, int[] p1) {
		defface[index] = p1;
		point[index] = p;
	}

	static {
		buildPart(FACE_BEHIND, new float[][] { p[1], p[0], p[2], p[3] }, new int[] { 0, 0, -1 });
		buildPart(FACE_FRONT, new float[][] { p[4], p[5], p[7], p[6] }, new int[] { 0, 0, 1 });
		buildPart(FACE_BOTTOM, new float[][] { p[0], p[1], p[5], p[4] }, new int[] { 0, -1, 0 });
		buildPart(FACE_TOP, new float[][] { p[6], p[7], p[3], p[2] }, new int[] { 0, 1, 0 });
		buildPart(FACE_LEFT, new float[][] { p[0], p[4], p[6], p[2] }, new int[] { -1, 0, 0 });
		buildPart(FACE_RIGHT, new float[][] { p[5], p[1], p[3], p[7] }, new int[] { 1, 0, 0 });
	}

	/*
	 * public void build() { ModelBuilder modelBuilder = new ModelBuilder(); block =
	 * new ModelInstance(modelBuilder.createBox(1f, 1f, 1f, new
	 * Material(ColorAttribute.createDiffuse(Color.GREEN)),
	 * 
	 * VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
	 * 
	 * 
	 * block.calculateBoundingBox(bounds); //r=bounds.getDimensions().len()/2; }
	 * public void build(String p) { FileHandle p1=Gdx.files.internal(p);
	 * ModelBuilder modelBuilder = new ModelBuilder(); block = new
	 * ModelInstance(modelBuilder.createBox(1f, 1f, 1f, new
	 * Material(TextureAttribute.createDiffuse(TextureManager.get(p1)), new
	 * BlendingAttribute(), FloatAttribute.createAlphaTest(0.25f)),
	 * VertexAttributes.Usage.Position |
	 * VertexAttributes.Usage.TextureCoordinates));
	 * 
	 * 
	 * block.calculateBoundingBox(bounds); //r=bounds.getDimensions().len()/2; }
	 */
	public void build(String[] pp) {
		MeshBuilder modelBuilder = new MeshBuilder();
		this.useblockTexture = pp;

		/*
		 * block[0] = buildPart(p[0], p[2], p[3], p[1], 0, 0, 1, modelBuilder, attr,
		 * TextureManager.get(Gdx.files.internal("dirt.png"))); block[1] =
		 * buildPart(p[5], p[7], p[6], p[4], 0, 0, -5, modelBuilder, attr, texture);
		 * block[2] = buildPart(p[4], p[6], p[2], p[0], 1, 0, 0, modelBuilder, attr,
		 * TextureManager.get(Gdx.files.internal("grass_side_carried.png"))); block[3] =
		 * buildPart(p[1], p[3], p[7], p[5], -1, 0, 0, modelBuilder, attr,
		 * TextureManager.get(Gdx.files.internal("grass_side_carried.png"))); block[4] =
		 * buildPart(p[2], p[6], p[7], p[3], 0, -1, 0, modelBuilder, attr,
		 * TextureManager.get(Gdx.files.internal("grass_side_carried.png"))); block[5] =
		 * buildPart(p[1], p[5], p[4], p[0], 0, 1, 0, modelBuilder, attr,
		 * TextureManager.get(Gdx.files.internal("grass_side_carried.png")));
		 */
	}

	public Block(FileHandle p1, FileHandle p2, FileHandle p3, FileHandle p4, FileHandle p5, FileHandle p6) {

	}

	public Block setPos(float p1, float p2, float p3) {
		/*
		 * for (ModelInstance b : block) { b.transform.setToTranslation(p1, p2, p3); }
		 */
		return this;
	}

	public void setPos(Vector3 p1) {
		/*
		 * for (ModelInstance b : block) { b.transform.setToTranslation(p1); }
		 */
	}

	public void render(ModelBatch modelBatch) {
		/*
		 * if (shaderOpen) { for (ModelInstance b : block) { modelBatch.render(b, null,
		 * shader); } } else { for (ModelInstance b : block) { modelBatch.render(b); } }
		 */
	}

	public ModelFace getModelFace(int x, int y, int z, int face) {
		if (useblockTexture != null) {
			ModelFace m = new ModelFace(useblockTexture[face], x, y, z);
			m.usePoint = point[face];
			m.defPos = defface[face];
			m.shaderOpen = shaderOpen;
			m.ownBlock = this;
			return m;
		} else {
			ModelFace m = new ModelFace(null, x, y, z);
			m.usePoint = point[face];
			m.defPos = defface[face];
			m.shaderOpen = shaderOpen;
			m.ownBlock = this;
			return m;
		}
	}

	public CollisionBox getCollisionBox(int x, int y, int z) {
		return new CollisionBox(x + 0.5f, y, z + 0.5f, 1, 1);
	}

	public enum BlockTexture {
		NULL(-1, BlockTexture.TYPE_AAAAAA, true, "grass_carried.png"),
		AIR(0, BlockTexture.TYPE_AAAAAA, true, "grass_carried.png"),
		DIRT(1, BlockTexture.TYPE_AAAAAA, true, "dirt.png"),
		GRASS(2, BlockTexture.TYPE_ABBBBC, true, "grass_carried.png", "grass_side_carried.png", "dirt.png"),
		WATER(3, new BlockWater()), VOID(255, BlockTexture.TYPE_AAAAAA, true, "grass_carried.png");

		private String[] texture;
		public int id;
		public boolean shader;
		public boolean dynamicUV;
		public BlockBase block;

		public static final int TYPE_AAAAAA = 0, TYPE_ABBBBC = 1, TYPE_ABCDEF = 2, TYPE_ABCBCD = 3;

		BlockTexture(int id, int type, String... texture) {
			this.id = id;
			// this.texture=texture;
			this.texture = new String[6];
			switch (type) {
			case TYPE_ABBBBC:
				this.texture[FACE_TOP] = texture[0];
				this.texture[FACE_FRONT] = texture[1];
				this.texture[FACE_RIGHT] = texture[1];
				this.texture[FACE_BEHIND] = texture[1];
				this.texture[FACE_LEFT] = texture[1];
				this.texture[FACE_BOTTOM] = texture[2];
				break;
			case TYPE_AAAAAA:
				this.texture[FACE_TOP] = texture[0];
				this.texture[FACE_FRONT] = texture[0];
				this.texture[FACE_RIGHT] = texture[0];
				this.texture[FACE_BEHIND] = texture[0];
				this.texture[FACE_LEFT] = texture[0];
				this.texture[FACE_BOTTOM] = texture[0];
				break;
			case TYPE_ABCDEF:
				this.texture[FACE_TOP] = texture[0];
				this.texture[FACE_FRONT] = texture[1];
				this.texture[FACE_RIGHT] = texture[2];
				this.texture[FACE_BEHIND] = texture[3];
				this.texture[FACE_LEFT] = texture[4];
				this.texture[FACE_BOTTOM] = texture[5];
				break;
			case TYPE_ABCBCD:
				this.texture[FACE_TOP] = texture[0];
				this.texture[FACE_FRONT] = texture[1];
				this.texture[FACE_RIGHT] = texture[2];
				this.texture[FACE_BEHIND] = texture[1];
				this.texture[FACE_LEFT] = texture[2];
				this.texture[FACE_BOTTOM] = texture[3];
				break;
			}
		}

		BlockTexture(int id, int type, boolean shader, String... texture) {
			this(id, type, texture);
			this.shader = shader;
		}

		BlockTexture(int id, BlockBase block) {
			this.id = id;
			shader = block.shaderOpen();
			dynamicUV = block.dynamicUV();
			this.block = block;
		}
	}

	public static HashMap<Integer, BlockTexture> blockTextureGetter = new HashMap<>();
	public static Shader shader;

	public static void init() {
		for (BlockTexture b : BlockTexture.values()) {
			blockTextureGetter.put(b.id, b);
			//System.out.println(b.id);
			BlockManager.get(b.id);
			if (b.texture != null) {
				for (String s : b.texture) {
					if (s != null) {
						BlockNature.getTextureUV(s, b.shader);
					}
				}
			}
		}
	}

	public static class ModelFace {
		public Block ownBlock;
		private boolean shaderOpen;
		private ModelInstance face;
		private BoundingBox bounds = new BoundingBox();
		public int x;
		public int y;
		public int z;
		private float r;
		private float[][] usePoint;
		public int[] defPos;
		private String pic;

		public void render(ModelBatch mb) {
			face.transform.setTranslation(x, y, z);
			if (shaderOpen) {
				mb.render(face, null, Block.shader);
			} else {
				mb.render(face);
			}
		}

		public void build(MeshBuilder builder) {
			buildPart(usePoint[0], usePoint[1], usePoint[2], usePoint[3], defPos[0], defPos[1], defPos[2], builder,
					attr);
		}

		private void buildPart(float[] p1, float[] p2, float[] p3, float[] p4, float a, float b, float c,
				MeshBuilder modelBuilder, int attr) {
			if (hasOwnTexture()) {
				
				
				modelBuilder.setUVRange(0, 0, 1,1f/getBlockData().getFrames());
				modelBuilder.rect(p1[0] + x, p1[1] + y, p1[2] + z, p2[0] + x, p2[1] + y, p2[2] + z, p3[0] + x,
						p3[1] + y, p3[2] + z, p4[0] + x, p4[1] + y, p4[2] + z, a, b, c);
			} else {
				Integer[] uvIntegers = BlockNature.getTextureUV(pic, shaderOpen);

				modelBuilder.setUVRange((float) uvIntegers[0] / (float) BlockNature.MAP_TEXTURE_SIZE,
						(float) uvIntegers[1] / (float) BlockNature.MAP_TEXTURE_SIZE,
						(float) uvIntegers[0] / (float) BlockNature.MAP_TEXTURE_SIZE
								+ (float) BlockNature.BLOCK_TEXTURE_SIZE / (float) BlockNature.MAP_TEXTURE_SIZE,
						(float) uvIntegers[1] / (float) BlockNature.MAP_TEXTURE_SIZE
								+ (float) BlockNature.BLOCK_TEXTURE_SIZE / (float) BlockNature.MAP_TEXTURE_SIZE);

				modelBuilder.rect(p1[0] + x, p1[1] + y, p1[2] + z, p2[0] + x, p2[1] + y, p2[2] + z, p3[0] + x,
						p3[1] + y, p3[2] + z, p4[0] + x, p4[1] + y, p4[2] + z, a, b, c);
			}
		}

		public boolean hasOwnTexture() {
			return ownBlock.hasOwnTexture();
		}
		public BlockBase getBlockData() {
			return ownBlock.blockData;
		}

		/*
		 * public ModelInstance getModel(){ face.transform.setTranslation(x, y, z);
		 * return face.copy(); }
		 */
		public ModelFace(String t, int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.r = bounds.getDimensions(new Vector3(x, y, z)).len();
			this.pic = t;
		}

		public boolean isVisible(final Camera camx) {
			return camx.frustum.sphereInFrustum(new Vector3(x, y, z), r);
		}
	}
}
