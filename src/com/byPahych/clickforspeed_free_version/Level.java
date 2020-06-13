package com.byPahych.clickforspeed_free_version;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Level {
	public int count_seconds = 60;
	public int need_points = 10;
	
	public int NumLevel;
	private boolean IsGroundEnable;
	private boolean IsLeftEnable;
	private boolean IsRightEnable;
	private float[] Frequences_ground;
	private float[] Frequences_left;
	private float[] Frequences_right;
	// обычный, скоростной, деформирующий, невидимый	
	private float[] Speed = new float[4];
	private float[] Scale = new float[4];
	// низ, лево, право
	private float[] Times = new float[3];
	private static final int SIZE_POOL = 4; 
	
	public Level(int num, boolean IsGroundEnable, boolean IsLeftEnable, boolean IsRightEnable, int count_seconds, int need_points)
	{
		this.NumLevel = num;
		this.count_seconds = count_seconds;
		this.need_points = need_points;
		this.IsGroundEnable = IsGroundEnable;
		this.IsLeftEnable = IsLeftEnable;
		this.IsRightEnable = IsRightEnable;
		for (int i = 0; i < 4; i++)
		{
			Speed[i] = 0;
			Scale[i] = 0;
		}
	}
	
	public void SetSpeedSettings(float ball, float speed_ball, float deform_ball, float invisible_ball)
	{
		Speed[0] = ball;
		Speed[1] = speed_ball;
		Speed[2] = deform_ball;
		Speed[3] = invisible_ball;
	}
	
	public void SetScaleSettings(float ball, float speed_ball, float deform_ball, float invisible_ball)
	{
		Scale[0] = ball;
		Scale[1] = speed_ball;
		Scale[2] = deform_ball;
		Scale[3] = invisible_ball;
	}
	
	public void SetGroundSettings(float ball, float speed_ball, float deform_ball, float invisible_ball, float time)
	{
		Frequences_ground = new float[]{ball, speed_ball, deform_ball, invisible_ball};
		Times[0] = time;
	}
	
	public void SetLeftSettings(float ball, float speed_ball, float deform_ball, float invisible_ball, float time)
	{
		Frequences_left = new float[]{ball, speed_ball, deform_ball, invisible_ball};
		Times[1] = time;
	}
	
	public void SetRightSettings(float ball, float speed_ball, float deform_ball, float invisible_ball, float time)
	{
		Frequences_right = new float[]{ball, speed_ball, deform_ball, invisible_ball};
		Times[2] = time;
	}
	
	public BallsManager GetGroundSettings(Scene scene, VertexBufferObjectManager pVertexBufferObjectManager,
			float min_x, float max_x, float min_y, float max_y)
	{
		if (!IsGroundEnable)
			return null;
		BallsManager ballsManager_ground = new BallsManager(min_x, max_x, min_y, max_y,
				Frequences_ground[0], Frequences_ground[1], Frequences_ground[2], Frequences_ground[3], Times[0],
				MainGameActivity.mRegionUsualBall, MainGameActivity.mRegionSpeedBall, MainGameActivity.mRegionDeformBall,
				MainGameActivity.mRegionInvisibleBall, SIZE_POOL, 
				Speed, Scale, scene, pVertexBufferObjectManager);
		return ballsManager_ground;
	}
	
	public BallsManager GetLeftSettings(Scene scene, VertexBufferObjectManager pVertexBufferObjectManager,
			float min_x, float max_x, float min_y, float max_y)
	{
		if (!IsLeftEnable)
			return null;
		BallsManager ballsManager_left = new BallsManager(min_x, max_x, min_y, max_y,
				Frequences_left[0], Frequences_left[1], Frequences_left[2], Frequences_left[3], Times[1],
				MainGameActivity.mRegionUsualBall, MainGameActivity.mRegionSpeedBall, MainGameActivity.mRegionDeformBall,
				MainGameActivity.mRegionInvisibleBall, SIZE_POOL, 
				Speed, Scale, scene, pVertexBufferObjectManager);
		return ballsManager_left;
	}
	
	public BallsManager GetRightSettings(Scene scene, VertexBufferObjectManager pVertexBufferObjectManager,
			float min_x, float max_x, float min_y, float max_y)
	{
		if (!IsRightEnable)
			return null;
		BallsManager ballsManager_right = new BallsManager(min_x, max_x, min_y, max_y,
				Frequences_right[0], Frequences_right[1], Frequences_right[2], Frequences_right[3], Times[2],
				MainGameActivity.mRegionUsualBall, MainGameActivity.mRegionSpeedBall, MainGameActivity.mRegionDeformBall,
				MainGameActivity.mRegionInvisibleBall, SIZE_POOL, 
				Speed, Scale, scene, pVertexBufferObjectManager);
		return ballsManager_right;
	}										
	
	// ....... ОПИСАНИЕ УРОВНЕЙ ........
	public static Level[] Levels = new Level[10];
	public static void InitialLevels()
	{
		// ... УРОВЕНЬ 1 ...
		// Шары обыкновенные, летят слева и справа с небольшой частотой и стандартного размера.
		Levels[0] = new Level(1, false, true, true, 60, 100);
		Levels[0].SetSpeedSettings(16.0f, 0, 0, 0);
		Levels[0].SetScaleSettings(1.4f, 0, 0, 0);
		
		Levels[0].SetLeftSettings(1.0f, 0, 0, 0, 3.0f);
		Levels[0].SetRightSettings(1.0f, 0, 0, 0, 4.0f);
		// ... УРОВЕНЬ 2 ...
		// Шары обыкновенные и скоростные, скоростные летят только снизу с маленькой скоростью
		Levels[1] = new Level(2, true, true, true, 100, 200);
		Levels[1].SetSpeedSettings(16.0f, 6.0f, 0, 0);
		Levels[1].SetScaleSettings(1.4f, 1.0f, 0, 0);
		
		Levels[1].SetGroundSettings(0, 1.0f, 0, 0, 3.0f);
		Levels[1].SetLeftSettings(1.0f, 0, 0, 0, 4.0f);
		Levels[1].SetRightSettings(1.0f, 0, 0, 0, 4.5f);
		// ... УРОВЕНЬ 3 ...
		// Шары обыкновенные и скоростные, летят отовсюду с повышенной скоростью
		Levels[2] = new Level(3, true, true, true, 100, 300);
		Levels[2].SetSpeedSettings(25.0f, 8.0f, 0, 0);
		Levels[2].SetScaleSettings(1.4f, 1.0f, 0, 0);
		
		Levels[2].SetGroundSettings(0.5f, 0.5f, 0, 0, 4.0f);
		Levels[2].SetLeftSettings(0.5f, 0.5f, 0, 0, 4.0f);
		Levels[2].SetRightSettings(1.0f, 0, 0, 0, 3.0f);
		// ... УРОВЕНЬ 4 ...
		// Шары скоростные и деформации
		Levels[3] = new Level(4, true, true, true, 100, 300);
		Levels[3].SetSpeedSettings(0, 8.0f, 6.0f, 0);
		Levels[3].SetScaleSettings(0, 1.0f, 1.0f, 0);
		
		Levels[3].SetGroundSettings(0, 0, 1.0f, 0, 4.0f);
		Levels[3].SetLeftSettings(0, 1.0f, 0, 0, 3.5f);
		Levels[3].SetRightSettings(0, 1.0f, 0, 0, 3.0f);
		// ... УРОВЕНЬ 5 ...
		// Шары обыкновенные, летят отовсюду с повышенной скоростью и частотой
		Levels[4] = new Level(5, true, true, true, 100, 350);
		Levels[4].SetSpeedSettings(25.0f, 0, 0, 0);
		Levels[4].SetScaleSettings(1.4f, 0, 0, 0);
		
		Levels[4].SetGroundSettings(1.0f, 0, 0, 0, 3.0f);
		Levels[4].SetLeftSettings(1.0f, 0, 0, 0, 3.5f);
		Levels[4].SetRightSettings(1.0f, 0, 0, 0, 4.0f);
		// ... УРОВЕНЬ 6 ...
		// Шары невидимости, летят отовсюду с обычной скоростью, но большой частотой
		Levels[5] = new Level(6, true, true, true, 100, 400);
		Levels[5].SetSpeedSettings(0, 0, 0, 8.0f);
		Levels[5].SetScaleSettings(0, 0, 0, 1.0f);
		
		Levels[5].SetGroundSettings(0, 0, 0, 1.0f, 3.0f);
		Levels[5].SetLeftSettings(0, 0, 0, 1.0f, 5.0f);
		Levels[5].SetRightSettings(0, 0, 0, 1.0f, 4.0f);
		// ... УРОВЕНЬ 7 ...
		// Шары обыкновенные, летят отовсюду с повышенной скоростью, частотой и размером
		Levels[6] = new Level(7, true, true, true, 100, 400);
		Levels[6].SetSpeedSettings(25.0f, 0, 0, 0);
		Levels[6].SetScaleSettings(2.0f, 0, 0, 0);
		
		Levels[6].SetGroundSettings(1.0f, 0, 0, 0, 2.0f);
		Levels[6].SetLeftSettings(1.0f, 0, 0, 0, 3.0f);
		Levels[6].SetRightSettings(1.0f, 0, 0, 0, 3.5f);
		// ... УРОВЕНЬ 8 ...
		// Шары всех типов, летят отовсюду
		Levels[7] = new Level(8, true, true, true, 120, 400);
		Levels[7].SetSpeedSettings(20.0f, 10.0f, 6.0f, 6.0f);
		Levels[7].SetScaleSettings(1.4f, 1.0f, 1.0f, 1.0f);
		
		Levels[7].SetGroundSettings(0.5f, 0, 0.5f, 0, 2.5f);
		Levels[7].SetLeftSettings(0.5f, 0.5f, 0, 0, 3.0f);
		Levels[7].SetRightSettings(0, 0.5f, 0, 0.5f, 3.5f);
		// ... УРОВЕНЬ 9 ...
		// Шары всех типов, летят отовсюду с уменьшенными и увеличенными размерами, и увеличенной скоростью
		Levels[8] = new Level(9, true, true, true, 150, 500);
		Levels[8].SetSpeedSettings(25.0f, 10.0f, 6.0f, 6.0f);
		Levels[8].SetScaleSettings(1.4f, 1.0f, 1.0f, 1.0f);
		
		Levels[8].SetGroundSettings(0.2f, 0, 0.8f, 0, 2.0f);
		Levels[8].SetLeftSettings(0.5f, 0.5f, 0, 0, 2.0f);
		Levels[8].SetRightSettings(0, 0.5f, 0, 0.5f, 2.5f);
		// ... УРОВЕНЬ 10 ...
		// Ну это просто пиздец...
		Levels[9] = new Level(10, true, true, true, 150, 500);
		Levels[9].SetSpeedSettings(30.0f, 8.0f, 6.0f, 8.0f);
		Levels[9].SetScaleSettings(1.4f, 1.0f, 1.0f, 1.0f);
		
		Levels[9].SetGroundSettings(0.5f, 0, 0.5f, 0, 1.0f);
		Levels[9].SetLeftSettings(0.5f, 0.5f, 0, 0, 2.0f);
		Levels[9].SetRightSettings(0.5f, 0, 0, 0.5f, 2.0f);
	}
}
