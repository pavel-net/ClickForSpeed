package com.byPahych.clickforspeed_free_version;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.FixtureDef;

public class InvisibleBall extends Ball {

	public InvisibleBall(float pX, float pY,
			ITiledTextureRegion pNormalTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			FixtureDef FIXTURE_DEF, float scale) {
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager, FIXTURE_DEF,
				scale);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void StartAnimation()
	{
		RemoveBodyForAnimation();
		this.animate(new long[] {100, 50, 50, 50, 50, 50, 100}, new int[] {1, 2, 3, 4, 5, 6, 7}, 0, new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {				
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {				
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {	
			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				EndAnimation();
			}
	        
	    });
	}
	@Override
	public void StartKillAnimation()
	{
		RemoveBodyForAnimation();	
		this.animate(new long[] {100, 100, 100}, new int[] {5, 6, 7}, 0, new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {				
			}
			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {				
			}
			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {	
			}
			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				EndAnimation();
			}	        
	    });
	}
}
