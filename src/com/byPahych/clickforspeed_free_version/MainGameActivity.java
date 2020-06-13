package com.byPahych.clickforspeed_free_version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.ads.*;

public class MainGameActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener{

	public static MainGameActivity _main = null;
	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 720;
	public static Camera camera;

	public static PhysicsWorld mPhysicsWorld;
	
	private static BitmapTextureAtlas mAtlasMainBall;
	private static BitmapTextureAtlas mAtlasUsualBall;
	private static BitmapTextureAtlas mAtlasHUD;
	private static BitmapTextureAtlas mAtlasAnimateBalls;
	private static BitmapTextureAtlas mAtlasButton;
	private static BitmapTextureAtlas mAtlasByPahych;
	private static BitmapTextureAtlas mAtlasAndEngine;
	
	public static TextureRegion mRegionHUD;
	public static TiledTextureRegion mRegionMainBall;	
	public static TiledTextureRegion mRegionUsualBall;			
	public static TiledTextureRegion mRegionSpeedBall;
	public static TiledTextureRegion mRegionDeformBall;
	public static TiledTextureRegion mRegionInvisibleBall;
	public static TiledTextureRegion mRegionButton;
	public static TextureRegion mRegionAndEngine;
	public static TextureRegion mRegionByPahych;
	
	public static Font mFontMenu;
	public static Font mFontGame;
	public static Font mFontBad;
	public static Font mFontStart;
	public static Font mFontTime;
	
	public Vibrator mVibrator;
	
	public static SceneManager _sceneManager;
	private boolean IsGameLoaded = false;
	
	public static String FILENAME = "data_file_release";
	public static String INITIAL_DATA_LEVEL = "1\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0";
	public static String[] REAL_DATA_LEVEL = new String[10];
	public static String[] REAL_DATA_RECORD = new String[10];
	public static String SUPER_RECORD = "-1";
	
	BitmapTextureAtlas fontTextureAtlas;
	public static Font font;		
	
	public static StrokeFont mStrokeFontStart;	
	public static StrokeFont mStrokeFontMenu;
	
	private InterstitialAd interstitial;
	
	public static String StringHelp = "\tHOW TO PLAY\n"
			+ "Your aim is to complete 10 levels. On each level you have to get definite score for a limit time.\n"
			+ "Scores you can get by the next ways:\n"
			+ "- pushing on the red button (not as easy!)\n"
			+ "- pushing on other balls, except for green one!\n"
			+ "You will loose points because of missing.\n\n"
			+ "\tПРАВИЛА ИГРЫ\n"
			+ "Ваша задача пройти 10 уровней, в каждом из которых требуется набрать определённое количество очков за ограниченное время.\n"
			+ "Очки можно получить следующим образом:\n"
			+ "- за нажатия на кнопку в игре (это не так просто!)\n"
			+ "- за нажатия на шары, но только не на зелёные!\n"
			+ "Также очки снимают за промахи.";
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		_main = this;
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);		
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {	
		mAtlasMainBall = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		mAtlasUsualBall = new BitmapTextureAtlas(this.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		mAtlasHUD = new BitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		mAtlasAnimateBalls = new BitmapTextureAtlas(this.getTextureManager(), 1024, 384, TextureOptions.BILINEAR);
		mAtlasButton = new BitmapTextureAtlas(this.getTextureManager(), 256, 64, TextureOptions.BILINEAR);
		mAtlasByPahych = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		mAtlasAndEngine = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");		
		mRegionUsualBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasUsualBall, this, "usual_ball.png", 0, 0, 1, 1);
		mRegionMainBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasMainBall, this, "super_ball.png", 0, 0, 2, 1);
		mRegionSpeedBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasAnimateBalls, this, "speed_ball.png", 0, 0, 8, 1);
		mRegionDeformBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasAnimateBalls, this, "deform_ball.png", 0, 128, 8, 1);
		mRegionInvisibleBall = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasAnimateBalls, this, "invisible_ball.png", 0, 256, 8, 1);
		mRegionHUD = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasHUD, this, "my_HUD.png", 0, 0);
		mRegionButton = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mAtlasButton, this, "my_button.png", 0, 0, 2, 1);
		mRegionByPahych = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasByPahych, this, "byPahych.png", 0, 0);
		mRegionAndEngine = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mAtlasAndEngine, this, "andEngine.png", 0, 0);
		
		mAtlasMainBall.load();	
		mAtlasUsualBall.load();
		mAtlasHUD.load();
		mAtlasAnimateBalls.load();
		mAtlasButton.load();
		mAtlasByPahych.load();
		mAtlasAndEngine.load();
		
		final ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		FontFactory.setAssetBasePath("font/");
		final ITexture fontTexture2 = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture fontTexture3 = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);		
		
		mFontMenu = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(), "ARIAL.TTF", 28.0f, true, Color.BLACK.getARGBPackedInt());
		mFontMenu.load();		
		mFontTime = FontFactory.createFromAsset(this.getFontManager(), fontTexture2, this.getAssets(), "ARIAL.TTF", 30.0f, true, Color.BLACK.getARGBPackedInt());
		mFontTime.load();
		mFontGame = FontFactory.createFromAsset(this.getFontManager(), fontTexture3, this.getAssets(), "ARIAL.TTF", 38.0f, true, Color.YELLOW.getARGBPackedInt());
		mFontGame.load();
		mFontBad = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, 
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 38, Color.RED.getARGBPackedInt());
		mFontBad.load();		
	
		final ITexture strokeFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture strokeFontTexture2 = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);		
		
		mStrokeFontMenu = FontFactory.createStroke(this.getFontManager(), strokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 26.0f, true, 
				Color.BLACK.getARGBPackedInt(), 0.2f, Color.BLACK.getARGBPackedInt());
		mStrokeFontStart  = FontFactory.createStroke(this.getFontManager(), strokeFontTexture2, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 52.0f, true, 
				Color.GREEN.getARGBPackedInt(), 1.5f, Color.BLACK.getARGBPackedInt());
		
		mStrokeFontMenu.load();
		mStrokeFontStart.load();	
		
		InitialFileData();
		Level.InitialLevels();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		_sceneManager = new SceneManager(this.getVertexBufferObjectManager());
		//_sceneManager.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		_sceneManager.setBackground(new Background(0.4f, 0.7f, 0.9f));
		IsGameLoaded = true;
		
		_sceneManager.setOnAreaTouchListener(this);
		_sceneManager.setOnSceneTouchListener(this);
		return _sceneManager;
	}
	
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event)
	{
		if (KeyCode == KeyEvent.KEYCODE_BACK)
		{
			if (!IsGameLoaded)
				return true;
			if (_sceneManager != null && IsGameLoaded)
			{
				_sceneManager.KeyBackPressed(KeyCode, event);
			}
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}
	
	@Override
	protected void onDestroy()
	{
		//SaveInfo();
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
//	@Override
//	protected void onStop()
//	{
//		super.onStop();
//		//SaveInfo();
//	}
//	
	public void SaveInfo()
	{
		String value = "";
		for (int i = 0; i < REAL_DATA_LEVEL.length; i++)
			value = value + REAL_DATA_LEVEL[i] + "\n";
		for (int i = 0; i < REAL_DATA_RECORD.length; i++)
			value = value + REAL_DATA_RECORD[i] + "\n";
		value = value + SUPER_RECORD;
		writeFile(value);
	}
	
	public void InitialFileData()
	{
		File file = new File(this.getFilesDir(), FILENAME);		
		if (!file.exists())
			CreateInitialFile();
		readFile();
	}
	
	public void CreateInitialFile()
	{
		writeFile(INITIAL_DATA_LEVEL);
	}
	
	public void writeFile(String data) {
		try {
			if (data == null)
				return;
		  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
		      openFileOutput(FILENAME, MODE_PRIVATE)));		     
		  bw.write(data);		 
		  bw.close();
		} 
		catch (FileNotFoundException e) {
		  e.printStackTrace();
		} 
		catch (IOException e) {
		  e.printStackTrace();
		}
	}
	
	void readFile() {
		try 
		{
		  BufferedReader br = new BufferedReader(new InputStreamReader(
		      openFileInput(FILENAME)));
		  String str = "";
		  for (int i = 0; i < 10 && ((str = br.readLine()) != null); i++)
		  {
			  REAL_DATA_LEVEL[i] = str;
		  }
		  for (int i = 0; i < 10 && ((str = br.readLine()) != null); i++)
		  {
			  REAL_DATA_RECORD[i] = str;
		  }
		  if ((str = br.readLine()) != null)
			  SUPER_RECORD = str;
		} catch (FileNotFoundException e) {
		  e.printStackTrace();
		} catch (IOException e) {
		  e.printStackTrace();
		}
	}
	public Handler toaster = new Handler(){      
    	@Override
        public void handleMessage(Message msg) {
         Toast.makeText(getBaseContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
    	}
	};
	
	public void makeToast(String str){
	     Message status = MainGameActivity.this.toaster.obtainMessage();
	     Bundle datax = new Bundle();
	     datax.putString("msg", str);
	     status.setData(datax);
	     MainGameActivity.this.toaster.sendMessage(status);
	 }

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		return false;
	}
		
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	  	super.onCreate(savedInstanceState);
	    	
	  	 // Создание межстраничного объявления.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-9840453190384801/5536259777");	
	  }
	
	
	 public void LoadAdvert()
	 {	   
		 MainGameActivity._main.runOnUiThread(new Runnable() {
				@Override
				public void run() {		
					 // Создание запроса объявления.
				    AdRequest adRequest = new AdRequest.Builder()
				    .build();
				
				    // Запуск загрузки межстраничного объявления.
				    interstitial.loadAd(adRequest);		
				}				
			});	       
	 }
	  // Вызовите displayInterstitial(), когда будете готовы показать межстраничное объявление.
	  public void displayInterstitial() {
		  MainGameActivity._main.runOnUiThread(new Runnable() {
				@Override
				public void run() {		
					if (interstitial.isLoaded()) {
				      interstitial.show();
				    }		
				}				
			});	    
	  }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	switch(id)
    	{
    		case 1:
    			builder.setMessage("You won and scored " + String.valueOf(SceneGame.score) + " points!")
    				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							displayInterstitial();
							_sceneManager.ShowSelectLevel();
						}
					}).setCancelable(false);
    			dialog = builder.create();
    			break;
    		case 2:
    			builder.setMessage("You lost, you need to score at least  " + String.valueOf(SceneGame.level.need_points) + " points")
    				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							displayInterstitial();
							_sceneManager.ShowSelectLevel();
						}
					}).setCancelable(false);
    			dialog = builder.create();
    			break;
    		case 3:
    			builder.setMessage("Достижение открыто! Вы прошли всю игру. Привет от разработчика и спасибо вам ;)")
    				.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							_sceneManager.ShowSelectLevel();
						}
					}).setCancelable(false);
    			dialog = builder.create();
    			break;
    		case 4:
    			builder.setMessage(StringHelp)
    				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).setCancelable(false);
    			dialog = builder.create();
    			break;
			default:
    	}
    	return dialog;
	}
}
