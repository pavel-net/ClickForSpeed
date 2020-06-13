package com.byPahych.clickforspeed_free_version;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.text.Text;

public class SceneSelectLevel extends CameraScene {
		
	SceneManager parentManager = null;
	ButtonSpriteWithText[] ArrayLevelButton;
	public Text[] Records;
	Text SuperRecord = null;
	private static final int COUNT_LEVELS = 10;
	private static float WIDTH = 220;
	private static float HEIGHT = 50;
	
	public SceneSelectLevel(VertexBufferObjectManager pVertexBufferObjectManager, SceneManager parentManager) {
		super(MainGameActivity.camera);
		setBackgroundEnabled(false);	
		this.parentManager = parentManager;
		HEIGHT = MainGameActivity.CAMERA_HEIGHT / 16.0f;
		CreateButtonLevel(pVertexBufferObjectManager);	
		SuperRecord = new Text(ArrayLevelButton[COUNT_LEVELS - 1].getX() + 30, ArrayLevelButton[COUNT_LEVELS - 1].getY() + 55,
				MainGameActivity.mFontGame, "SUPER RECORD: " + MainGameActivity.SUPER_RECORD, 20, pVertexBufferObjectManager);
		attachChild(SuperRecord);
	}
	
	public void Show()
	{
		setVisible(true);
		setIgnoreUpdate(false);
	}
	
	public void Hide()
	{
		setVisible(false);
		setIgnoreUpdate(true);
	}
	
	private void CreateButtonLevel(VertexBufferObjectManager pVertexBufferObjectManager)
	{
		ArrayLevelButton = new ButtonSpriteWithText[COUNT_LEVELS];
		Records = new Text[COUNT_LEVELS];
		float step = 0;
		for (int i = 0; i < COUNT_LEVELS; i++)
		{
			CreateButton(i, MainGameActivity.CAMERA_WIDTH/2.0f - WIDTH, 10 + step, pVertexBufferObjectManager);
			
			step += 1.5f * HEIGHT;
			Records[i] = new Text(ArrayLevelButton[i].getX() + WIDTH + 40, ArrayLevelButton[i].getY(),
					MainGameActivity.mStrokeFontMenu, "Record: " + MainGameActivity.REAL_DATA_RECORD[i], 20, pVertexBufferObjectManager);
			Records[i].setY(ArrayLevelButton[i].getY() + Records[i].getHeight()/4);
			this.attachChild(Records[i]);			
		}
	}
	
	private void RemoveButton(int index)
	{
		this.unregisterTouchArea(ArrayLevelButton[index]);
		this.detachChild(ArrayLevelButton[index]);
	}
	
	private void CreateButton(int index, float x, float y, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		ArrayLevelButton[index] = new ButtonSpriteWithText(x, y,
				MainGameActivity.mRegionButton, pVertexBufferObjectManager, "Level " + String.valueOf(index+1),
				MainGameActivity.mFontMenu, WIDTH, HEIGHT)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(pSceneTouchEvent.isActionDown())
				{
					this.setCurrentTileIndex(1);
					return true;
				}
				else if(pSceneTouchEvent.isActionUp())
				{
					this.setCurrentTileIndex(0);
					if (this.IsActive)
					{
						Level level = (Level)this.getUserData();
						parentManager.ShowGame(level);
					}
					else
						MainGameActivity._main.makeToast("SORRY, level not available.");
					return true;
				}
				return false;
			}
		};
		ArrayLevelButton[index].setUserData(Level.Levels[index]);
		if (MainGameActivity.REAL_DATA_LEVEL[index].equals("1"))
			ArrayLevelButton[index].IsActive = true;
		else
			ArrayLevelButton[index].setColor(Color.BLUE);
		this.registerTouchArea(ArrayLevelButton[index]);
		this.attachChild(ArrayLevelButton[index]);
	}
	
	public boolean IsNewRecord(int score, int index)
	{
		if (score > Integer.valueOf(MainGameActivity.REAL_DATA_RECORD[index]))
			return true;
		return false;
	}
	
	public void SetNewRecord(int score, int index)
	{
		Records[index].setText("Record: " + String.valueOf(score));
		MainGameActivity.REAL_DATA_RECORD[index] = String.valueOf(score);
	}
	
	public void UpdateNextLevelState(int index)
	{
		if (index == 9)
			return;
		if (ArrayLevelButton[index + 1].IsActive)
			return;		
		//ArrayLevelButton[index + 1].IsActive = true;
		MainGameActivity.REAL_DATA_LEVEL[index + 1] = "1";
		float x = ArrayLevelButton[index+1].getX();
		float y = ArrayLevelButton[index+1].getY();
		RemoveButton(index + 1);
		CreateButton(index + 1, x, y, MainGameActivity._main.getEngine().getVertexBufferObjectManager());
	}
	
	public void UpdateSuperRecord()
	{
		int value = 0;
		for (int i = 0; i < 10; i++)
		{
			value += Integer.valueOf(MainGameActivity.REAL_DATA_RECORD[i]);
		}
		MainGameActivity.SUPER_RECORD = String.valueOf(value);
		this.SuperRecord.setText("SUPER RECORD: " + String.valueOf(value));
	}
}
