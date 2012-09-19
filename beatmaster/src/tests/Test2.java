package tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Test2 extends Game implements InputProcessor
{
	private PerspectiveCamera camera;
	private SpriteBatch batch;

	float HEIGHT;
	float WIDTH;
	float worldWidth;
	float worldHeight;
	float horizRatio;
	float vertRatio;
	
	private Stage uiStage;
	
	private InputMultiplexer inputMultiplexer;
	
	private ArrowButton arrowButton;
	
	private Texture texture;
	
	public Test2()
	{
		
		
	}

	@Override
	public void create()
	{
		HEIGHT = Gdx.graphics.getHeight();
		WIDTH = Gdx.graphics.getWidth();
		
		worldWidth = 800;
		worldHeight = 480;
		
		horizRatio = WIDTH / worldWidth;
		vertRatio = HEIGHT / worldHeight;
		
		camera = new PerspectiveCamera(67, worldWidth, worldHeight);
		camera.position.set(0,-40 * vertRatio, 20 * vertRatio).mul(20);
		camera.project(new Vector3(0, 0, 0), 0, 0, WIDTH, HEIGHT);
		camera.lookAt(0, 0, 0);
		camera.fieldOfView = 67;
		camera.near = 0.1f;
		camera.far = 5000f;
		batch = new SpriteBatch();
		
		uiStage = new Stage(worldWidth, worldHeight, true);
		
		texture = new Texture(Gdx.files.internal("texture/step_right_nocolor.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		arrowButton = new ArrowButton(0, texture, "left",camera, 0, 0, 0, 128, 128, 1, 1, horizRatio, vertRatio);
		
		uiStage.addActor(arrowButton);
		
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(uiStage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		batch.dispose();
	}

	@Override
	public void render()
	{
		super.render();
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		uiStage.act();
		uiStage.draw();
//		System.out.println(arrowButton.getY());
	}

	@Override
	public boolean keyDown(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
