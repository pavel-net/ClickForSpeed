package com.byPahych.clickforspeed_free_version;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;




public class SceneMainMenu extends CameraScene {

	SceneManager _parentManager;
	ButtonSpriteWithText Button_start = null;
	ButtonSpriteWithText Button_help = null;
	ITiledTextureRegion pTextureRegionButton = null;
	Text text;
	MainBall mainBall;
	
	public SceneMainMenu(VertexBufferObjectManager pVertexBufferObjectManager, SceneManager _parentManager) {
		super(MainGameActivity.camera);
		setBackgroundEnabled(false);
		this._parentManager = _parentManager;
		this.pTextureRegionButton = MainGameActivity.mRegionButton;
		Button_start = new ButtonSpriteWithText(MainGameActivity.CAMERA_WIDTH/2.0f, MainGameActivity.CAMERA_HEIGHT/2.0f - 30, this.pTextureRegionButton, pVertexBufferObjectManager,
				"Start game", MainGameActivity.mFontMenu, 300, 100)
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
					SceneMainMenu.this._parentManager.ShowSelectLevel();
					return true;
				}
				return false;
			}
		};		
		//Button_start.setScale(2.0f);
		Button_start.setX(MainGameActivity.CAMERA_WIDTH/2.0f - Button_start.getWidth()/2.0f);
		//Button_start.setWidth(1.2f * Button_start.getWidth());
		registerTouchArea(Button_start);
		attachChild(Button_start);	
		Button_help = new ButtonSpriteWithText(Button_start.getX(), Button_start.getY() + Button_start.getHeight() + 20, this.pTextureRegionButton, pVertexBufferObjectManager,
				"HELP", MainGameActivity.mFontMenu, 300, 100)
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
					MainGameActivity._main.runOnUiThread(new Runnable() {
						@Override
						public void run() {		
							MainGameActivity._main.onCreateDialog(4).show();	
						}
					});
					return true;
				}
				return false;
			}
		};		
		//Button_help.setScale(2.0f);
		registerTouchArea(Button_help);
		attachChild(Button_help);	
	
		CreateTitle();
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
	
	public void CreateMainBall(float x, float y)
	{
		mainBall = new MainBall(this, x, y, MainGameActivity.mRegionMainBall, MainGameActivity._main.getEngine().getVertexBufferObjectManager())
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) 
			{
				if(pSceneTouchEvent.isActionDown())
				{
					mainBall.setCurrentTileIndex(1);
					return true;
				}
				if(pSceneTouchEvent.isActionUp())
				{
					mainBall.setCurrentTileIndex(0);
					return true;
				}
				return false;
			}
		};
		mainBall.RemoveBody();
		mainBall.setScale(0.6f);
		mainBall.setY(-40);	
		IEntityModifier pEntityModifier = new RotationModifier(3, 0, 360);
		mainBall.registerEntityModifier(new LoopEntityModifier(pEntityModifier));
		this.registerTouchArea(mainBall);
		this.attachChild(mainBall);
	}
	
	public void CreateTitle()
	{
		VertexBufferObjectManager value = MainGameActivity._main.getEngine().getVertexBufferObjectManager();
		Text _textClick = new Text(MainGameActivity.CAMERA_WIDTH/32, MainGameActivity.CAMERA_HEIGHT/16, MainGameActivity.mStrokeFontStart, "Click", value);
		CreateMainBall(MainGameActivity.CAMERA_WIDTH/4 - 20.0f, MainGameActivity.CAMERA_HEIGHT/32);
		TextOptions opt = new TextOptions(AutoWrap.WORDS, 1.0f, HorizontalAlign.CENTER);
		Text _textForSpeed = new Text(mainBall.getX() + mainBall.getWidth() + 20.0f, MainGameActivity.CAMERA_HEIGHT/16, MainGameActivity.mStrokeFontStart, "For Speed", opt, value);
		Sprite spriteByPahych = new Sprite(-30, 100, MainGameActivity.mRegionByPahych, value);
		//Sprite spriteAndEngine = new Sprite(Button_help.getX(), Button_help.getY() + Button_help.getHeight() + 50, MainGameActivity.mRegionAndEngine, value);
		//spriteAndEngine.setX(MainGameActivity.CAMERA_WIDTH/2.0f - spriteAndEngine.getWidth()/2);
		this.attachChild(_textClick);
		this.attachChild(_textForSpeed);
		this.attachChild(spriteByPahych);
		//this.attachChild(spriteAndEngine);
	}
}
