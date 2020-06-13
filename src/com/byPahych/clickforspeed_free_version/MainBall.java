package com.byPahych.clickforspeed_free_version;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MainBall extends TiledSprite {

	private static final Vector2 START_MOVE = new Vector2(3.0f, 1.5f);
	private Scene parentScene;
	Body body = null;
	PhysicsConnector connector = null;
	public float real_size = 1.0f;
	
	public MainBall(Scene parentScene, float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.parentScene = parentScene;
		this.real_size = 0.9f;
		this.setScale(this.real_size);		
		body = PhysicsFactory.createCircleBody(MainGameActivity.mPhysicsWorld, this, 
				BodyType.DynamicBody, BallsManager.BALL_FIXTURE_DEF);
		
		body.setUserData("MAIN_BALL");		
		connector = new PhysicsConnector(this, body, true, true);
		MainGameActivity.mPhysicsWorld.registerPhysicsConnector(connector);

		body.setLinearVelocity(START_MOVE);
		Vector2Pool.recycle(START_MOVE);
	}
	
	public void UpdateSpeedBall(int count_click) {

		Vector2 vector_dir = body.getLinearVelocity();
		Vector2 velocity = vector_dir;
		if (Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y)) < 1)
		{
			velocity = START_MOVE;
		}
		else
		{
			// выявляем преобладание скорости по одной координате
			if (velocity.y != 0)
			{
				if (Math.abs(velocity.x / velocity.y) < 0.1f)
				{
					velocity.x = velocity.y / 3.0f;
				}
			}
			if (velocity.x != 0)
			{
				if (Math.abs(velocity.y / velocity.x) < 0.1f)
				{
					velocity.y = velocity.x / 3.0f;
				}
			}
			
			if (count_click == 1)
			{
				velocity.x = velocity.x * 1.15f;
				velocity.y = velocity.y * 1.15f;
			}
			else if (count_click == 2)
			{
				velocity.x = velocity.x * 1.25f;
				velocity.y = velocity.y * 1.25f;
			}
			else if (count_click == 3)
			{
				velocity.x = velocity.x * 1.5f;
				velocity.y = velocity.y * 1.5f;
			}
			else if (count_click == 4)
			{
				velocity.x = velocity.x * 1.8f;
				velocity.y = velocity.y * 1.8f;
			}
			else if (count_click >= 5)
			{
				velocity.x = velocity.x * 1.9f;
				velocity.y = velocity.y * 1.9f;
			}	
		}
		if (count_click != 0)
		{
			body.setLinearVelocity(velocity);
			Vector2Pool.recycle(velocity);
		}			
	}

	public void Acceleration(float acceleration)
	{
		Vector2 vector_dir = body.getLinearVelocity();
		vector_dir.x *= acceleration;
		vector_dir.y *= acceleration;
		body.setLinearVelocity(vector_dir);
		Vector2Pool.recycle(vector_dir);
	}
	
	public void Resize(float scale)
	{
		Vector2 vector_speed = body.getLinearVelocity();
		MainGameActivity.mPhysicsWorld.unregisterPhysicsConnector(connector);
		MainGameActivity.mPhysicsWorld.destroyBody(body);
		this.setScale(scale);
		body = PhysicsFactory.createCircleBody(MainGameActivity.mPhysicsWorld, this, 
				BodyType.DynamicBody, BallsManager.BALL_FIXTURE_DEF);		
		body.setUserData("MAIN_BALL");		
		connector = new PhysicsConnector(this, body, true, true);
		MainGameActivity.mPhysicsWorld.registerPhysicsConnector(connector);
		body.setLinearVelocity(vector_speed);
		Vector2Pool.recycle(vector_speed);
	}
	
	public void RemoveBody()
	{
		MainGameActivity.mPhysicsWorld.unregisterPhysicsConnector(connector);
		MainGameActivity.mPhysicsWorld.destroyBody(body);
	}
	
	public void ReleaseMainBall()
	{
		MainGameActivity.mPhysicsWorld.unregisterPhysicsConnector(connector);
		MainGameActivity.mPhysicsWorld.destroyBody(body);
		this.parentScene.unregisterTouchArea(this);
		this.parentScene.detachChild(this);
	}
}
