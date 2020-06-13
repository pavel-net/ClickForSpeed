package com.byPahych.clickforspeed_free_version;

import java.util.Stack;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class SceneGame extends CameraScene implements IOnAreaTouchListener, IOnSceneTouchListener {

	public static Level level;
	private int time;
	SceneManager parentScene;
	
	private BallsManager ballsManager_left = null;
	private BallsManager ballsManager_right = null;
	private BallsManager ballsManager_ground = null;
	
	private Rectangle ground = null;
	private Rectangle roof = null;
	private Rectangle left = null;
	private Rectangle right = null;
	
	public static boolean flag_exit = false;
	
	private Sprite HUD_sprite;
	private HUD myHUD = null;
	
	public static Text TextScore;
	public static Text TextLevel;
	public static Text TextTime;
	public static Text TextTimeValue;
	public static Text TextRecord;
	public static Text TextNeedPoints;
	
	public static MainBall mainBall = null;
	private TimerHandler TimerUpdateSpeed = null;
	private TimerHandler TimerFinishGame = null;
	private TimerHandler TimerTimeTick = null;
	private IUpdateHandler UpdateScene = null;
	private int count_click = 0;
	public static int score = 0;
	
	private boolean loaded = false;
	private Stack<Body> _stack_delete = null;
		
	
	public SceneGame(SceneManager parentScene) {
		super(MainGameActivity.camera);
		this.parentScene = parentScene;
		setBackgroundEnabled(false);	
		this.setOnAreaTouchListener(this);
		this.setOnSceneTouchListener(this);
	}
	
	public void Show(Level level_in)
	{
		flag_exit = false;
		level = level_in;
		time = level.count_seconds;
		Start(MainGameActivity._main.getVertexBufferObjectManager());
		setVisible(true);
		setIgnoreUpdate(false);
		//MainGameActivity._sceneManager.setBackground(new Background(0.4f, 0.7f - (level.NumLevel - 1)/18.0f, 0.9f - (level.NumLevel - 1)/18.0f));
	}


	public void Hide()
	{
		//MainGameActivity._sceneManager.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		MainGameActivity._sceneManager.setBackground(new Background(0.4f, 0.7f, 0.9f));
		ReleaseScene();
		setVisible(false);
		setIgnoreUpdate(true);
	}
	
	private void CreateRectangles(VertexBufferObjectManager vertexBufferObjectManager)
	{
		ground = new Rectangle(0, MainGameActivity.CAMERA_HEIGHT - 2, MainGameActivity.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		roof = new Rectangle(0, HUD_sprite.getHeight(), MainGameActivity.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		left = new Rectangle(0, 0, 2, MainGameActivity.CAMERA_HEIGHT, vertexBufferObjectManager);
		right = new Rectangle(MainGameActivity.CAMERA_WIDTH - 2, 0, 2, MainGameActivity.CAMERA_HEIGHT, vertexBufferObjectManager);
		ground.setColor(0, 0, 0);
		roof.setColor(0, 0, 0);
		left.setColor(0, 0, 0);
		right.setColor(0, 0, 0);
		PhysicsFactory.createBoxBody(MainGameActivity.mPhysicsWorld, ground, BodyType.StaticBody, BallsManager.WALL_FIXTURE_DEF).setUserData("RECTANGLE");
		PhysicsFactory.createBoxBody(MainGameActivity.mPhysicsWorld, roof, BodyType.StaticBody, BallsManager.WALL_FIXTURE_DEF).setUserData("RECTANGLE");
		PhysicsFactory.createBoxBody(MainGameActivity.mPhysicsWorld, left, BodyType.StaticBody, BallsManager.WALL_FIXTURE_DEF).setUserData("RECTANGLE");
		PhysicsFactory.createBoxBody(MainGameActivity.mPhysicsWorld, right, BodyType.StaticBody, BallsManager.WALL_FIXTURE_DEF).setUserData("RECTANGLE");		
		this.attachChild(ground);
		this.attachChild(roof);
		this.attachChild(left);
		this.attachChild(right);
	}
	
	private void CreateHUD(VertexBufferObjectManager vertexBufferObjectManager)
	{
		myHUD = new HUD();
		HUD_sprite = new Sprite(0, 0, MainGameActivity.mRegionHUD, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) 
			{
				return true;
			}	
		};
		HUD_sprite.setWidth(MainGameActivity.CAMERA_WIDTH);
		HUD_sprite.setHeight(MainGameActivity.CAMERA_HEIGHT / 9.0f);
		myHUD.attachChild(HUD_sprite);
		Text _text0 = new Text(HUD_sprite.getWidth()/32, HUD_sprite.getHeight()/4, MainGameActivity.mFontGame, "SCORE: ", vertexBufferObjectManager);
		TextScore = new Text(HUD_sprite.getWidth()/32 + _text0.getWidth(), HUD_sprite.getHeight()/2, MainGameActivity.mFontGame, "0", 8, vertexBufferObjectManager);
		TextScore.setY(HUD_sprite.getHeight() - TextScore.getHeight() - HUD_sprite.getHeight()/16);
		_text0.setY(HUD_sprite.getHeight() - TextScore.getHeight() - HUD_sprite.getHeight()/16);
		
		TextNeedPoints = new Text(HUD_sprite.getWidth()/32, HUD_sprite.getHeight()/4, MainGameActivity.mStrokeFontMenu, "Need points: " + String.valueOf(level.need_points), vertexBufferObjectManager);
		TextNeedPoints.setY(HUD_sprite.getHeight()/4 - TextNeedPoints.getHeight()/2);
		
		TextLevel = new Text(HUD_sprite.getWidth()/1.3f, HUD_sprite.getHeight()/4, MainGameActivity.mStrokeFontMenu, "Level " + String.valueOf(level.NumLevel), vertexBufferObjectManager);
		TextLevel.setY(HUD_sprite.getHeight()/4 - TextLevel.getHeight()/2);
		
		TextRecord = new Text(HUD_sprite.getWidth()/2, HUD_sprite.getHeight()/4, MainGameActivity.mStrokeFontMenu, "Record:" + MainGameActivity.REAL_DATA_RECORD[level.NumLevel - 1], vertexBufferObjectManager);
		TextRecord.setY(HUD_sprite.getHeight()/4 - TextRecord.getHeight()/2);
		TextRecord.setX(HUD_sprite.getWidth()/2 - TextRecord.getWidth()/8);
		
		TextTime = new Text(HUD_sprite.getWidth()/1.4f, HUD_sprite.getHeight()/2, MainGameActivity.mFontTime, "Time: ", vertexBufferObjectManager);
		TextTime.setY(HUD_sprite.getHeight()/2);
		
		TextTimeValue = new Text(HUD_sprite.getWidth()/1.3f, TextTime.getY(), MainGameActivity.mFontTime, String.valueOf(time), 8, vertexBufferObjectManager);
		TextTimeValue.setX(TextTime.getX() + TextTime.getWidth());
		
		myHUD.attachChild(_text0);
		myHUD.attachChild(TextScore);
		myHUD.attachChild(TextNeedPoints);
		myHUD.attachChild(TextLevel);
		myHUD.attachChild(TextRecord);
		myHUD.attachChild(TextTime);
		myHUD.attachChild(TextTimeValue);
		MainGameActivity.camera.setHUD(myHUD);
		this.registerTouchArea(HUD_sprite);
	}
	
	private void CreateMainBall(VertexBufferObjectManager vertexBufferObjectManager)
	{
		mainBall = new MainBall(this, MainGameActivity.CAMERA_WIDTH/4.0f, MainGameActivity.CAMERA_HEIGHT/4.0f, MainGameActivity.mRegionMainBall, vertexBufferObjectManager)
		{
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) 
			{
				if(pSceneTouchEvent.isActionDown()) {	
					if (flag_exit)
						return true;
					mainBall.setCurrentTileIndex(1);
					count_click++;
					score++;
					TextScore.setText(String.valueOf(score));
					final Text text = new Text(mainBall.getX(), mainBall.getY(), MainGameActivity.mFontGame, "+1", MainGameActivity._main.getVertexBufferObjectManager());
					IEntityModifier pEntityModifier = new SequenceEntityModifier(  
				                new ParallelEntityModifier(  
				                    new AlphaModifier(0.5f, 0.0f, 1.0f),  
				                    new ScaleModifier(0.5f, 0.5f, 1.5f)))
					{
				        @Override
				        protected void onModifierFinished(IEntity pItem)
				        {
			                super.onModifierFinished(pItem);
			                RemoveText(text);
				        }
					};				
					text.registerEntityModifier(pEntityModifier);  					
					AddText(text);  
					return true;
				}
				if (pSceneTouchEvent.isActionUp())
				{
					mainBall.setCurrentTileIndex(0);
					return true;
				}
				return false;
			}
		};
		mainBall.setCurrentTileIndex(0);
		this.registerTouchArea(mainBall);
		this.attachChild(mainBall);
	}
	
	private void CreateBallsManager(VertexBufferObjectManager vertexBufferObjectManager)
	{		
		ballsManager_left = level.GetLeftSettings(this, vertexBufferObjectManager, 0, 0, HUD_sprite.getHeight(), MainGameActivity.CAMERA_HEIGHT);
		ballsManager_right = level.GetRightSettings(this, vertexBufferObjectManager, MainGameActivity.CAMERA_WIDTH,
				MainGameActivity.CAMERA_WIDTH, HUD_sprite.getHeight(), MainGameActivity.CAMERA_HEIGHT);
		ballsManager_ground = level.GetGroundSettings(this, vertexBufferObjectManager, 0, MainGameActivity.CAMERA_WIDTH,
				MainGameActivity.CAMERA_HEIGHT, MainGameActivity.CAMERA_HEIGHT);
	}
	
	private void CreateUpdateSpeedTimeHandler()
	{
		TimerUpdateSpeed = new TimerHandler(0.5f, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				if (flag_exit)
					return;
				mainBall.UpdateSpeedBall(count_click);
				count_click = 0;				
				pTimerHandler.reset();				
			}
		});
		
		this.registerUpdateHandler(TimerUpdateSpeed);
	}
	
	private void CreateTimeTickHandler()
	{
		TimerTimeTick = new TimerHandler(1.0f, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				if (flag_exit)
					return;
				time--;
				if (time == 0)
				{
					EndGame();
				}
				if (time == 20)
				{
					MainGameActivity._main.LoadAdvert();
					myHUD.detachChild(TextTimeValue);
					TextTimeValue = new Text(HUD_sprite.getWidth()/1.3f, TextTime.getY(), MainGameActivity.mFontBad, String.valueOf(time), MainGameActivity._main.getEngine().getVertexBufferObjectManager());
					TextTimeValue.setX(TextTime.getX() + TextTime.getWidth());
					myHUD.attachChild(TextTimeValue);
				}				
				TextTimeValue.setText(String.valueOf(time));
				pTimerHandler.reset();				
			}
		});
		
		this.registerUpdateHandler(TimerTimeTick);
	}
		
	private void CreateUpdateScene()
	{
		registerUpdateHandler(UpdateScene = new IUpdateHandler() {		
			@Override
			public void reset() {			
			}		
			@Override
			public void onUpdate(float pSecondsElapsed) 
			{
				DeleteBodyOnCollision();
			}
		});
	}
	private void DeleteBodyOnCollision()
	{
		if (!_stack_delete.isEmpty())
		{
			Object[] BodyDelete = _stack_delete.toArray();
			
			for (int i = 0; i < BodyDelete.length; i++)
			{
				BallsManager.CollisionAction((Body)BodyDelete[i]);
			}
			_stack_delete.clear();
		}			
	}
	
	private ContactListener createContactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        @Override
	        public void beginContact(Contact contact)
	        {
	        }
	        @Override
	        public void endContact(Contact contact)
	        {
	        	final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();	            
	            final Body body1 = x1.getBody();
	            final Body body2 = x2.getBody();
//	            String data1 = "";
//	            String data2 = "";
//	            if (body1.getUserData() != null)
//	            	data1 = body1.getUserData().getClass().getName();
//	            if (body2.getUserData() != null)
//	            	data2 = body2.getUserData().getClass().getName();
//	            String value = data1 + " " + data2;
//	            if (body2.getUserData() instanceof java.lang.String)
//	            	MainGameActivity._main.makeToast("RECTANGLE or MAINBALL");
	            if (body1.getUserData() != null && body2.getUserData() != null)
	            {	            	
//		            if (body1.getUserData().getClass().getName().equals("java.lang.String") && !body2.getUserData().getClass().getName().equals("org.andengine.entity.primitive.Rectangle"))
//		            {
//		            	_stack_delete.add(body2);
//		            }
	            	if (body1.getUserData().getClass().getName().equals("java.lang.String") && !body2.getUserData().getClass().getName().equals("java.lang.String"))
		            {
		            	_stack_delete.add(body2);
		            }
	            }
	        }
	        @Override
	        public void preSolve(Contact contact, Manifold oldManifold)
	        {
	        }
	        @Override
	        public void postSolve(Contact contact, ContactImpulse impulse)
	        {
	        }                                                 
	        
	    };
	    return contactListener;
	}
	
	public void Start(VertexBufferObjectManager vertexBufferObjectManager)
	{
		this.registerUpdateHandler(MainGameActivity.mPhysicsWorld);		
		loaded = true;		
		score = 0;
		CreateHUD(vertexBufferObjectManager);
		CreateRectangles(vertexBufferObjectManager);
		CreateBallsManager(vertexBufferObjectManager);
		CreateMainBall(vertexBufferObjectManager);
		if (ballsManager_ground != null)
			ballsManager_ground.Start();
		if (ballsManager_left != null)
			ballsManager_left.Start();
		if (ballsManager_right != null)
			ballsManager_right.Start();
		CreateUpdateSpeedTimeHandler();
		CreateTimeTickHandler();
		_stack_delete = new Stack<Body>(); 
		MainGameActivity.mPhysicsWorld.setContactListener(createContactListener());
		CreateUpdateScene();
	}
	

	public void AddText(Text text)
	{
		this.attachChild(text);
	}
	
	public void RemoveText(final Text text)
	{
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	detachChild(text);
	        }
	    });
		
	}
	
	private void ReleaseScene()
	{
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	if (ballsManager_ground != null)
	    			ballsManager_ground.DeleteManager();
	    		if (ballsManager_right != null)
	    			ballsManager_right.DeleteManager();
	    		if (ballsManager_left != null)
	    			ballsManager_left.DeleteManager();
	    		ballsManager_ground = null;
	    		ballsManager_right = null;
	    		ballsManager_left = null;	    		
	    		if (ground != null)
	    			detachChild(ground);
	    		if (roof != null)
	    			detachChild(roof);
	    		if (left != null)
	    			detachChild(left);
	    		if (right != null)
	    			detachChild(right);		
	    		MainGameActivity.mPhysicsWorld.clearPhysicsConnectors();
	    		if (myHUD != null)
	    		{
	    			myHUD.detachChildren();
	    			MainGameActivity.camera.setHUD(new HUD());
	    		}
	    		if (TimerUpdateSpeed != null)
	    		{
	    			unregisterUpdateHandler(TimerUpdateSpeed);
	    			TimerUpdateSpeed = null;
	    		}
	    		if (TimerFinishGame != null)
	    		{
	    			unregisterUpdateHandler(TimerFinishGame);
	    			TimerFinishGame = null;
	    		}
	    		if (TimerTimeTick != null)
	    		{
	    			unregisterUpdateHandler(TimerTimeTick);
	    			TimerTimeTick = null;
	    		}
	    		if (mainBall != null)
	    			mainBall.ReleaseMainBall();		
	    		mainBall = null;
	    		TextScore = null;
	    		myHUD = null;
	    		ground = null;
	    		left = null;
	    		right = null;
	    		roof = null;
	    		//score = 0;
	    		count_click = 0;
	    		if (loaded)
	    		{
	    			unregisterUpdateHandler(UpdateScene);
	    			unregisterUpdateHandler(MainGameActivity.mPhysicsWorld);
	    			loaded = false;
	    		}
	    		System.gc();
	        }
	    });
	}

	private void EndGame()
	{		
		flag_exit = true;
		MainGameActivity._sceneManager.setBackground(new Background(0.8f, 0.3f, 0.3f));
		MainGameActivity._main.mVibrator.vibrate(1000);
		TimerFinishGame = new TimerHandler(5.0f, new ITimerCallback()
		{			
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{				
				MainGameActivity._main.runOnUiThread(new Runnable() {		
					@Override
					public void run() {		
						// если поставили новый рекорд				
						if (parentScene._sceneSelectLevel.IsNewRecord(score, level.NumLevel - 1))
						{
							parentScene._sceneSelectLevel.SetNewRecord(score, level.NumLevel - 1);
							parentScene._sceneSelectLevel.UpdateSuperRecord();
							MainGameActivity._main.SaveInfo();
						}
						// если набрали нужное количество очков и достигли последнего уровня
						if (score >= level.need_points && level.NumLevel == 10)
						{
							MainGameActivity._main.onCreateDialog(3).show();
							//MainGameActivity._main.SaveInfo();
						}
						// выиграли!
						else if (score >= level.need_points)
						{
							MainGameActivity._main.onCreateDialog(1).show();
							parentScene._sceneSelectLevel.UpdateNextLevelState(level.NumLevel - 1);
							MainGameActivity._main.SaveInfo();
						}				
						// проиграли
						else
							MainGameActivity._main.onCreateDialog(2).show();					
					}				
				});
			}
		});
		
		this.registerUpdateHandler(TimerFinishGame);		
	}
	
	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {		
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			if (score == 0)
				return true;
			if (flag_exit)
				return true;
			MainGameActivity._main.mVibrator.vibrate(100);
			score--;
			TextScore.setText(String.valueOf(score));
			final Text text = new Text(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), MainGameActivity.mFontBad, "-1", MainGameActivity._main.getVertexBufferObjectManager());
			IEntityModifier pEntityModifier = new SequenceEntityModifier(  
		                new ParallelEntityModifier(  
		                    new AlphaModifier(0.5f, 0.0f, 1.0f),  
		                    new ScaleModifier(0.5f, 0.5f, 1.5f)))
			{
		        @Override
		        protected void onModifierFinished(IEntity pItem)
		        {
	                super.onModifierFinished(pItem);
	                RemoveText(text);
		        }
			};				
			text.registerEntityModifier(pEntityModifier);  					
			AddText(text);  
			return true;
		}
		if(pSceneTouchEvent.isActionUp())
		{
			mainBall.setCurrentTileIndex(0);
			return true;
		}
		return false;
	}
}
