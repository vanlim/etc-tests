package tests;

import interpolationtests.CatmullRomSpline;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.analysis.KissFFT;
import com.badlogic.gdx.audio.io.Mpg123Decoder;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;

public class Test extends Game implements InputProcessor
{
	private String FILE = "music/payphone.mp3";
	private Mpg123Decoder decoder;
	private AudioDevice device;
	private KissFFT fft;
	private float[] spectrum = new float[2048];
	private float[] maxValues = new float[2048];
	private float[] topValues = new float[2048];
	private boolean playing = false;
	private short[] samples = new short[2048];
	private int NB_BARS = 50;
	private float barWidth;
	private Texture colors;
	
	private DecalBatch decalBatch;
	

	private PerspectiveCamera camera;
	private PerspectiveCamera camera2;
	private OrthographicCamera camera2d;
	private InputMultiplexer inputMultiplexer;

	private Stage uiStage;

	private Texture texture;
	private TextureRegion textureRegion;

	private float WIDTH;
	private float HEIGHT;

	private Matrix4 matrix;

	SpriteBatch batch;

	float worldWidth;
	float worldHeight;

	float ppcX;
	float ppcY;

	float laneStep;
	float laneGap;
	float lanes;
	float laneSize;
	float laneSpan;

	float horizRatio;
	float vertRatio;

	private ArrayList<TextureRegion> arrowSet;
	private ArrayList<TextureRegion> arrayTex;
	private Texture arrow;
	private Texture arrowTemp;
	private ArrayList<Arrow> arrows;

	private ArrayList<Decal> laneSet;	
	private ArrayList<TextureRegion> arrowButtons = new ArrayList<TextureRegion>(); 
	
	private ArrayList<ArrowButton> buttons = new ArrayList<ArrowButton>();
	
	private BitmapFont font;
	private Vector3 buttonLocation;
	private Vector3 laneLocation;

	private boolean finishSong = false;
	private float lastDropTime = 0;
	
	CatmullRomSpline spline;
	List<Vector3> curvePath;
	Texture tunnelPanel;	
	Vector3 curvePos;

	public Test()
	{
	}

	class Arrow
	{
		float x = 0;
		float y = 0;
		float z = 0;
		float beatVeloc = 0;
		float trailVeloc = 0;
		float length = 0;
		float trailSpacing = 0;
		int type;
		boolean landed = false;
		boolean isPressed = false;
		boolean pressOnce = false;
		
		ArrayList<Decal> decals = new ArrayList<Decal>();
	}

	FPSLogger fpsLogger;
	boolean pressOnce;
	

	Arrow arrowBeat;
	Thread playbackThread;
	Decal dec;
	
	
	// NEW VARIABLES ----------------------------------------
	float distancePerSecond;
	float laneLength;
	float worldEnd;
	float upperBound;
	float lowerBound;
	float buttonPos;
	
	
	@Override
	public void create()
	{
		fpsLogger = new FPSLogger();
		pressOnce = false;
		
		HEIGHT = Gdx.graphics.getHeight();
		WIDTH = Gdx.graphics.getWidth();

		barWidth = ((float) WIDTH / (float) NB_BARS);

		worldWidth = 800;
		worldHeight = 480;

		horizRatio =  WIDTH / worldWidth;
		vertRatio = HEIGHT / worldHeight;

		ppcX = Gdx.graphics.getPpcX();
		ppcY = Gdx.graphics.getPpcY();

		uiStage = new Stage(WIDTH, HEIGHT, true);

		camera2d = new OrthographicCamera(WIDTH, HEIGHT);
		camera2d.position.set(0, 0, 0);

		camera = new PerspectiveCamera(67, worldWidth, worldHeight);
		camera.position.set(0,-40 * vertRatio, 20 * vertRatio).mul(20);
		camera.project(new Vector3(0, 0, 0), 0, 0, WIDTH, HEIGHT);
		camera.lookAt(0, 0, 0);
		camera.fieldOfView = 67;
		camera.near = 0.1f;
		camera.far = 10000f; // CHANGED ------------------------
		camera.update(); // ADDED ------------------------
		
		camera2 = new PerspectiveCamera(120, 1000, 1000);
		camera2.position.set(0, 0, 0);
		camera2.near = 0.1f;
		camera2.far = 1000;
		
		
		laneGap = -3;
		lanes = 4;

		laneSize = worldWidth * horizRatio / lanes;

		laneStep = laneSize + laneGap;

		laneSpan = laneStep * 3;
		
		// added after camera declaration
		////////////////////////////////////////////////////////////	
		buttonPos = 72*vertRatio;
		buttonLocation = getPointOnPlane(0,buttonPos);
		laneLength = worldHeight * 6 * vertRatio;
		worldEnd = -worldHeight * 2 * vertRatio;
		distancePerSecond = laneLength - buttonLocation.y;				
		lowerBound = buttonLocation.y - (150 * vertRatio);
		upperBound = buttonLocation.y + (200 * vertRatio);
		////////////////////////////////////////////////////////////
		
		batch = new SpriteBatch();
		
		decalBatch = new DecalBatch();
//		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		 
		arrows = new ArrayList<Arrow>();
		
		String[] name = { "left", "down", "up", "right" };
		arrowSet = new ArrayList<TextureRegion>();
		
		//texture loading
		//beats
		for (int i = 0; i < 4; i++)
		{
			arrowTemp = new Texture(Gdx.files.internal("texture/step_" + name[i] + ".png"));
			arrowTemp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			arrow = arrowTemp;
			arrowSet.add(new TextureRegion(arrow));
		}
		for (int i = 0; i < 4; i++)
		{
			arrowTemp = new Texture(Gdx.files.internal("texture/step_" + name[i] + "_trail.png"));
//			arrowTemp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			arrow = arrowTemp;
			arrowSet.add(new TextureRegion(arrow));
		}
		//buttons
//		for (int i = 0; i < 4; i++)
//		{
//			arrowTemp = new Texture(Gdx.files.internal("texture/step_" + name[i] + "_nocolor.png"));
//			arrowTemp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//			arrow = arrowTemp;
//			arrowButtons.add(new TextureRegion(arrow));
//		}		
		
		//new
		
//		camera.unproject(tempVec);			
	
		for (int i = 0, x=0; i < 4; i++, x+=laneStep)
		{
			arrowTemp = new Texture(Gdx.files.internal("texture/step_" + name[i] + "_nocolor.png"));
			arrowTemp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			arrow = arrowTemp;
			// CHANGED ---------------------------
			buttons.add(new ArrowButton(arrow, name[i], camera, x - laneSize / 2 - laneSpan / 2 + laneStep / 2,buttonLocation.y,0,arrow.getWidth(),arrow.getHeight(), 1, 1, horizRatio, vertRatio));
		}	
		for (int i = 0; i < buttons.size(); i++)
		{
			uiStage.addActor(buttons.get(i));
		}
		
		//lane textures
		arrayTex = new ArrayList<TextureRegion>();
		laneSet = new ArrayList<Decal>();
		name = new String[] { "yellow", "blue", "red", "green" };
		for (int i = 0; i < 4; i++)
		{
			texture = new Texture(Gdx.files.internal("texture/test/" + name[i] + "lane.png"));
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			textureRegion = new TextureRegion(texture, 0, 0, 64, 64);
			arrayTex.add(textureRegion);
		}
//		for (int y = (int) worldEnd; y < laneLength; y += 64*vertRatio) // CHANGED ------------------------
//		{
			for (int i = 0, x = 0; i < arrayTex.size(); x += laneStep, i++)
			{
				TextureRegion temp = arrayTex.get(i);
				dec = Decal.newDecal(64,laneLength-worldEnd,temp,true);
				dec.setPosition(x - laneSize / 2 - laneSpan / 2 + laneStep/2, -worldEnd, 0); // CHANGED ------------------------
				dec.setScale(3*horizRatio, vertRatio);
				laneSet.add(dec);	
			}
//		}	
		arrayTex.clear();
		arrayTex = null;
		
		
		font = new BitmapFont(Gdx.files.internal("font/roboto_bold_italic.fnt"), Gdx.files.internal("font/roboto_bold_italic.png"), false);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font.setScale(1*horizRatio, 1*vertRatio);

		matrix = new Matrix4();
		matrix.scale(horizRatio, vertRatio, 0);

		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(uiStage);
		inputMultiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(inputMultiplexer);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		colors = new Texture(Gdx.files.internal("texture/colors-borders.png"));

		fft = new KissFFT(2048);

		// Assign - to maxValues and topValues
		for (int i = 0; i < maxValues.length; i++)
		{
			maxValues[i] = 0;
			topValues[i] = 0;
		}

		// The audio file has to be on the external storage (not in the assets)
		FileHandle externalFile = Gdx.files.external("temp1/payphone.mp3");
		Gdx.files.internal(FILE).copyTo(externalFile);

		// Create the decoder
		decoder = new Mpg123Decoder(externalFile);

		// Create an audio device for playback
		device = Gdx.audio.newAudioDevice(decoder.getRate(), decoder.getChannels() == 1 ? true : false);

		playbackThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				int readSamples = 0;
				// read until we reach the end of the file
				while (playing && (readSamples = decoder.readSamples(samples, 0, samples.length)) > 0)
				{
					// get audio spectrum
					fft.spectrum(samples, spectrum);
					// write the samples to the AudioDevice
					device.writeSamples(samples, 0, readSamples);
				}
				finishSong = true;
			}
		});
		playbackThread.setDaemon(true);
		playbackThread.start();
		playing = true;
		
		
		///////////////////////////////////////////////////////////////////////////////////
		initParticles();
		///////////////////////////////////////////////////////////////////////////////////
		initTunnel();
		
	}
	
	int pathPointSize = 100;
	int idx = 1;
	Vector2 newPoint;
	Vector3 upVector = new Vector3(0, 1, 0);
	int length = 0;
	List<Vector3> tangents;
	Vector3 point2;
	
	public static Vector2 rotate(float x, float y, float a, float b, float angleDeg) {
		   float cos = MathUtils.cosDeg(angleDeg);
		   float sin = MathUtils.sinDeg(angleDeg);
		   return new Vector2(cos*(x-a) - sin*(y-b) + a, sin*(x-a) + cos*(y-b) + b);
	}
	
	private void initTunnel()
	{
		tunnelPanel = new Texture(Gdx.files.internal("data/libgdx.png"));
		curvePos = new Vector3();
		spline = new CatmullRomSpline();
		curvePath = new ArrayList<Vector3>();
		for (int i = 0; i < 5; i++)
		{
			spline.add(new Vector3(MathUtils.random(-worldWidth/4, worldWidth/4), MathUtils.random(-worldHeight/2, worldHeight/2), length -= 900));
		}
		spline.getPath(curvePath, pathPointSize);
	}

	private float avg(int pos, int nb)
	{
		int sum = 0;
		for (int i = 0; i < nb; i++)
		{
			sum += spectrum[pos + i];
		}

		return (float) (sum / nb);
	}
	
	CustomParticleEffect effect;
	CustomParticleEffect tempFx;

	ArrayList<CustomParticleEffect> effects;

	FileHandle effectFile;
	FileHandle effectImgDir;
	
	float[] tintcol = new float[3];

	private void initParticles()
	{
		effect = new CustomParticleEffect();
		effectFile = Gdx.files.internal("particle/vis_particle.p");
		effectImgDir = Gdx.files.internal("texture");
		effects = new ArrayList<CustomParticleEffect>();
		effect.load(effectFile,effectImgDir);
	}
	
	private void visualize()
	{
		tempFx = new CustomParticleEffect(effect);
		tempFx.setPosition(MathUtils.random(-WIDTH/2,WIDTH/2), MathUtils.random(-HEIGHT/2,HEIGHT/2));
		tempFx.allowCompletion();
			
		tintcol[0] = MathUtils.random(0.1f,0.7f);
		tintcol[1] = MathUtils.random(0.1f,0.7f);
		tintcol[2] = MathUtils.random(0.1f,0.7f);
			
		for (CustomParticleEmitter emitter : tempFx.getEmitters())
		{
			emitter.horizRatio = horizRatio;
			emitter.vertRatio = vertRatio;				
			emitter.getTint().setColors(tintcol);
		}
		
		effects.add(tempFx);
		effects.get(effects.size()-1).start();
		tempFx = null;
	}
	
	private void checkLongBeat()
	{
		for(ArrowButton button : buttons)
		{
			temp = getPointOnPlane(button.getWorldPosition().x, button.getWorldPosition().y);
			for (int x = 0; x < arrows.size(); x++)
			{
				tempBeat = arrows.get(x);
				if(tempBeat.length > 0)
				{
					arrowRange = tempBeat.x;
					pressLoc = temp.y;
					arrowDist = tempBeat.y;
					lastTrailPos = tempBeat.y + (tempBeat.length * tempBeat.trailSpacing);
					if (arrowRange > temp.x - laneStep / 2 && arrowRange < temp.x + laneStep / 2) // CHANGED
					{
						if (tempBeat.isPressed && button.longPress && tempBeat.decals.size() > 1)
						{
							for(int i = 0 ; i < tempBeat.decals.size() ; i++)
							{
								dec = tempBeat.decals.get(i);
								if (dec.getY() <= pressLoc && i != 0)
								{
									switch(tempBeat.type)
									{
										case 0:
											dec.setColor(1, 0.9f, 0.3f, 1);
											break;
										case 1:
											dec.setColor(0.3f, 0.6f, 1, 1);
											break;
										case 2:
											dec.setColor(1, 0.3f, 0.3f, 1);
											break;
										case 3:
											dec.setColor(0.5f, 1, 0.3f, 1);
											break;
									}
								}
							}
						}		
						if (pressLoc >= lastTrailPos && tempBeat.isPressed && button.longPress)
						{
							button.longPress = false;
							tempBeat.isPressed = false;
							arrows.remove(tempBeat);
						}
					}
				}
			}
		}
		
		for (ArrowButton button : buttons)
		{
			temp = getPointOnPlane(button.getWorldPosition().x, button.getWorldPosition().y);
			for (int x = 0; x < arrows.size(); x++)
			{
				tempBeat = arrows.get(x);
				arrowRange = tempBeat.x;
				pressLoc = temp.y;
				lastTrailPos = tempBeat.y + (tempBeat.length * tempBeat.trailSpacing);
				if (arrowRange > temp.x - laneStep / 2 && arrowRange < temp.x + laneStep / 2) // CHANGED
				{
					if (tempBeat.isPressed && pressLoc <= lastTrailPos && button.touchUp)
					{
						for (Decal dec : tempBeat.decals)
						{
							dec.setColor(0.3f, 0.3f, 0.3f, 1f);
						}
						tempBeat.isPressed = false;
						button.longPress = false;
					}
				}
			}
		}
	}
	
	private void createSteps(float length)
	{
		arrowBeat = new Arrow();
		float frequency =  MathUtils.PI*2/100;
		int seed = MathUtils.random(0,3);
		for(int x= 0; x < length; x++)
		{
			if (x > 0)
			{
				dec = Decal.newDecal(64, 64, arrowSet.get(seed + 4), true);
				dec.setColor(Math.abs(MathUtils.sin(frequency*x+2)), Math.abs(MathUtils.sin(frequency*x+0)),  Math.abs(MathUtils.sin(frequency*x+4)), 1f);
			}
			else
			{
				dec = Decal.newDecal(64, 64, arrowSet.get(seed), true);
			}
			dec.rotateX(60);
			dec.setScale(3*horizRatio, 3*vertRatio);
			arrowBeat.length = x;
			arrowBeat.decals.add(dec);
		}
		arrowBeat.type = seed;
		arrowBeat.x = (laneStep * seed) - laneSize / 2 - laneSpan / 2 + laneStep / 2; // CHANGED ------------------------			
		arrowBeat.trailSpacing = 10 * vertRatio;
		arrowBeat.beatVeloc = 0;
		arrows.add(arrowBeat);	
		lastDropTime = TimeUtils.nanoTime();
		
	}
	
	public static Vector3 touchPoint = new Vector3();
	public static Plane plane = new Plane(new Vector3(0,0,1), 0);
	
	// Returns a Vector3 in 3D space from the Gdx.input
	public Vector3 getPointOnPlane(Input input){
	    // Gets the PickYar from the camera
	    Ray pickRay = camera.getPickRay(input.getX(), input.getY());
	    // touchPoint is set from this function
	    Intersector.intersectRayPlane(pickRay, plane, touchPoint);
	    // deliver the goods
	    return touchPoint;
	}
	
	
	// NEW FUNCTION -------------------------
	public Vector3 getPointOnPlane(float x, float y){
	    // Gets the PickYar from the camera
	    Ray pickRay = camera.getPickRay(x, HEIGHT - y);
	    // touchPoint is set from this function
	    Intersector.intersectRayPlane(pickRay, plane, touchPoint);
	    // deliver the goods
	    return touchPoint;
	}
	Matrix4 matrix2 = new Matrix4();
	Matrix4 matrix3 = new Matrix4();
	
	Vector3 temp; 
	float arrowRange;
	float arrowDist;
	float arrowDistEnd;
	
	Arrow tempBeat;	
	
	float rate;
	float pressLoc;
	float lastTrailPos; 
	float laneLoc;
	
	float k, speed;
	
	boolean up = true;
	boolean down = true;
	
	float deltaTime;
	
	@Override
	public void render()
	{
		deltaTime = Gdx.graphics.getDeltaTime();
		
		super.render();
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer))
		{
			float accelX = Gdx.input.getAccelerometerX();
			float accelY = Gdx.input.getAccelerometerY();
			// float accelZ = Gdx.input.getAccelerometerZ();

			// Gdx.app.log("Accelerometer: x: ", Gdx.input.getAccelerometerX() +
			// "y: " + Gdx.input.getAccelerometerY() + "z: " +
			// Gdx.input.getAccelerometerZ() );
			if(up)
			{
				if (accelX >= 2 && accelY >= -1 && accelY <= 1)
				{
					Gdx.app.log("sensor-x", "UP");
				}
				up = false;
			}
			else if(accelX < 2)
			{
				up = true;
			}
			if(down)
			{
				if (accelX < 0 && accelY >= -1 && accelY <= 1)
				{
					Gdx.app.log("sensor-x", "down");
				}
				down = false;
			}
			else if(accelX > 0)
			{
				down = true;
			}

//			Gdx.app.log("sensor", Boolean.toString(printOnce));
		}
		
		camera.update();
		camera2.update();
		camera2d.update();
		
//		uiStage.act();
		
		//input processing
		if(Gdx.input.isTouched())
		{
			for(ArrowButton button : buttons)
			{
				if(button.isPressed() && button.pressOnce == false)
				{
					temp = getPointOnPlane(button.getWorldPosition().x, button.getWorldPosition().y);
					for(int x = 0; x < arrows.size() ; x++)
					{
						tempBeat = arrows.get(x);
						arrowRange = tempBeat.x; // CHANGED -----------------------------
						arrowDist = tempBeat.y;
						if(arrowDist >= lowerBound && arrowDist <= upperBound && !tempBeat.pressOnce)
						{
							if(arrowRange > temp.x - laneStep/2 && arrowRange < temp.x + laneStep/2) // CHANGED ------------------------
							{
								button.hit = true;
								tempBeat.pressOnce = true;
								tempBeat.isPressed = true;
//								Gdx.input.vibrate(100);
								if (tempBeat.length > 0)
								{
									button.longPress = true;
								}
								else
								{
									
									arrows.remove(tempBeat);
								}
							}
						}
						tempBeat = null;
					}
					button.pressOnce = true;
				}
				if(button.isPressed() == false)
				{
					button.pressOnce = false;
					button.hit = false;
				}
			}
		}
		else
		{
			for(ArrowButton button : buttons)
			{
				button.pressOnce = false;
			}
		}
		
		checkLongBeat();
		
		
//		fpsLogger.log();

		matrix2.setToRotation(0, 0, 0, 0);
		batch.setProjectionMatrix(camera2d.combined);
		batch.setTransformMatrix(matrix2);
		batch.begin();
		
		for (int i = 0; i < NB_BARS; i++)
		{
			int histoX = 0;
			if (i < NB_BARS / 2)
			{
				histoX = NB_BARS / 2 - i;
			}
			else
			{
				histoX = i - NB_BARS / 2;
			}

			int nb = (samples.length / NB_BARS) / 2;
			if (avg(histoX, nb) > maxValues[histoX])
			{
				
				maxValues[histoX] = avg(histoX, nb);
				if ((TimeUtils.nanoTime() - lastDropTime > 370000000) && finishSong == false)
				{
					createSteps(MathUtils.random(1,100));
//					visualize();
				}
//				System.out.println("Drop.render() 1 " + maxValues[histoX]);
			}

			if (avg(histoX, nb) > topValues[histoX])
			{
				topValues[histoX] = avg(histoX, nb);
				
//				System.out.println("Drop.render() 2 " + topValues[histoX]);
			}

//			float scw = i * barWidth;
//			batch.draw(colors, -WIDTH/2 + scw , 0, barWidth, avg(histoX, nb) / 256 * HEIGHT * 3.0f, 0, 0, 16, 5, false, false);
//			batch.draw(colors, -WIDTH/2 + scw, maxValues[histoX] / 256 * HEIGHT * 3.0f , barWidth, 4, 0, 5, 16, 5, false, false);
////			batch.draw(colors, -WIDTH/2 + scw, topValues[histoX] / 256 * HEIGHT * 3.0f, barWidth, 2, 0, 10, 16, 5, false, false);
//
//			batch.draw(colors, -WIDTH/2 + scw , 0, barWidth, -avg(histoX, nb) / 256 * HEIGHT * 2.0f, 0, 0, 16, 5, false, false);
//			batch.draw(colors, -WIDTH/2 + scw, -maxValues[histoX] / 256 * HEIGHT * 2.0f , barWidth, 4, 0, 5, 16, 5, false, false);
////			batch.draw(colors, -WIDTH/2 + scw, -topValues[histoX] / 256 * HEIGHT * 2.0f, barWidth, 2, 0, 10, 16, 5, false, false);
			
			maxValues[histoX] -= (1.0 / 3.0);
		}
		
//		String lyrics = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG.";
//		String[] words = lyrics.split(" ");
//		
//		for (int i = 0; i < words.length; i++)
//		{
//			int histoX = 0;
//			if (i < words.length / 2)
//			{
//				histoX = words.length / 2 - i;
//			}
//			else
//			{
//				histoX = i - words.length / 2;
//			}
//			int nb = (samples.length / words.length) / 2;
//			if (avg(histoX, nb) > maxValues[histoX])
//			{
//				maxValues[histoX] = avg(histoX, nb);
//				if ((TimeUtils.nanoTime() - lastDropTime > 370000000) && finishSong == false)
//				{
//					createSteps(MathUtils.random(1,15));
//					visualize();
//				}
////				System.out.println("Drop.render() 1 " + maxValues[histoX]);
//			}
//			if (avg(histoX, nb) > topValues[histoX])
//			{
//				topValues[histoX] = avg(histoX, nb);
//				
////				System.out.println("Drop.render() 2 " + topValues[histoX]);
//			}
//						
//				
////			font.drawWrapped(batch, words[i], -WIDTH+(i*32*words.length), avg(histoX, nb) / 256 * HEIGHT * 3.0f, 32*words.length, BitmapFont.HAlignment.CENTER);
//			maxValues[histoX] -= (1.0 / 3.0);
//		}
		
		CustomParticleEffect temp = new CustomParticleEffect();
		
		for (CustomParticleEffect effect : effects)
		{
			effect.draw(batch, Gdx.graphics.getDeltaTime());
			if (effect.isComplete())
			{
				effect.dispose();
				temp = effect;
			}
		}
		
		effects.remove(temp);
		temp.dispose();
		temp = null;
		
		batch.end();
		
		// 2D BATCHES
		
		String lyrics = "The Quick Brown Fox jumps over the Lazy Dog.";
//		batch.setProjectionMatrix(camera2d.combined);
//		batch.setTransformMatrix(matrix2);
		batch.begin();
		font.drawWrapped(batch, lyrics, -WIDTH/2+15*horizRatio, HEIGHT/2-15*vertRatio, WIDTH/2, BitmapFont.HAlignment.LEFT);
		batch.end();
		lyrics= null;
		
//		float shift = 100*horizRatio;
//		String lyrics = "The Quick Brown Fox jumps over the Lazy Dog.";
//		matrix2.setToRotation(0, 0, 1, 90);
//		matrix3.setToRotation(1, 0, 0, 90);		
//		matrix2.mul(matrix3);
//		matrix3.setToTranslation(0, 200*vertRatio,  -laneSpan/2);
//		matrix2.mul(matrix3);
//		batch.setProjectionMatrix(camera.combined);
//		batch.setTransformMatrix(matrix2);
//		batch.begin();
//		font.drawWrapped(batch, lyrics, buttonLocation.y-shift, 0, laneLength, BitmapFont.HAlignment.LEFT);
//		batch.end();
////				
//		matrix2.setToRotation(0, 0, -1, 90);
//		matrix3.setToRotation(1, 0, 0, 90);		
//		matrix2.mul(matrix3);
//		matrix3.setToTranslation(0, 200*vertRatio, -laneSpan/2);
//		matrix2.mul(matrix3);
//		batch.setProjectionMatrix(camera.combined);
//		batch.setTransformMatrix(matrix2);
//		batch.begin();
//		font.drawWrapped(batch, lyrics, -buttonLocation.y-laneLength+shift, 0, laneLength, BitmapFont.HAlignment.RIGHT);
//		batch.end();
//		
		// 3D Game World - 3d Batches
				
		matrix2.setToRotation(0, 0,0,0);
		batch.setTransformMatrix(matrix2);
		batch.setProjectionMatrix(camera2.combined);
		batch.begin();
		batch.end();			
		
//		if (idx == ((spline.getControlPoints().size() - 2) * pathPointSize) + 2 - pathPointSize * 2)
//		{
//			if (spline.getControlPoints().size() > 4)
//			{
//				for (int x = 0; x < spline.getControlPoints().size() - 3; x++)
//				{
//					spline.getControlPoints().remove(x);
//				}
//			}
//			idx = (spline.getControlPoints().size() - 2) * pathPointSize - pathPointSize * 2 + 2;
//			curvePath.clear();
//			spline.add(new Vector3(MathUtils.random(-worldWidth / 4, worldWidth / 4), MathUtils.random(-worldHeight / 2, worldHeight / 2), length -= 900));
//			spline.getPath(curvePath, pathPointSize);
//		}
//
//		tangents = spline.getTangents(pathPointSize);
//
//		float fade = 0.25f;
//
//		if (spline.getControlPoints().size() > 4)
//		{
//			for (int i = idx + pathPointSize; i > idx; i--)
//			{
//				Vector3 point = curvePath.get(i).cpy();
//				Vector3 tempTan = tangents.get(i);
//
//				if (i % 20 == 0)
//				{
//					for (int x = 0; x < 12; x++)
//					{
//						float newAngle = x * 30;
//						newPoint = rotate(point.x + 150, point.y, point.x, point.y, newAngle);
//						dec = Decal.newDecal(new TextureRegion(tunnelPanel, 16, 16), true);
//						dec.setScale(8, 4);
//						dec.setColor(fade, fade, fade, 1);
//						dec.setPosition(newPoint.x, newPoint.y + 80, point.z);
//						dec.setRotation(tempTan, upVector);
//						dec.rotateY(90);
//						dec.rotateX(newAngle);
//						decalBatch.add(dec);
//						dec = null;
//					}
//					fade += 0.15f;
//				}
//				point = null;
//				tempTan = null;
//
//			}
//		}
//
//		if (spline.getControlPoints().size() > 4)
//		{
//			point2 = curvePath.get(idx).cpy();
//
//			float dist = curvePos.dst(point2);
//			for (float i = 0.0f; i < 1.0; i += (1 * deltaTime) / dist)
//			{
//
//				curvePos.lerp(point2, i);
//				camera2.position.set(curvePos);
//			}
//
//			camera2.direction.lerp(tangents.get(idx), deltaTime * 3);
//
//			if ((Math.abs(point2.x - curvePos.x) <= 10f && Math.abs(point2.y - curvePos.y) <= 10f) && idx < curvePath.size() - 2)
//			{
//				idx++;
//			}
//		}
//		tangents.clear();
//
//		if (curvePos.z < -Integer.MAX_VALUE)
//		{
//			length = 0;
//
//			for (Vector3 tempPoint : spline.getControlPoints())
//			{
//				tempPoint.set(tempPoint.x, tempPoint.y, length -= 900);
//			}
//			curvePath.clear();
//			spline.getPath(curvePath, pathPointSize);
//		}
//		decalBatch.flush();
//		
//		
//		
//		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.end();	// CHANGED -------------------------------
		
		for (int i = 0 ; i < laneSet.size(); i++)
		{
			dec = laneSet.get(i);
			decalBatch.add(dec);				
		}
		
		
		k = 0; 
		speed = 0.5f;
		rate = distancePerSecond * Gdx.graphics.getDeltaTime()*speed;
		for(int x = arrows.size()-1; x >=0 ; x--)
		{
			Arrow beat = arrows.get(x);
			int decalSize = beat.decals.size()-1;
			if(!beat.landed)
			{
				beat.beatVeloc += rate; // CHANGED ------------------------
				beat.z = laneLength - beat.beatVeloc; // CHANGED ------------------------
				k = 0;
				for (int i = decalSize; i >=0; i--)
				{
					Decal dec = beat.decals.get(i);
					dec.setPosition(beat.x, laneLength, beat.z - (-beat.length + k++) * beat.trailSpacing); // CHANGED
					decalBatch.add(dec);
					dec = null;
				}
				if(beat.z<=0)
				{
					beat.trailVeloc = beat.beatVeloc;
					beat.beatVeloc = 0;
					beat.landed = true;
				}
			}
			else if (beat.landed)
			{
				beat.trailVeloc += rate;
				beat.beatVeloc += rate; // CHANGED
				beat.y = laneLength - beat.beatVeloc; // CHANGED
				k = 0;
				for (int i = decalSize; i >= 0; i--)
				{
					Decal dec = beat.decals.get(i);
					if (dec.getZ() > 0)
					{
						dec.setPosition(beat.x, laneLength, laneLength - beat.trailVeloc - (-beat.length + k++) * beat.trailSpacing); // CHANGED
						if (i == 0)
							beat.trailVeloc = 0;
					}
					else
					{
						dec.setPosition(beat.x, beat.y - (-beat.length + k++) * beat.trailSpacing, 0); // CHANGED
					}
					decalBatch.add(dec);
					dec = null;
				}			
			}
			beat = null;
		}
		
		for(int x = 0; x < arrows.size(); x++)
		{
			Arrow beat = arrows.get(x);
			if (laneLength - beat.beatVeloc + (beat.length * beat.trailSpacing) < worldEnd) // CHANGED
			{
				arrows.set(x, null); // CHANGED --------------------------
				arrows.remove(x);
			}			
		}
		
		decalBatch.flush();

		
		uiStage.draw();
		
//		for (int x = 0, i = 0; x < arrowButtons.size(); x++, i+=laneStep)
//		{
//			float texW,texH;
//			float scale = 1;
//			texW = arrowButtons.get(x).getRegionWidth();
//			texH = arrowButtons.get(x).getRegionHeight();
//			buttonLocation = new Vector3(i - laneSize / 2 - laneSpan / 2 + laneStep/2, -worldHeight ,0);
//			camera.project(buttonLocation);
//			batch.draw(arrowButtons.get(x), buttonLocation.x - WIDTH/2 - texW*scale/2, buttonLocation.y - texH*scale/2 - HEIGHT/2,0,0,texW,texH,scale,scale,0);
////			dec.lookAt(camera.position, camera.up);
////			System.out.println(dec.getPosition());
//		}
		
		
//		veloc += worldHeight * 6 * Gdx.graphics.getDeltaTime() * 0.5f;
//
//		batch.draw(arrowSet.get(0), -laneSize / 2 - laneSpan / 2, worldHeight * 6 - veloc, laneSize, textureRegion.getRegionHeight());
//		batch.draw(arrowSet.get(1), laneStep - laneSize / 2 - laneSpan / 2, worldHeight * 6 - veloc, laneSize, textureRegion.getRegionHeight());
//		batch.draw(arrowSet.get(2), laneStep * 2 - laneSize / 2 - laneSpan / 2, worldHeight * 6 - veloc, laneSize, textureRegion.getRegionHeight());
//		batch.draw(arrowSet.get(3), laneStep * 3 - laneSize / 2 - laneSpan / 2, worldHeight * 6 - veloc, laneSize, textureRegion.getRegionHeight());
//		
//		if (worldHeight * 6 - veloc < -worldHeight * 2)
//			veloc = 0;
		
//		matrix3.setToRotation(0, 0, 0, 90);
//		matrix2.setToRotation(0, 0, 0, 90);
//		matrix2.mul(matrix3);
//		batch.setTransformMatrix(matrix2);

//		batch.end();
//
//		// 2D UI Rendering
//		matrix2.setToRotation(0, 0, 0, 0);
//		batch.setProjectionMatrix(camera2d.combined);
//		batch.setTransformMatrix(matrix2);
//		batch.begin();		
////		 batch.draw(texture, -texture.getRegionWidth() / 2,
////		 -texture.getRegionHeight() / 2);
		
	}

	@Override
	public void dispose()
	{
		super.dispose();

		for(ArrowButton arrowButton : buttons)
			arrowButton.dispose();
		
		playing = false;
		device.dispose();
		decoder.dispose();
		Gdx.files.external("temp1/payphone.mp3").delete();
		
		batch.dispose();
		decalBatch.dispose();
		uiStage.dispose();
	}

	@Override
	public void pause()
	{
		super.pause();
	}

	@Override
	public void resume()
	{
		super.resume();
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen)
	{
		super.setScreen(screen);
	}

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	Vector3 coord;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
//		Gdx.app.log("input: ", screenX + " " + screenY);
//		
//		coord = getPointOnPlane(Gdx.input);
//		Gdx.app.log("world xy: x: ", coord.x + " y: " + coord.y + " cam: "+ plane.distance(camera.position));
//		camera.project(coord);
//		Gdx.app.log("window xy: x: ", coord.x + " y: " + coord.y + " cam: "+ plane.distance(camera.position));
//		
		//TODO
		//Transfer buttons to 2D camera using plane <-> project
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

}
