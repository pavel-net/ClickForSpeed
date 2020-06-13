package com.byPahych.clickforspeed_free_version;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.entity.scene.Scene;

import android.view.KeyEvent;

public class SceneManager extends Scene {

	private static final int SCENE_MAIN_MENU_STATE = 0; 
	private static final int SCENE_SELECT_LEVEL_STATE = 1; 
	private static final int SCENE_GAME_STATE = 2;
	private static int GAME_STATE = 0; 
	
	SceneMainMenu _sceneMainMenu;
	SceneSelectLevel _sceneSelectLevel;
	SceneGame _sceneGame;
	
	public SceneManager(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		_sceneMainMenu = new SceneMainMenu(pVertexBufferObjectManager, this);
		_sceneSelectLevel = new SceneSelectLevel(pVertexBufferObjectManager, this);
		_sceneGame = new SceneGame(this);
		attachChild(_sceneMainMenu);
		attachChild(_sceneSelectLevel);
		attachChild(_sceneGame);
		ShowMainMenu();
		//this.setOnAreaTouchListener(this);
	}
	
	public void ShowMainMenu()
	{
		_sceneSelectLevel.Hide();
		//_sceneGame.Hide();
		_sceneMainMenu.Show();		
		GAME_STATE = SCENE_MAIN_MENU_STATE;
	}
	
	public void ShowSelectLevel()
	{
		_sceneMainMenu.Hide();
		_sceneGame.Hide();
		_sceneSelectLevel.Show();
		GAME_STATE = SCENE_SELECT_LEVEL_STATE;
	}
	
	public void ShowGame(Level level)
	{
		//_sceneMainMenu.Hide();
		_sceneSelectLevel.Hide();
		_sceneGame.Show(level);
		GAME_STATE = SCENE_GAME_STATE;
	}
			
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		switch (GAME_STATE)
		{
			case SCENE_MAIN_MENU_STATE:
				_sceneMainMenu.onSceneTouchEvent(pSceneTouchEvent);
				break;
			case SCENE_SELECT_LEVEL_STATE:
				_sceneSelectLevel.onSceneTouchEvent(pSceneTouchEvent);
				break;
			case SCENE_GAME_STATE:
				_sceneGame.onSceneTouchEvent(pSceneTouchEvent);
				break;
		}
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}	
	
	public void KeyBackPressed(int KeyCode, KeyEvent event)
	{
		switch (GAME_STATE)
		{
			case SCENE_MAIN_MENU_STATE:
				MainGameActivity._main.onDestroy();
				break;
			case SCENE_SELECT_LEVEL_STATE:
				ShowMainMenu();
				break;
			case SCENE_GAME_STATE:
				ShowSelectLevel();
				break;
		}
	}

}
