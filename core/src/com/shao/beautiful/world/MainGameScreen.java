package com.shao.beautiful.world;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.shao.beautiful.gameObj.Block;
import com.shao.beautiful.gameObj.Block.BlockTexture;
import com.shao.beautiful.gameObj.model.player.ModelNormalHuman;
import com.shao.beautiful.manager.UIManager;
import com.shao.beautiful.manager.WorldManager;
import com.shao.beautiful.tools.BlockManager;
import java.util.Iterator;
import java.util.Map.Entry;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import com.shao.beautiful.gameObj.RenderToolManager;

public class MainGameScreen implements Screen {

    private MainGame game;
    
    
    private float time;
    
    public WorldManager worldManager;
    public UIManager uiManager;

    private ModelNormalHuman entityTest;

    private RenderToolManager manager;
    
    public MainGameScreen(MainGame game){
        this.game=game;
    }
    @Override
    public void show() {
        
        manager = new RenderToolManager();
        
        
        worldManager=new WorldManager();
        uiManager=new UIManager(worldManager);
        uiManager.createUI(game.stage);
        game.batch.setProjectionMatrix(uiManager.cam.combined);
        manager.setCamera(uiManager.cam);
        manager.setCenter(worldManager.entities.getPlayer().getPosition());
    }

    @Override
    public void render(float p1) {
        
        
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.8f, 0.95f, 1f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
       
        uiManager.step(Gdx.graphics.getDeltaTime());
        time+=Gdx.graphics.getDeltaTime();
        
        manager.upDate(Gdx.graphics.getDeltaTime());
        
        worldManager.step(Gdx.graphics.getDeltaTime());
        worldManager.render(manager,uiManager);
        
        uiManager.step(Gdx.graphics.getDeltaTime());
        uiManager.render(game.stage);
        /*
        modelBatch.begin(uiManager.cam);
        
        
        BlockManager.get(1).setPos(0,0,1).render(modelBatch);
        BlockManager.get(1).setPos(1,0,0).render(modelBatch);
        BlockManager.get(1).setPos(0,1,0).render(modelBatch);
        BlockManager.get(1).setPos(0,0,0).render(modelBatch);
        try {
            modelBatch.end();
        } catch (Exception e) {

        }
        */
    }

    @Override
    public void resize(int p1, int p2) {
        uiManager.resize();
        
        game.stage.getViewport().update(p1,p2,true);
        //game.stage.getCamera().position.setZero();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        worldManager.destroy();
        uiManager.destroy();
    }
    
    
    
    
}
