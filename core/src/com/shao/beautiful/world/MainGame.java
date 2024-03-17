package com.shao.beautiful.world;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainGame extends Game {

    public Stage stage;
	private MainGameScreen mScreen;

    @Override
    public void create() {
        bms.helper.Global.dir = "shaoWorld";
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(),batch);
        
        font = new BitmapFont();
        this.setScreen(mScreen=new MainGameScreen(this));
        
        //Gdx.graphics.setForegroundFPS(300);
        Gdx.graphics.setVSync(false);
    }
    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void render() {
        super.render();
        
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
        stage.dispose();
        mScreen.dispose();
    }




} 
