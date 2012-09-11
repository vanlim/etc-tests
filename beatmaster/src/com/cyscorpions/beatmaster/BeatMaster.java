package com.cyscorpions.beatmaster;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cyscorpions.beatmaster.screens.GameScreen;

public class BeatMaster extends Game
{
	public static final String LOG = BeatMaster.class.getSimpleName();
	
	private PerspectiveCamera gameCamera;

	SpriteBatch batch;
	TextureRegion tex;

	@Override
	public void create()
	{
		Gdx.app.log(LOG, "Creating game on " + Gdx.app.getType());
		setScreen(new GameScreen(this));
	}
	
	@Override
	public void setScreen(Screen screen)
	{
		super.setScreen(screen);
		Gdx.app.log(LOG, "Setting screen: " + screen.getClass().getSimpleName());
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void render()
	{
		super.render();
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
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
}
