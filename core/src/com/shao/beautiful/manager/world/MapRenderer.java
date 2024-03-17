package com.shao.beautiful.manager.world;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.config.Global;
import com.shao.beautiful.gameObj.Block;
import com.shao.beautiful.gameObj.BlockNature;
import com.shao.beautiful.manager.UIManager;
import com.shao.beautiful.manager.WorldManager;
import com.shao.beautiful.manager.world.DataSaver.KeyGetter;
import com.shao.beautiful.tools.BlockManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.shao.beautiful.tools.FaceDataManager;
import bms.helper.tools.TimeDelayer;
import com.shao.beautiful.gameObj.FaceData;
import com.shao.beautiful.gameObj.Block.ModelFace;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.Mesh;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import com.shao.beautiful.gameObj.RenderToolManager;
import net.mgsx.gltf.loaders.shared.geometry.MeshTangentSpaceGenerator;

public class MapRenderer {

	private WorldManager worldManager;

	private boolean first = true;

	private boolean isLoadOver = true;

	public MapRenderer(WorldManager worldManager) {
		this.worldManager = worldManager;

		BlockNature.init();
		Block.init();
		BlockNature.getMaterial();
	}

	public void loadRenderFace(int chunkX, int chunkY, int chunkZ) {

		final Chunk c = worldManager.loader.getChunk(chunkX, chunkY, chunkZ);
		if (c == null) {
			return;
		}
		int pos = 0;
		if (!c.isLoaded) {
			c.isLoaded = true;
			c.renderFace.clear();
			if (c.isEdit) {
				c.isEdit = false;
				TimeDelayer timer = new TimeDelayer(100);
				timer.GetDelay();
				boolean save = true;
				byte[] faceData = new byte[Chunk.width * Chunk.width * Chunk.width];
				for (int z = 0; z < Chunk.width; z++) {
					for (int x = 0; x < Chunk.width; x++) {
						for (int y = 0; y < Chunk.width; y++) {
							int id = c.getBlock(pos) & 0xff;
							if (!BlockNature.isCannotBeRender(id)) {

								FaceData data = new FaceData();
								int nx = chunkX * Chunk.width + x;
								int ny = chunkY * Chunk.width + y;
								int nz = chunkZ * Chunk.width + z;
								// Block b;
								int pid;

								pid = (worldManager.loader.getExistingBlock(nx - 1, ny, nz)).id;
								save = save && pid != -1;
								if (BlockNature.isCanRenderBlock(pid) && pid != id) {
									data.addFace(Block.FACE_LEFT);
								}
								pid = (worldManager.loader.getExistingBlock(nx + 1, ny, nz)).id;
								save = save && pid != -1;
								if (BlockNature.isCanRenderBlock(pid) && pid != id) {
									data.addFace(Block.FACE_RIGHT);
								}
								pid = (worldManager.loader.getExistingBlock(nx, ny + 1, nz)).id;
								save = save && pid != -1;
								if (BlockNature.isCanRenderBlock(pid) && pid != id) {
									// LOG.print("发现",nx+"/"+ny+"/"+nz);
									data.addFace(Block.FACE_TOP);
								}
								pid = (worldManager.loader.getExistingBlock(nx, ny - 1, nz)).id;
								save = save && pid != -1;
								if (BlockNature.isCanRenderBlock(pid) && pid != id) {
									data.addFace(Block.FACE_BOTTOM);
								}
								pid = (worldManager.loader.getExistingBlock(nx, ny, nz + 1)).id;
								save = save && pid != -1;
								if (BlockNature.isCanRenderBlock(pid) && pid != id) {
									data.addFace(Block.FACE_FRONT);
								}
								pid = (worldManager.loader.getExistingBlock(nx, ny, nz - 1)).id;
								save = save && pid != -1;
								if (BlockNature.isCanRenderBlock(pid) && pid != id) {
									data.addFace(Block.FACE_BEHIND);
								}
								// batch.render(BlockManager.get(id).setPos(chunkX,chunkY,chunkZ).block);
								faceData[pos] = data.getData();
							}
							pos++;

						}
						// LOG.print("使用时间",z+"/"+x +"/"+ timer.GetDelay());
					}

				}

				c.setRenderFace(faceData);
				// LOG.print("使用时间总体" ,""+ timer.GetDelay());
				if (save) {
					worldManager.saver.putRenderFace(chunkX, chunkY, chunkZ, faceData);
				} else {
					c.isLoaded = false;
					c.isEdit = true;

				}

			}
			pos = 0;

			final ArrayList<ModelFace> arrayList = new ArrayList<Block.ModelFace>();
			for (int z = 0; z < Chunk.width; z++) {
				for (int x = 0; x < Chunk.width; x++) {
					for (int y = 0; y < Chunk.width; y++) {

						FaceData face = FaceDataManager.get(c.faceData.get(pos));
						Block b = BlockManager.get(c.blockData.get(pos));

						int nx = chunkX * Chunk.width + x;
						int ny = chunkY * Chunk.width + y;
						int nz = chunkZ * Chunk.width + z;
						// Block b;
						for (int i = 0; i < 6; i++) {
							if (face == null)
								continue;
							if (face.analysisFace(i))
								arrayList.add(b.getModelFace(nx, ny, nz, i));
						}
						pos++;
					}

				}

			}

			// Global.mainThread.postRunnable(new Runnable() {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {

					final MeshBuilder msb = new MeshBuilder();
					final ModelBuilder mb = new ModelBuilder();

					final HashMap<Block, ArrayList<ModelFace>> map = new HashMap<Block, ArrayList<ModelFace>>();

					mb.begin();

					final VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(),
							VertexAttribute.Normal(),
							new VertexAttribute(VertexAttributes.Usage.Tangent, 4, ShaderProgram.TANGENT_ATTRIBUTE),
							VertexAttribute.TexCoords(0));
					msb.begin(attributes, GL20.GL_TRIANGLES);
					for (ModelFace modelFace : arrayList) {
						if (modelFace.hasOwnTexture()) {
							if (!map.containsKey(modelFace.ownBlock)) {
								map.put(modelFace.ownBlock, new ArrayList<ModelFace>());
							}
							map.get(modelFace.ownBlock).add(modelFace);
						} else {
							modelFace.build(msb);
						}
					}
					final Material material = BlockNature.getMaterial();
					int v = 0;
					Mesh m = msb.end();

					v += m.getNumIndices();
					if (m.getNumIndices() != 0)
						MeshTangentSpaceGenerator.computeTangentSpace(m, material, false, true);
					mb.node().id = "block:def";

					mb.part("block:def", m, GL20.GL_TRIANGLES, material);

					Set<Entry<Block, ArrayList<ModelFace>>> en = map.entrySet();
					Iterator<Entry<Block, ArrayList<ModelFace>>> it = en.iterator();

					while (it.hasNext()) {
						Entry<Block, ArrayList<ModelFace>> e = it.next();
						Material Bmaterial = e.getKey().blockData.getMaterial();

						msb.begin(attributes, GL20.GL_TRIANGLES);
						for (ModelFace mf : e.getValue()) {
							mf.build(msb);
						}
						Mesh ms = msb.end();
						v += ms.getNumIndices();
						PBRTextureAttribute normalMap = Bmaterial.get(PBRTextureAttribute.class,
								PBRTextureAttribute.NormalTexture);
						if (normalMap != null && ms.getNumIndices() != 0)
							MeshTangentSpaceGenerator.computeTangentSpace(ms, Bmaterial, false, true);
						// System.out.println(((PBRTextureAttribute)(
						// e.getKey().blockData.getMaterial().get(PBRTextureAttribute.NormalTexture))).textureDescription.texture.getHeight());
						mb.node().id = "block:" + e.getKey().id;
						mb.part("block:" + e.getKey().id, ms, GL20.GL_TRIANGLES, Bmaterial);
					}
					c.disPoseMixModel();
					if (v != 0) {
						c.setMixModel(new ModelInstance(mb.end()));
					} else {
						mb.end().dispose();
					}

				}
			});

		}
		/*
		 * if (c.isLoadingComplete()) { if (worldManager.saver.getExistingChunk(chunkX +
		 * 1, chunkY, chunkZ) != null && worldManager.saver.getExistingChunk(chunkX - 1,
		 * chunkY, chunkZ) != null && worldManager.saver.getExistingChunk(chunkX, chunkY
		 * + 1, chunkZ) != null && worldManager.saver.getExistingChunk(chunkX, chunkY -
		 * 1, chunkZ) != null && worldManager.saver.getExistingChunk(chunkX, chunkY,
		 * chunkZ + 1) != null && worldManager.saver.getExistingChunk(chunkX, chunkY,
		 * chunkZ - 1) != null) { if (worldManager.saver.getExistingChunk(chunkX + 1,
		 * chunkY, chunkZ).isLoadingComplete() &&
		 * worldManager.saver.getExistingChunk(chunkX - 1, chunkY,
		 * chunkZ).isLoadingComplete() && worldManager.saver.getExistingChunk(chunkX,
		 * chunkY + 1, chunkZ).isLoadingComplete() &&
		 * worldManager.saver.getExistingChunk(chunkX, chunkY - 1,
		 * chunkZ).isLoadingComplete() && worldManager.saver.getExistingChunk(chunkX,
		 * chunkY, chunkZ + 1).isLoadingComplete() &&
		 * worldManager.saver.getExistingChunk(chunkX, chunkY, chunkZ -
		 * 1).isLoadingComplete()) {
		 * 
		 * c.release(); } } }
		 */
	}

	private void loadTest(final int playerChunkX, final int playerChunkY, final int playerChunkZ) {
		if (isLoadOver) {
			isLoadOver = false;
			Global.mainThread.start(new Runnable() {
				@Override
				public void run() {

					// 加载范围内所有区块
					for (int chunkX = playerChunkX - GameConfig.chunkLoadNum; chunkX <= playerChunkX
							+ GameConfig.chunkLoadNum; chunkX++) {
						for (int chunkY = playerChunkY - GameConfig.chunkLoadNum; chunkY <= playerChunkY
								+ GameConfig.chunkLoadNum; chunkY++) {
							final int fchunkY = chunkY;
							final int fchunkX = chunkX;
							for (int chunkZ = playerChunkZ - GameConfig.chunkLoadNum; chunkZ <= playerChunkZ
									+ GameConfig.chunkLoadNum; chunkZ++) {
								worldManager.loader.putChunkIfNon(fchunkX, fchunkY, chunkZ);
								// System.out.println("加载");
							}
						}

					}

					for (int chunkX = playerChunkX - GameConfig.chunkLoadNum; chunkX <= playerChunkX
							+ GameConfig.chunkLoadNum; chunkX++) {
						for (int chunkY = playerChunkY - GameConfig.chunkLoadNum; chunkY <= playerChunkY
								+ GameConfig.chunkLoadNum; chunkY++) {
							final int fchunkY = chunkY;
							final int fchunkX = chunkX;
							for (int chunkZ = playerChunkZ - GameConfig.chunkLoadNum; chunkZ <= playerChunkZ
									+ GameConfig.chunkLoadNum; chunkZ++) {

								loadRenderFace(fchunkX, fchunkY, chunkZ);
								// System.out.println("ssss");
							}

						}
					}

					for (int chunkX = playerChunkX - GameConfig.chunkLoadNum; chunkX <= playerChunkX
							+ GameConfig.chunkLoadNum; chunkX++) {
						for (int chunkY = playerChunkY - GameConfig.chunkLoadNum; chunkY <= playerChunkY
								+ GameConfig.chunkLoadNum; chunkY++) {
							for (int chunkZ = playerChunkZ - GameConfig.chunkLoadNum; chunkZ <= playerChunkZ
									+ GameConfig.chunkLoadNum; chunkZ++) {
								Chunk chunk=worldManager.loader.getChunk(chunkX, chunkY, chunkZ);
								if (chunk!=null) {
									chunk.release();
								}
							}

						}
					}

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
					// System.gc();
					isLoadOver = true;
				}
			});

		}
	}

	public void render(RenderToolManager batch, UIManager ui) {
		TimeDelayer delayer = new TimeDelayer(200);
		delayer.UpdateLastTime();
		Vector3 position = worldManager.entities.getPlayer().getPosition();

		int playerChunkX = (int) Math.floor(position.x / Chunk.width);
		int playerChunkY = (int) Math.floor(position.y / Chunk.width);
		int playerChunkZ = (int) Math.floor(position.z / Chunk.width);
		loadTest(playerChunkX, playerChunkY, playerChunkZ);
		// LOG.print("坐标",playerChunkX+"/"+playerChunkY+"/"+playerChunkZ);

		batch.begin(RenderToolManager.RENDER_BLOCK_DEFAULT);

		for (int chunkX = playerChunkX - GameConfig.chunkLoadNum; chunkX <= playerChunkX
				+ GameConfig.chunkLoadNum; chunkX++) {

			for (int chunkY = playerChunkY - GameConfig.chunkLoadNum; chunkY <= playerChunkY
					+ GameConfig.chunkLoadNum; chunkY++) {
				for (int chunkZ = playerChunkZ - GameConfig.chunkLoadNum; chunkZ <= playerChunkZ
						+ GameConfig.chunkLoadNum; chunkZ++) {

					Chunk c = worldManager.loader.getChunk(chunkX, chunkY, chunkZ);

					if (c != null) {
						if (c.mixModel != null) {
							if (c.isVisible(ui.cam)) {
								batch.render(c.mixModel);
							}
						}
					}

				}
			}
		}

		batch.end();

	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
