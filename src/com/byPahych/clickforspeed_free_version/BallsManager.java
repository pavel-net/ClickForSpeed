package com.byPahych.clickforspeed_free_version;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;








import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class BallsManager {

	private static final short CATEGORYBIT_WALL = 1;
	private static final short CATEGORYBIT_BALL = 2;
	private static final short CATEGORYBIT_FACE = 4;
	private static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_BALL;
	private static final short MASKBITS_BALL = CATEGORYBIT_WALL + CATEGORYBIT_FACE;
	private static final short MASKBITS_FACE = CATEGORYBIT_FACE + CATEGORYBIT_BALL;
	
	public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL, MASKBITS_WALL, (short)0);
	public static final FixtureDef BALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.8f, 0.5f, false, CATEGORYBIT_BALL, MASKBITS_BALL, (short)0);
	
	public static final FixtureDef DEFAULT_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_FACE, MASKBITS_FACE, (short)0);
	private static Scene parentScene;
	
	private Ball[] Balls = null;
	public SpeedBall[] SpeedBalls = null;
	public DeformBall[] DeformBalls = null;
	public InvisibleBall[] InvisibleBalls = null;
	
	private float X_DEFAULT;
	private float Y_DEFAULT;
	
	private float min_x;
	private float max_x;
	private float min_y;
	private float max_y;
	private int SIZE_POOL = 3; 
	
	private float  frequence_balls = 0;
	private float  frequence_speed_balls = 0;
	private float  frequence_deform_balls = 0;
	private float  frequence_invisible_balls = 0;
	// обычный, скоростной, деформирующий, невидимый
	private float[]  Border;
	// обычный, скоростной, деформирующий, невидимый
	private float[] Speed = null;
	// обычный, скоростной, деформирующий, невидимый
	private float[] Scale = null;
	private float  sign = 1;
	private float  speed = 10.0f;
	
	private float pTimerSeconds;
	private boolean x_static = false;
	private boolean y_static = false;
	private int current_ball = 0;
	private TimerHandler timerUpdate = null;

	public BallsManager(float min_x, float max_x, float min_y, float max_y, float freqBalls,
			float freqSpeedBalls, float freqDeformBalls, float freqInvisibleBalls, float pTimerSeconds,
			ITiledTextureRegion pRegionBalls, ITiledTextureRegion pRegionSpeedBalls,
			ITiledTextureRegion pRegionDeformBalls, ITiledTextureRegion pRegionInvisibleBalls,
			int SIZE_POOL, float speed, Scene scene, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this.SIZE_POOL = SIZE_POOL;
		this.speed = speed;
		this.X_DEFAULT = 0;
		this.Y_DEFAULT = MainGameActivity.CAMERA_HEIGHT + 100.0f;
		parentScene = scene;
		Initial(min_x, max_x, min_y, max_y, freqBalls,
				freqSpeedBalls, freqDeformBalls, freqInvisibleBalls, pTimerSeconds);
		CreateBalls(pRegionBalls, pRegionSpeedBalls, pRegionDeformBalls, pRegionInvisibleBalls, pVertexBufferObjectManager);
	}
	
	public BallsManager(float min_x, float max_x, float min_y, float max_y, float freqBalls,
			float freqSpeedBalls, float freqDeformBalls, float freqInvisibleBalls, float pTimerSeconds,
			ITiledTextureRegion pRegionBalls, ITiledTextureRegion pRegionSpeedBalls,
			ITiledTextureRegion pRegionDeformBalls, ITiledTextureRegion pRegionInvisibleBalls,
			int SIZE_POOL, float[] Speed, float[] Scale, Scene scene, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		this.SIZE_POOL = SIZE_POOL;
		this.Speed = new float[]{Speed[0], Speed[1], Speed[2], Speed[3]};
		this.Scale = new float[]{Scale[0], Scale[1], Scale[2], Scale[3]};
		this.X_DEFAULT = 0;
		this.Y_DEFAULT = MainGameActivity.CAMERA_HEIGHT + 100.0f;
		parentScene = scene;
		Initial(min_x, max_x, min_y, max_y, freqBalls,
				freqSpeedBalls, freqDeformBalls, freqInvisibleBalls, pTimerSeconds);
		CreateBalls(pRegionBalls, pRegionSpeedBalls, pRegionDeformBalls, pRegionInvisibleBalls, pVertexBufferObjectManager);
	}
	
	private void Initial(float min_x, float max_x, float min_y, float max_y, float freqBalls,
			float freqSpeedBalls, float freqDeformBalls, float freqInvisibleBalls, float pTimerSeconds)
	{
		if (freqBalls + freqDeformBalls + freqInvisibleBalls + freqSpeedBalls != 1.0f)
		{
			this.frequence_balls = 1.0f;			
		}
		else
		{
			this.frequence_balls = freqBalls;
			this.frequence_deform_balls = freqDeformBalls;
			this.frequence_invisible_balls = freqInvisibleBalls;
			this.frequence_speed_balls = freqSpeedBalls;
		}
		// Border - задаёт распределение вероятностей появления определённого типа шара
		// по параметрам frequence, заданным пользователем
		Border = new float[4];
		for (int i = 0; i < 4; i++)
			Border[i] = 0;
		float value_summ = 0;
		Border[0] = this.frequence_balls;
		value_summ += Border[0];
		if (this.frequence_speed_balls != 0)
		{
			Border[1] = this.frequence_speed_balls + value_summ;
			value_summ += Border[1];
		}
		if (this.frequence_deform_balls != 0)
		{
			Border[2] = this.frequence_deform_balls + value_summ;
			value_summ += Border[2];
		}
		if (this.frequence_invisible_balls != 0)
			Border[3] = this.frequence_invisible_balls + value_summ;

		
		this.min_x = min_x;
		this.max_x = max_x;
		this.min_y = min_y;
		this.max_y = max_y;
		if (this.min_x == this.max_x)
		{
			x_static = true;
			if (this.min_x > 0)
				this.sign = -1;
		}
		if (this.min_y == this.max_y)
		{
			y_static = true;
			if (this.min_y > 0)
				this.sign = -1;
		}
		this.pTimerSeconds = pTimerSeconds;
	}
	
	private void CreateBalls(ITiledTextureRegion pRegionBalls, ITiledTextureRegion pRegionSpeedBalls,
			ITiledTextureRegion pRegionDeformBalls, ITiledTextureRegion pRegionInvisibleBalls,
			VertexBufferObjectManager pVertexBufferObjectManager)
	{					
		if (frequence_balls != 0)
		{
			this.Balls = new Ball[SIZE_POOL];
			float scale = 1.0f;
			if (Scale != null)
				scale = Scale[0];
			for (int i = 0; i < SIZE_POOL; i++)
			{
				Balls[i] = new Ball(X_DEFAULT, Y_DEFAULT,
						pRegionBalls, pVertexBufferObjectManager, DEFAULT_DEF, scale);
				parentScene.attachChild(Balls[i]);
			}
		}
		if (frequence_speed_balls != 0)
		{
			this.SpeedBalls = new SpeedBall[SIZE_POOL];
			float scale = 1.0f;
			if (Scale != null)
				scale = Scale[1];
			for (int i = 0; i < SIZE_POOL; i++)
			{
				SpeedBalls[i] = new SpeedBall(X_DEFAULT, Y_DEFAULT,
						pRegionSpeedBalls, pVertexBufferObjectManager, DEFAULT_DEF, scale)
				{
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) 
					{
						if(pSceneTouchEvent.isActionDown()) {	
							if (!this.isAnimationRunning() && !SceneGame.flag_exit)
							{
								this.StartKillAnimation();
								SceneGame.score += 3;
								SceneGame.TextScore.setText(String.valueOf(SceneGame.score));
								CreateModificatorText(this);
							}
							return true;
						}
						return false;
					}
				};
				SpeedBall temp = SpeedBalls[i];
				parentScene.registerTouchArea(temp);
				parentScene.attachChild(temp);
				
			}
		}
		if (frequence_deform_balls != 0)
		{
			this.DeformBalls = new DeformBall[SIZE_POOL];
			float scale = 1.0f;
			if (Scale != null)
				scale = Scale[2];
			for (int i = 0; i < SIZE_POOL; i++)
			{
				DeformBalls[i] = new DeformBall(X_DEFAULT, Y_DEFAULT,
						pRegionDeformBalls, pVertexBufferObjectManager, DEFAULT_DEF, scale)
				{
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) 
					{
						if(pSceneTouchEvent.isActionDown()) {	
							if (!this.isAnimationRunning() && !SceneGame.flag_exit)
							{
								this.StartKillAnimation();
								SceneGame.score += 3;
								SceneGame.TextScore.setText(String.valueOf(SceneGame.score));
								CreateModificatorText(this);
							}
							return true;
						}
						return false;
					}
				};
				DeformBall temp = DeformBalls[i];
				parentScene.registerTouchArea(temp);
				parentScene.attachChild(temp);
			}
		}
		if (frequence_invisible_balls != 0)
		{
			this.InvisibleBalls = new InvisibleBall[SIZE_POOL];
			float scale = 1.0f;
			if (Scale != null)
				scale = Scale[3];
			for (int i = 0; i < SIZE_POOL; i++)
			{
				InvisibleBalls[i] = new InvisibleBall(X_DEFAULT, Y_DEFAULT,
						pRegionInvisibleBalls, pVertexBufferObjectManager, DEFAULT_DEF, scale)
				{
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) 
					{
						if(pSceneTouchEvent.isActionDown()) {
							if (!this.isAnimationRunning() && !SceneGame.flag_exit)
							{
								this.StartKillAnimation();
								SceneGame.score += 3;
								SceneGame.TextScore.setText(String.valueOf(SceneGame.score));
								CreateModificatorText(this);
							}
							return true;
						}
						return false;
					}
				};
				parentScene.registerTouchArea(InvisibleBalls[i]);
				parentScene.attachChild(InvisibleBalls[i]);
			}
		}
	}
	
	// Удаляет отработанные шары со сцены
	private void ReleaseBallsInOffCamera(Ball[] Balls)
	{
		for (int i = 0; i < SIZE_POOL; i++)
		{
			if (!Balls[i].IsReady())
			{
				if (Balls[i].IsOffScene())
				{
					Balls[i].SetRelease();
				}
			}
		}		
	}
	// Запускает очередной шар
	private void StartNextBall(Ball[] Balls, float speed)
	{
		if (Balls[current_ball].IsReady())
		{
			float start;
			if (y_static)
				start = MathUtils.random(0, max_x - 50.0f);
			else if (x_static)
				start = MathUtils.random(0, max_y - 50.0f);
			else
				return;
			float factor_speed = MathUtils.random(0, speed / 2.0f);
			if (y_static)
			{
				if (start > (max_x - 50.0f) / 2.0f)
					factor_speed *= -1;
				Balls[current_ball].SetNewPosition(start, max_y - sign * 100.0f);
				Balls[current_ball].SetMove(new Vector2(factor_speed, speed * sign));
			}
			else
			{
				if (start > (max_y - 50.0f) / 2.0f)
					factor_speed *= -1;
				Balls[current_ball].SetNewPosition(max_x - sign * 100.0f, start);
				Balls[current_ball].SetMove(new Vector2(speed * sign, factor_speed));
			}			
			Balls[current_ball].SetBusy();
			if (current_ball == SIZE_POOL - 1)
				current_ball = 0;
			else
				current_ball++;
		}
	}
	// Осуществляет цикл обновления шаров на сцене
	private void UpdateBallState()
	{
		if (frequence_balls != 0)
			ReleaseBallsInOffCamera(this.Balls);
		if (frequence_deform_balls != 0)
			ReleaseBallsInOffCamera(this.DeformBalls);
		if (frequence_invisible_balls != 0)
			ReleaseBallsInOffCamera(this.InvisibleBalls);
		if (frequence_speed_balls != 0)
			ReleaseBallsInOffCamera(this.SpeedBalls);
		
		float value_ball = MathUtils.random(0.02f, 1.4f);
		if (value_ball < Border[0])
		{
			if (Speed == null)
				StartNextBall(this.Balls, speed);
			else
				StartNextBall(this.Balls, Speed[0]);
		}
		else if (value_ball < Border[1])
		{
			if (Speed == null)
				StartNextBall(this.SpeedBalls, speed);
			else
				StartNextBall(this.SpeedBalls, Speed[1]);
		}
		else if (value_ball < Border[2])
		{
			if (Speed == null)
				StartNextBall(this.DeformBalls, speed);
			else
				StartNextBall(this.DeformBalls, Speed[2]);
		}
		else if (value_ball < Border[3])
		{
			if (Speed == null)
				StartNextBall(this.InvisibleBalls, speed);		
			else
				StartNextBall(this.InvisibleBalls, Speed[3]);
		}
	}
	// Создаёт и активирует таймер
	private void createBallsTimer()
	{
		timerUpdate = new TimerHandler(pTimerSeconds, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				UpdateBallState();
				pTimerHandler.reset();				
			}
		});
		
		parentScene.registerUpdateHandler(timerUpdate);
	}
	
	public static void CollisionAction(Body body)
	{
			if (body.getUserData() instanceof com.byPahych.clickforspeed_free_version.SpeedBall)
			{
				SpeedBall ball = (SpeedBall)body.getUserData();
				ball.StartAnimation();
				if (SceneGame.mainBall != null)
					SceneGame.mainBall.Acceleration(3.0f);
			}
			else if (body.getUserData() instanceof com.byPahych.clickforspeed_free_version.DeformBall)
			{
				DeformBall ball = (DeformBall)body.getUserData();
				ball.StartAnimation();
				if (SceneGame.mainBall.getScaleX() > 0.4f)
				{
					ScaleModifier mod = new ScaleModifier(0.5f, SceneGame.mainBall.getScaleX(), SceneGame.mainBall.getScaleX()/1.5f)
					{
				        @Override
				        protected void onModifierFinished(IEntity pItem)
				        {
			                super.onModifierFinished(pItem);			                
			                SceneGame.mainBall.Resize(SceneGame.mainBall.getScaleX());
				        }
					};
					SceneGame.mainBall.registerEntityModifier(mod);
					
					TimerHandler th = new TimerHandler(5.0f, new ITimerCallback()
					{      
						@Override
						public void onTimePassed(final TimerHandler pTimerHandler)
						{
							parentScene.unregisterUpdateHandler(pTimerHandler);
							if (SceneGame.mainBall != null && SceneGame.mainBall.getScaleX() < SceneGame.mainBall.real_size)
								SceneGame.mainBall.Resize(SceneGame.mainBall.getScaleX()*1.5f);
						}
					});    
					parentScene.registerUpdateHandler(th);
				}	
			}
			else if (body.getUserData() instanceof com.byPahych.clickforspeed_free_version.InvisibleBall)
			{				
				InvisibleBall ball = (InvisibleBall)body.getUserData();
				ball.StartAnimation();
				if (SceneGame.mainBall != null && SceneGame.mainBall.getAlpha() == 1.0f)
				{				
					AlphaModifier mod = new AlphaModifier(2, 1, 0)			
					{
				        @Override
				        protected void onModifierFinished(IEntity pItem)
				        {
			                super.onModifierFinished(pItem);
			                SceneGame.mainBall.Resize(SceneGame.mainBall.getScaleX());
				        }
					};
					SceneGame.mainBall.registerEntityModifier(mod);
					
					TimerHandler th = new TimerHandler(4.0f, new ITimerCallback()
					{      
						@Override
						public void onTimePassed(final TimerHandler pTimerHandler)
						{
							parentScene.unregisterUpdateHandler(pTimerHandler);
							SceneGame.mainBall.setAlpha(1.0f);
						}
					});    
					parentScene.registerUpdateHandler(th);
				}
			}
			else if (body.getUserData() instanceof com.byPahych.clickforspeed_free_version.Ball)
			{

			}
	}
	
	public void Start()
	{
		createBallsTimer();
	}
	
	public void DeleteManager()
	{
		if (timerUpdate != null)
			parentScene.unregisterUpdateHandler(timerUpdate);
		for (int i = 0; i < SIZE_POOL; i++)
		{
			if (Balls != null)
			{
				Balls[i].SetRelease();
				parentScene.detachChild(Balls[i]);
			}
			if (SpeedBalls != null)
			{
				SpeedBalls[i].SetRelease();
				parentScene.unregisterTouchArea(SpeedBalls[i]);
				parentScene.detachChild(SpeedBalls[i]);
			}
			if (DeformBalls != null)
			{
				DeformBalls[i].SetRelease();
				parentScene.unregisterTouchArea(DeformBalls[i]);
				parentScene.detachChild(DeformBalls[i]);
			}
			if (InvisibleBalls != null)
			{
				InvisibleBalls[i].SetRelease();
				parentScene.unregisterTouchArea(InvisibleBalls[i]);
				parentScene.detachChild(InvisibleBalls[i]);
			}			
		}
	}
	
	public void CreateModificatorText(Ball ball)
	{
		//mFontGame
		final Text text = new Text(ball.getX(), ball.getY(), MainGameActivity.mFontGame, "+3", MainGameActivity._main.getVertexBufferObjectManager());
		
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
	}
	
	public void AddText(Text text)
	{
		parentScene.attachChild(text);
	}
	
	public void RemoveText(final Text text)
	{
		MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    {
	        public void run()
	        {
	        	parentScene.detachChild(text);
	        }
	    });
		
	}
}
