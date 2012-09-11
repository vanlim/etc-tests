package interpolationtests;

import java.util.ArrayList;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CurveTest implements ApplicationListener
{
	float screenWidth;
	float screenHeight;
	OrthographicCamera camera2d;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Vector3[] path;
	
	ArrayList<Vector3> curvePath;

	CatmullRomSpline spline;

	public CurveTest()
	{

	}

	@Override
	public void create()
	{
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		camera2d = new OrthographicCamera(screenWidth, screenHeight);
		camera2d.position.set(screenWidth / 2, screenHeight / 2, 0);

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		curvePath = new ArrayList<Vector3>();
		spline = new CatmullRomSpline();

		
		
	}

	@Override
	public void resize(int width, int height)
	{
		camera2d.update();
	}

	@Override
	public void render()
	{
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl10.glClearColor(1, 1, 1, 1);

		camera2d.update();

		batch.setProjectionMatrix(camera2d.combined);
		batch.begin();

		batch.end();
		
		if (Gdx.input.isTouched() && touchOnce)
		{
			spline.add(new Vector3(Gdx.input.getX(),  screenHeight - Gdx.input.getY(), 0));
			touchOnce = false;
		}

		if (Gdx.input.justTouched())
			touchOnce = true;

		if (spline.getControlPoints().size() > 4)
		{
			spline.getPath(curvePath, 10);
			shapeRenderer.setColor(1, 1, 1, 1);
			shapeRenderer.begin(ShapeType.FilledCircle);
			for (int i = 0; i < curvePath.size()-1 ; i++)
			{
				Vector3 point = curvePath.get(i);
				shapeRenderer.filledCircle(point.x, point.y, 2);
			}
			shapeRenderer.setColor(1, 0, 0, 1);
			for (int i = 0; i < spline.getControlPoints().size(); i++)
			{
				Vector3 point = spline.getControlPoints().get(i);
				shapeRenderer.filledCircle(point.x, point.y, 4);
			}
			shapeRenderer.end();
			
		}
		
		float delta = Gdx.graphics.getDeltaTime();
		
		shapeRenderer.setColor(0, 0, 1, 1);
		shapeRenderer.begin(ShapeType.FilledCircle);
		if(spline.getControlPoints().size() > 4)
		{
			end = spline.getControlPoints().get(idx).cpy();
			start = curvePath.get(idx2).cpy();
			start.x = Interpolation.linear.apply(start.x, end.x, start.x);
			start.y = Interpolation.linear.apply(start.y, end.y, start.y);
			shapeRenderer.filledCircle(start.x,start.y, 4);
			if(start.x >= spline.getControlPoints().get(idx).cpy().x &&  start.y >= spline.getControlPoints().get(idx).cpy().y)
			{
				idx++;
			}
			
				idx2++;
			
		}
		shapeRenderer.end();
	}
	Vector3 start;
	Vector3 end;
	float x;
	float y;
	int idx2;
	int idx = 0;
	boolean touchOnce = true;

	@Override
	public void pause()
	{

	}

	@Override
	public void resume()
	{

	}

	@Override
	public void dispose()
	{
	}

}
