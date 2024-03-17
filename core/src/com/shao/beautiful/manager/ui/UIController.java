package com.shao.beautiful.manager.ui;

import com.shao.beautiful.manager.UIManager;
import com.shao.beautiful.manager.WorldManager;
import com.shao.beautiful.tools.CameraController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;

public class UIController extends GestureDetector {

	private UIManager uiManager;

	protected static class UIGestureListener extends GestureAdapter {
		public UIController controller;

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			return false;

		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			return false;
		}
	};

	public UIController(UIManager uiManager) {
		this(new UIGestureListener(), uiManager);
	}

	protected UIController(UIGestureListener listener, UIManager uiManager) {
		super(listener);
		this.uiManager = uiManager;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.W) {
			uiManager.buttonWDown = true;
		} else if (keycode == Keys.A) {
			uiManager.buttonADown = true;
		} else if (keycode == Keys.S) {
			uiManager.buttonSDown = true;
		} else if (keycode == Keys.D) {
			uiManager.buttonDDown = true;
		} else if (keycode == Keys.GRAVE) {
			uiManager.changeLocking();
		} else if (keycode == Keys.SPACE) {
			uiManager.jump();
		} else if (keycode == Keys.F12) {
			uiManager.changeHideUI();
		} else if (keycode == Keys.LEFT) {
			uiManager.leftDown = true;
		} else if (keycode == Keys.RIGHT) {
			uiManager.rightDown = true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.W) {
			uiManager.buttonWDown = false;
		} else if (keycode == Keys.A) {
			uiManager.buttonADown = false;
		} else if (keycode == Keys.S) {
			uiManager.buttonSDown = false;
		} else if (keycode == Keys.D) {
			uiManager.buttonDDown = false;
		} else if (keycode == Keys.LEFT) {
			uiManager.leftDown = false;
		} else if (keycode == Keys.RIGHT) {
			uiManager.rightDown = false;
		}
		return false;
	}
}
