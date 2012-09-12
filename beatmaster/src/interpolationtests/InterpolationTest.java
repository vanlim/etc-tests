package interpolationtests;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.klabcyscorpions.beatsmaster.utils.ImageAccessor;
import com.klabcyscorpions.beatsmaster.utils.LanguagesManager;
import com.klabcyscorpions.beatsmaster.utils.Vector3TweenAccessor;

public class InterpolationTest implements ApplicationListener
{
	PerspectiveCamera camera;
	OrthographicCamera camera2d;
	
	

	TweenManager tweenManager;

	SpriteBatch batch;
	DecalBatch decalBatch;
	Decal decal;

	CatmullRomSpline spline;
	Vector3 splinePath;

	Texture texture;
	Texture texture2;
	Texture texture3;
	Texture texture4;
	Texture texture5;
	Texture texture6;
	Texture texture7;
	Texture texture8;
	Texture texture9;
	Texture texture10;
	Vector3 position;

	float screenX;
	float screenY;
	float deltaTime;

	float startX;
	float endX;

	boolean touchOnce;
	ShapeRenderer shapeRenderer;

	BitmapFont font;
	BitmapFont font2;
	BitmapFont font3;

	List<Vector3> curvePath;
	
	LanguagesManager lang;

	int length = 0;
	
	public InterpolationTest()
	{
	}

	@Override
	public void create()
	{
		screenX = Gdx.graphics.getWidth();
		screenY = Gdx.graphics.getHeight();

		camera = new PerspectiveCamera(120, 1000, 1000);
		camera.position.set(0, 0, 0);
		camera.near = 0.1f;
		camera.far = 1000;

		camera2d = new OrthographicCamera(screenX, screenY);
		camera2d.position.set(0, 0, 0);

		batch = new SpriteBatch();
		decalBatch = new DecalBatch();

		tweenManager = new TweenManager();
		Tween.registerAccessor(Vector3.class, new Vector3TweenAccessor());

		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
//		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		texture2 = new Texture(Gdx.files.internal("texture/test/black.png"));
//		texture2.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		texture3 = new Texture(Gdx.files.internal("texture/test/redlane.png"));
//		texture2.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		texture4 = new Texture(Gdx.files.internal("texture/test/bluelane.png"));
//		texture2.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		texture5 = new Texture(Gdx.files.internal("texture/test/greenlane.png"));
//		texture2.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		texture6 = new Texture(Gdx.files.internal("texture/test/yellowlane.png"));
//		texture2.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		texture7 = new Texture(Gdx.files.internal("texture/step_left.png"));
		
		texture8 = new Texture(Gdx.files.internal("texture/step_down.png"));
		
		texture9 = new Texture(Gdx.files.internal("texture/step_up.png"));
		
		texture10 = new Texture(Gdx.files.internal("texture/step_right.png"));
		
		ArrayList<TextureRegion> texSet = new ArrayList<TextureRegion>();
		
			
			texSet.add(new TextureRegion(texture7));
			texSet.add(new TextureRegion(texture8));
			texSet.add(new TextureRegion(texture9));
			texSet.add(new TextureRegion(texture10));
			
		
		TextureRegion texReg = new TextureRegion(texture, 0, 0, 512,275);

		decal = Decal.newDecal(texReg, true);

		position = new Vector3(0, 0, 0);

		// Tween.setWaypointsLimit(10);

		spline = new CatmullRomSpline();
		shapeRenderer = new ShapeRenderer();
		curvePath = new ArrayList<Vector3>();
		
		lang = LanguagesManager.getInstance();		
//		lang.loadLanguage("ja_JP");		
		System.out.print(lang.getLanguage());

		for (int i = 0; i < 5; i++)
		{
			spline.add(new Vector3(MathUtils.random(-screenX/4, screenX/4), MathUtils.random(-screenY/2, screenY/2), length -= 900));
		}
		spline.getPath(curvePath, pathPointSize);

		font = new BitmapFont(Gdx.files.internal("font/roboto_bold_italic.fnt"), Gdx.files.internal("font/roboto_bold_italic.png"), false);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		font2 = new BitmapFont(Gdx.files.internal("font/asian_title.fnt"), Gdx.files.internal("font/asian_title_fx.png"), false);
		font2.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		font3 = new BitmapFont(Gdx.files.internal("font/microgramma.fnt"), Gdx.files.internal("font/microgramma_fx.png"), false);
		font3.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		tex = new Image(texReg);
		tex.setPosition(-screenX*2,-tex.getHeight()/2);
		tex.setScaleY(1);
		
		arrow1 = new Image(texSet.get(0));
		arrow1.setPosition(screenX*2, -arrow1.getHeight());
		arrow2 = new Image(texSet.get(1));
		arrow2.setPosition(-arrow2.getWidth(), screenY*2);
		arrow3 = new Image(texSet.get(2));
		arrow3.setPosition(0, -screenY*2);
		arrow4 = new Image(texSet.get(3));
		arrow4.setPosition(-screenX*2, -arrow4.getHeight());
		
		float duration = 2;
		float delay = 0;
		 
		Tween.registerAccessor(Image.class, new ImageAccessor());
		
		Timeline.createSequence()
			.push(Tween.to(tex, ImageAccessor.position_x, duration).target(-tex.getWidth()/2).ease(TweenEquations.easeInOutExpo))
			
			.beginParallel()
				.push(Tween.to(tex, ImageAccessor.position_y, 1).target(0))
				.push(Tween.to(arrow1, ImageAccessor.position_x, 1).target(-arrow1.getWidth()*2).ease(TweenEquations.easeInOutExpo).delay(delay+=0.1f))
				.push(Tween.to(arrow2, ImageAccessor.position_y, 1).target(-arrow2.getHeight()).ease(TweenEquations.easeInOutExpo).delay(delay+=0.1f))
				.push(Tween.to(arrow3, ImageAccessor.position_y, 1).target(-arrow2.getHeight()).ease(TweenEquations.easeInOutExpo).delay(delay+=0.1f))
				.push(Tween.to(arrow4, ImageAccessor.position_x, 1).target(arrow4.getWidth()).ease(TweenEquations.easeInOutExpo).delay(delay+=0.1f))
			.end()
		.start(tweenManager);
		
	}

	Image tex;
	Image arrow1;
	Image arrow2;
	Image arrow3;
	Image arrow4;
	
	@Override
	public void resize(int width, int height)
	{
		// TODO Auto-generated method stub

	}

	Decal dec;
	int idx = 1;
	int pathPointSize = 100;
	boolean isComplete;
	Vector3 upVector = new Vector3(0, 1, 0);
	List<Vector3> tangents;
	Vector2 newPoint;
	
	public static Vector2 rotate(float x, float y, float a, float b, float angleDeg) {
		   float cos = MathUtils.cosDeg(angleDeg);
		   float sin = MathUtils.sinDeg(angleDeg);
		   return new Vector2(cos*(x-a) - sin*(y-b) + a, sin*(x-a) + cos*(y-b) + b);
	}
	
	@Override
	public void render()
	{
		deltaTime = Gdx.graphics.getDeltaTime();
		tweenManager.update(deltaTime);
		
		camera.update();

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl10.glClearColor(0.2f, 0.2f, 0.2f, 1);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.end();

		
		
		// if (Gdx.input.isTouched() && touchOnce)
		// {
		// curvePath.clear();
		// if (spline.getControlPoints().size() > 8)
		// {
		// for(int i= 0; i < spline.getControlPoints().size() - 10; i++)
		// {
		// spline.getControlPoints().remove(i);
		// }
		// idx = ((spline.getControlPoints().size()-2) *pathPointSize) + 2 -
		// pathPointSize*7;
		// }
		// spline.add(new Vector3(clickx,clicky,0));
		// spline.getPath(curvePath, pathPointSize);
		// touchOnce = false;
		// }
		//
		if (idx == ((spline.getControlPoints().size() - 2) * pathPointSize) + 2 - pathPointSize * 2)
		// if(idx == pathPointSize*2+1 )
		{
			// List<Vector3> temp = new ArrayList<Vector3>();
			// for(int x = 0; x < spline.getControlPoints().size(); x++)
			// temp.add(curvePath.get(idx+x));
//			 System.out.print(idx + " " + curvePath.get(idx) + " ---- ");
			// for(int x = 0; x < 10; x ++)
			// System.out.println(curvePath.get(x));
			// System.out.println("-------------------------");
			if (spline.getControlPoints().size() > 4)
			{
				for (int x = 0; x < spline.getControlPoints().size() - 3; x++)
				{
					spline.getControlPoints().remove(x);
				}
				// idx = pathPointSize;
			}
			idx = (spline.getControlPoints().size() - 2) * pathPointSize - pathPointSize * 2 + 2;
			curvePath.clear();
			spline.add(new Vector3(MathUtils.random(-screenX/4, screenX/4), MathUtils.random(-screenY/2, screenY/2), length -= 900));
			spline.getPath(curvePath, pathPointSize);
			// for(int x = 8-spline.getControlPoints().size(); x > 0; x--)
			// curvePath.add(idx,temp.get(x));
			// for(int x = 0; x < 10; x ++)
			// System.out.println(curvePath.get(x));
			// System.out.println("-------------------------");
			// temp = null;
			// curvePath.remove(idx);
//			 System.out.println(idx+ " " + curvePath.get(idx));
		}

		tangents = spline.getTangents(pathPointSize);

		float fade = 0;
		
		if (spline.getControlPoints().size() > 4)
		{
			for (int i = idx + pathPointSize; i > idx / 2; i--)
			{
//				if (i % 3 == 0)
//				{
					Vector3 point = curvePath.get(i).cpy();
					Vector3 tempTan = tangents.get(i);

					//
					if(i%20 == 0)
					{
						for(int x = 0; x < 12; x++)
						{
							float newAngle = x*30;
							newPoint = rotate(point.x + 150, point.y, point.x, point.y, newAngle);
							dec = Decal.newDecal(new TextureRegion(texture, 16, 16), true);
							dec.setScale(8, 4);
							dec.setColor(1, 1, 1, fade);
							dec.setPosition(newPoint.x, newPoint.y+80, point.z);
							dec.setRotation(tempTan, upVector);
//							System.out.println(tempTan);
							dec.rotateY(90);
							dec.rotateX(newAngle);
							decalBatch.add(dec);
						}
						fade += 0.2f;
					}
					
//					dec = Decal.newDecal(new TextureRegion(texture5, 64, 64), true);
//					dec.setPosition(point.x, point.y-50, point.z);
//					dec.setRotation(tempTan, upVector);
//					dec.rotateX(90);
//					decalBatch.add(dec);
//					dec = Decal.newDecal(new TextureRegion(texture4, 64, 64), true);
//					dec.setPosition(point.x - 50, point.y , point.z);
//					dec.setRotation(tangents.get(i), new Vector3(0, 1, 0));
//					dec.rotateX(90);
//					dec.rotateY(90);
//					decalBatch.add(dec);
//					dec = Decal.newDecal(new TextureRegion(texture3, 64, 64), true);
//					dec.setPosition(point.x + 32, point.y , point.z);
//					dec.setRotation(tangents.get(i), new Vector3(0, 1, 0));
//					dec.rotateX(-90);
//					decalBatch.add(dec);
//					dec = Decal.newDecal(new TextureRegion(texture5, 64, 64), true);
//					dec.setPosition(point.x + 96, point.y, point.z);
//					dec.setRotation(tangents.get(i), new Vector3(0, 1, 0));
//					dec.rotateX(-90);
//					decalBatch.add(dec);

					dec = null;
					point = null;
					tempTan = null;

//				}
			}
			// dec.setPosition(point.x, point.y, point.z);
			// dec.rotateY(90);
			// dec.setRotation(tangents.get(i),new Vector3(0, 1, 0));
			// decalBatch.add(dec);

			// batch.draw(texture2,point.x-5,point.y-5,10,10);
			// batch.draw(texture,point.x-5,point.y+50-5,10,10);
			// batch.draw(texture,point.x-5,point.y-50-5,10,10);

			// for (int i = 0; i < spline.getControlPoints().size(); i++)
			// {
			// Vector3 point = spline.getControlPoints().get(i).cpy();
			// dec = Decal.newDecal(new TextureRegion(texture,64,64), true);
			// dec.setPosition(point.x,point.y,point.z);
			// decalBatch.add(dec);
			//
			// }
		}

		// if(Gdx.input.justTouched() && spline.getControlPoints().size() > 4)
		// {
		// Timeline.createSequence()
		// .push(Tween.to(position, Vector3TweenAccessor.position_xy,
		// 3f).target(clickx,clicky)
		// .path(TweenPaths.catmullRom)
		// .waypoint(spline.getControlPoints().get(spline.getControlPoints().size()-3).x,spline.getControlPoints().get(spline.getControlPoints().size()-3).y)
		// .waypoint(spline.getControlPoints().get(spline.getControlPoints().size()-2).x,spline.getControlPoints().get(spline.getControlPoints().size()-2).y)
		// .waypoint(spline.getControlPoints().get(spline.getControlPoints().size()-1).x,spline.getControlPoints().get(spline.getControlPoints().size()-1).y)
		// )
		// .start(tweenManager);
		// }

		// position.x = Interpolation.linear.apply(position.x, clickx,
		// deltaTime*2);
		// position.y = Interpolation.linear.apply(position.y, clicky,
		// deltaTime*2);

		if (spline.getControlPoints().size() > 4)
		{

			// decal.setRotation(tangents.get(idx),new Vector3(0, 1, 0));
			// point = spline.getControlPoints().get(idx).cpy();

			// if(idx == ((spline.getControlPoints().size()-2) *pathPointSize) -
			// pathPointSize*2 + 1)
			// {
			// System.out.println(camera.position);
			// point2 = curvePath.get(idx+1).cpy();
			// System.out.println(point2);
			// }
			// else
			point2 = curvePath.get(idx).cpy();

			float dist = position.dst(point2);
			for (float i = 0.0f; i < 1.0; i += (1*deltaTime) / dist)
			{

				position.lerp(point2, i);
				// decal.setPosition(position.x,position.y,position.z);
				camera.position.set(position);
			}

//			position.lerp(point2, deltaTime);
//			camera.position.set(position);
			
			// System.out.print(camera.position + " - ");

			// dist = camera.direction.dst(tangents.get(idx));
			// for (float i = 0.0f; i < 1.0; i += (1 * deltaTime) / dist)
			// {
			// camera.direction.lerp(tangents.get(idx),i);
			// }
			camera.direction.lerp(tangents.get(idx), deltaTime * 3);
			// camera.up.set(upVector);

			if ((Math.abs(point2.x - position.x) <= 10f && Math.abs(point2.y - position.y) <= 10f) && idx < curvePath.size() - 2)
			{
				idx++;
			}
		}
		tangents.clear();

		if (position.z < -Integer.MAX_VALUE)
		{
			length = 0;

			for (Vector3 temp : spline.getControlPoints())
			{
				// System.out.println(temp);
				temp.set(temp.x, temp.y, length -= 900);
				// System.out.println(temp);
			}
//			 idx = (spline.getControlPoints().size()-2) *pathPointSize -
//			 pathPointSize*2;
			curvePath.clear();
			spline.getPath(curvePath, pathPointSize);
//			temp = null;
		}

		// System.out.println((spline.getControlPoints().size()-2)
		// *pathPointSize + " " + idx);

		// decal.lookAt

		// decalBatch.add(decal);
		decalBatch.flush();

		// batch.end();

		batch.setProjectionMatrix(camera2d.combined);
		batch.begin();
		
		tex.draw(batch, 1);
		
		arrow1.draw(batch, 1);
		arrow2.draw(batch, 1);
		arrow3.draw(batch, 1);
		arrow4.draw(batch, 1);
		
		
		font.drawWrapped(batch, "Z: " + Float.toString(position.z), -screenX / 2, screenY / 2 - font.getAscent(), screenX);
		font.drawWrapped(batch, "FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond()), -screenX / 2, screenY / 2 - font.getAscent(), screenX, HAlignment.RIGHT);
		font3.setScale(0.5f);
		font3.drawWrapped(batch, "COMBO X2", -screenX / 2, font2.getCapHeight()/2, screenX, HAlignment.CENTER);
		font3.setScale(0.4f);
		font3.drawWrapped(batch, "PERFECT", -screenX / 2, 0, screenX, HAlignment.CENTER);
		font2.drawWrapped(batch, lang.getString("Beatsmaster"), -screenX / 2, -screenY / 2 + font2.getCapHeight() + 5 , screenX, HAlignment.LEFT);
		batch.end();
	}

	Vector3 point2;

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

}
