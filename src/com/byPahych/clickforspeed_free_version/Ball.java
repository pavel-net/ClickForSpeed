package com.byPahych.clickforspeed_free_version;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ball extends AnimatedSprite {

	boolean flag_ready = true;
	boolean flag_exist_body = false;
	Body body;
	PhysicsConnector connector;
	FixtureDef FIXTURE_DEF;
	float scale;
	float start_pX;
	float start_pY;
	
	public Ball(final float pX, final float pY, final ITiledTextureRegion pNormalTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager, FixtureDef FIXTURE_DEF,
			float scale){
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager);
		this.FIXTURE_DEF = FIXTURE_DEF;
		this.scale = scale;
		this.setScale(this.scale);
		this.start_pX = pX;
		this.start_pY = pY;
	}
	// Метод возвращает true, если объект находится за пределами камеры
	public boolean IsOffScene()
	{
		if (this.getY() + this.getHeight() < 0 || this.getY() > MainGameActivity.CAMERA_HEIGHT + this.getHeight())
		{
			return true;
		}
		if (this.getX() + this.getHeight() < 0 || this.getX() > MainGameActivity.CAMERA_WIDTH + this.getHeight())
		{
			return true;
		}
		return false;
	}
	
	public void SetBusy()
	{
		flag_ready = false;
	}
	public void SetRelease()
	{
		flag_ready = true;
		RemoveBody();
	}
	public boolean IsReady()
	{
		return flag_ready;
	}
	public void Stop()
	{
		SetMove(new Vector2(0, 0));
	}
	
	public void SetMove(Vector2 velocity)
	{
		if (!flag_exist_body)
			return;
		body.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}
	
	void RemoveBody()
	{		
		if (!flag_exist_body)
			return;
		Stop();
		MainGameActivity.mPhysicsWorld.unregisterPhysicsConnector(connector);
		MainGameActivity.mPhysicsWorld.destroyBody(body);
		flag_exist_body = false;
	}
	
	private void CreateBody()
	{
		body = PhysicsFactory.createCircleBody(MainGameActivity.mPhysicsWorld, this, 
				BodyType.DynamicBody, FIXTURE_DEF);
		connector = new PhysicsConnector(this, body, true, true);
		MainGameActivity.mPhysicsWorld.registerPhysicsConnector(connector);
		flag_exist_body = true;
		body.setUserData(this);
	}
	
	public void SetNewPosition(float pX, float pY)
	{
		if (flag_exist_body)
		{
			RemoveBody();
		}
		this.setPosition(pX, pY);
		CreateBody();
	}	
	
	public void RemoveBodyForAnimation()
	{
		RemoveBody();
	}
	
	public void StartAnimation()
	{
		RemoveBodyForAnimation();
	}
	
	public void StartKillAnimation()
	{
		
	}
	
	protected void EndAnimation()
	{
		this.setPosition(start_pX, start_pY);
		this.setCurrentTileIndex(0);
		this.flag_ready = true;
	}
}
