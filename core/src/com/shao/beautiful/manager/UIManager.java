package com.shao.beautiful.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.shao.beautiful.manager.ui.UIController;
import com.shao.beautiful.manager.world.Chunk;
import com.shao.beautiful.manager.world.generate.boimes.BoimeData;
import com.shao.beautiful.manager.world.generate.boimes.NoiseUtil;
import com.shao.beautiful.manager.world.generate.boimes.WorldBoime;
import com.shao.beautiful.tools.CameraController;
import com.shao.beautiful.tools.TextureManager;

import bms.helper.tools.TimeDelayer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.shao.beautiful.config.GameConfig;
import com.shao.beautiful.gameObj.RenderToolManager;
import com.shao.beautiful.gameObj.entities.Player;

public class UIManager implements StepAble {
	public PerspectiveCamera cam;

	private CameraController camController;
	private InputMultiplexer inputMultiplexer;
	public UIController controller;
	private Stage stage;
	private int standardLong;

	private boolean isMoving;
	private boolean lockingAngle;
	// 涓存椂
	private final float movement = 800;
	public final Vector2 moveVec = new Vector2();

	private WorldManager worldManager;
	private Label positionText;
	private Label fpsText;
	private TimeDelayer timeDelayer;
	private int dec_size;

	public boolean buttonWDown = false;
	public boolean buttonADown = false;
	public boolean buttonSDown = false;
	public boolean buttonDDown = false;

	private Image lockingImage;

	private TextureRegionDrawable lockingStyleDrawableA;

	private TextureRegionDrawable lockingStyleDrawableB;

	private Actor direction_bar;

	private boolean uiHidden;

	private Image jumpButton;

	private int fpsTimes = 0;

	private Actor canvas;

	public void resize() {
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.update();
		stage.clear();
		loadStandardLong();

		createUI(stage);
	}

	private void loadStandardLong() {
		this.standardLong = Gdx.graphics.getHeight() / 20;
	}

	public void render(Stage stage) {
		stage.act();
		stage.draw();
		
		//System.out.println("t");
	}

	public void changeHideUI() {
		if (uiHidden) {
			stage.getRoot().addActor(lockingImage);
			stage.getRoot().addActor(direction_bar);
			stage.getRoot().addActor(jumpButton);
		} else {
			stage.getRoot().removeActor(lockingImage);
			stage.getRoot().removeActor(direction_bar);
			stage.getRoot().removeActor(jumpButton);
		}
		uiHidden = !uiHidden;

	}

	public void createUI(Stage stage) {
		this.stage = stage;

		inputMultiplexer.addProcessor(0, stage);

		final float dec_position = standardLong / 2;
		dec_size = standardLong * 8;
		final float center_position = dec_position + dec_size / 2;
		final float center_size = standardLong * 2;

		final Texture direction_tex = new Texture(Gdx.files.internal("direction_move1.png"));
		final Texture direction_cen = new Texture(Gdx.files.internal("direction_move2.png"));
		;
		direction_bar = new Actor() {
			public void draw(Batch batch, float parentAlpha) {
				batch.draw(direction_tex, getX(), getY(), getWidth(), getHeight());

				batch.draw(direction_cen, center_position - center_size / 2 + moveVec.x,
						center_position - center_size / 2 + moveVec.y, center_size, center_size);
			}
		};
		direction_bar.setSize(dec_size, dec_size);
		direction_bar.setPosition(dec_position, dec_position);
		direction_bar.addListener(new InputListener() {
			public float limit(float a, float b, float c) {
				return Math.min(Math.max(a, b), c);
			}

			public void touchDragged(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer) {
				moveVec.x = limit(-dec_size / 2, x - dec_position - dec_size / 2, dec_size / 2);
				moveVec.y = limit(-dec_size / 2, y - dec_position - dec_size / 2, dec_size / 2);
			}

			public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer,
					int button) {

				isMoving = true;
				moveVec.x = x - dec_position - dec_size / 2;
				moveVec.y = y - dec_position - dec_size / 2;

				return true;
			}

			public void touchUp(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer,
					int button) {
				isMoving = false;
				moveVec.setZero();
			}

		});
		/*
		 * 绗� 4 姝�: 娣诲姞 button 鍒拌垶鍙�
		 */

		stage.addActor(direction_bar);
		LabelStyle style = new Label.LabelStyle();
		style.font = new BitmapFont();
		positionText = new Label("position", style);
		positionText.setPosition(standardLong, standardLong * 19);
		stage.addActor(positionText);

		fpsText = new Label("fps", style);
		fpsText.setPosition(standardLong, standardLong * 18);
		stage.addActor(fpsText);

		lockingStyleDrawableA = new TextureRegionDrawable(
				new TextureRegion(TextureManager.get(Gdx.files.internal("lockingAngleA.png"))));
		lockingStyleDrawableB = new TextureRegionDrawable(
				new TextureRegion(TextureManager.get(Gdx.files.internal("lockingAngleB.png"))));

		lockingImage = new Image(lockingStyleDrawableA);
		lockingImage.setSize(standardLong * 2, standardLong * 2);
		lockingImage.setPosition(standardLong * 0.5f, standardLong * 8.5f);
		lockingImage.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				changeLocking();

				return true;
			}
		});
		stage.addActor(lockingImage);

		jumpButton = new Image(TextureManager.get(Gdx.files.internal("jump.png")));
		jumpButton.setPosition(Gdx.graphics.getWidth() - standardLong * 7, standardLong * 2);
		jumpButton.setSize(standardLong * 5, standardLong * 5);
		jumpButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				jump();
				return true;
			}
		});
		stage.addActor(jumpButton);

		
		Pixmap pixmap=new Pixmap(2, 2, Pixmap.Format.RGBA8888);
		pixmap.setColor(0xffffffff);
		pixmap.fill();
		final Texture testTex = new Texture(pixmap);
		final WorldBoime boime=new WorldBoime(new NoiseUtil(10086));
		canvas = new Actor() {
			
			@Override
			public void draw(Batch batch, float parentAlpha) {
				for (int x = 0; x < 200; x++) {
					for (int y = 0; y < 200; y++) {
						BoimeData data=new BoimeData();
						boime.getBoime(x*50, y*50, data);
						batch.setColor((float) ((((1+boime.noise.noise5(x, y))*data.altitudeMutiplier)*64f)/128f),0, 0f, 1.0f);
						batch.draw(testTex, getX()+x*testTex.getWidth(), getY()+y*testTex.getHeight());
					}
				}
				
			}
		};
		canvas.setPosition(standardLong * 2, standardLong * 2);
		canvas.setSize(standardLong * 16, standardLong * 16);

//		stage.addActor(canvas);

		timeDelayer = new TimeDelayer(400);
	}

	public void changeLocking() {

		lockingAngle = !lockingAngle;
		if (lockingAngle) {
			lockingImage.setDrawable(lockingStyleDrawableB);
		} else {
			lockingImage.setDrawable(lockingStyleDrawableA);
		}
	}

	private final Vector3 tmpPos = new Vector3();

	private int canJump;

	// 3D瑙嗚
	@Override
	public void step(float delay) {
		fpsTimes += 1;

		Player player = worldManager.entities.getPlayer();
		tmpPos.set(camController.target).sub(player.getPosition());
		camController.target = player.getPosition().cpy();
		cam.position.sub(tmpPos);

		float fpsDelay = timeDelayer.GetDelay();
		if (timeDelayer.IsExceed()) {
			fpsText.setText("fps: " + ((float) fpsTimes * 1000) / fpsDelay);
			fpsTimes = 0;
		}
		positionText.setText("position: " + tmpPos.set((int) Math.floor(player.getPosition().x),
				(int) Math.floor(player.getPosition().y), (int) Math.floor(player.getPosition().z)));
		// cam.direction.set(cam.direction.x, cam.direction.y, 0);
		cam.update();
		camController.update();
		// cam.position.set(tmpPos.set(player.getPosition()).add(10,15,10));
		cam.lookAt(player.getPosition());

		float rotate = 0;
		if (lockingAngle) {
			rotate = -new Vector2(cam.direction.x, cam.direction.z).angleDeg() + 90;
		}

		if (buttonADown || buttonDDown || buttonSDown || buttonWDown) {
			lastGiveSpeed.setZero();

			if (buttonWDown) {
				lastGiveSpeed.add(tmpV.set(0, 1).rotateDeg(rotate).scl(-1, 1).scl(movement));
			}
			if (buttonADown) {
				lastGiveSpeed.add(tmpV.set(-1, 0).rotateDeg(rotate).scl(-1, 1).scl(movement));
			}
			if (buttonSDown) {
				lastGiveSpeed.add(tmpV.set(0, -1).rotateDeg(rotate).scl(-1, 1).scl(movement));
			}
			if (buttonDDown) {
				lastGiveSpeed.add(tmpV.set(1, 0).rotateDeg(rotate).scl(-1, 1).scl(movement));
			}

			move(fpsDelay);
		} else {
			move(fpsDelay, tmpV.set(moveVec).rotateDeg(rotate), dec_size);
		}

		if (leftDown) {
			worldManager.batch.globalTime -= delay * 40f;
		}
		if (rightDown) {
			worldManager.batch.globalTime += delay * 40f;
		}

		if (worldManager.entities.getPlayer().speed.y == 0) {
			canJump = 3;
		}
		// cam.position.x+=delay*2;
		// cam.position.z+=delay*2;

		// cam.position.y=(float)
		// NoiseGenerate.noise.noise(cam.position.x*0.01,cam.position.z*0.01)*100+30;
	}

	public UIManager(WorldManager worldManager) {

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());// 67鍙互鐞嗚В鎴愪竴涓畾鍊硷紝瑙嗚瀹藉害锛�67搴︼級

		cam.position.set(0f, 10f, 0f);
		// cam.zoom=0.02f;
		cam.near = .1f;
		cam.far = GameConfig.chunkLoadNum * Chunk.width * 3;

		cam.up.y = 0;
		cam.up.z = 1;

		cam.update();
		loadStandardLong();
		inputMultiplexer = new InputMultiplexer();
		camController = new CameraController(cam);

		inputMultiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(inputMultiplexer);
		controller = new UIController(this);
		inputMultiplexer.addProcessor(controller);
		this.worldManager = worldManager;
	}

	private final Vector2 tmpV = new Vector2();

	private final Vector2 lastGiveSpeed = new Vector2();

	public boolean leftDown;

	public boolean rightDown;

	public void move(float delay, Vector2 vec, float all) {
		lastGiveSpeed.set(tmpV.set(vec).scl(-1, 1).scl(movement / all * 2));
		move(delay);

	}

	public void jump() {
		if (canJump > 0) {
			worldManager.entities.getPlayer().speed.add(0, 5.4f, 0);
			canJump--;
		}

	}

	public void move(float delay) {

		worldManager.entities.getPlayer().speedActive.set(lastGiveSpeed.x, 0, lastGiveSpeed.y);
		if (!lastGiveSpeed.isZero()) {
			worldManager.entities.getPlayer().query.body_y_rotation = -lastGiveSpeed.angleDeg() + 90;
		}

	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
