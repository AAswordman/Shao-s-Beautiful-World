package com.shao.beautiful.world.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;
import com.shao.beautiful.world.MainGame;

public class DesktopLauncher {
	private static MainGame mainGame;
	
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// config.getDesktopDisplayMode().
		config.resizable = true;
		config.foregroundFPS = 300;
		config.backgroundFPS = 300;
		config.vSyncEnabled = false;

		mainGame = new MainGame();

		LwjglApplication application = new LibgdxApp(mainGame, config);

		

	}

	public static class LibgdxApp extends LwjglApplication {
		public LibgdxApp(ApplicationListener listener, LwjglApplicationConfiguration config) {
			super(listener, config);
			
			mainLoopThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					// TODO Auto-generated method stub
					e.printStackTrace();
					mainGame.dispose();
					
					
					System.exit(0);
				}
				
			});
		}
	}
}
