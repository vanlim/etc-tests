package com.klabcyscorpions.beatsmaster.ui;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class UITest implements ApplicationListener, InputProcessor
{
	private static final float WIDTH = 800;
	private static final float HEIGHT = 480;

	float screenX;
	float screenY;
	float xRatio;
	float yRatio;

	OrthographicCamera camera2d;
	Stage stage;

	SpriteBatch batch;

	TweenManager tweenManager;

	public UITest()
	{

	}

	@Override
	public void create()
	{
		screenX = Gdx.graphics.getWidth();
		screenY = Gdx.graphics.getHeight();

		xRatio = screenX / WIDTH;
		yRatio = screenY / HEIGHT;

		camera2d = new OrthographicCamera(screenX, screenY);
		camera2d.position.set(0, 0, 0);

		stage = new Stage(screenX, screenY, false);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO Auto-generated method stub
		screenX = Gdx.graphics.getWidth();
		screenY = Gdx.graphics.getHeight();
	}

	@Override
	public void render()
	{
		// TODO Auto-generated method stub

	}

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
