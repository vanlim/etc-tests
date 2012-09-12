package com.klabcyscorpions.beatsmaster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.klabcyscorpions.beatsmaster.BeatMaster;
import com.klabcyscorpions.beatsmaster.objects.Button;

public class GameScreen extends AbstractScreen
{
	private Stage gameStage;
	private PerspectiveCamera gameCamera;

	SpriteBatch batch;
	TextureRegion tex;

	public GameScreen(BeatMaster game)
	{
		super(game);
		gameStage = new Stage();
		gameCamera = new PerspectiveCamera(67, width, height);
		gameCamera.position.set(0, 0, 550);
		gameCamera.near = 0.1f;
		gameCamera.far = 1024f;
		gameCamera.update();
		gameStage.setCamera(gameCamera);
	}

	@Override
	public void show()
	{
		super.show();
		batch = new SpriteBatch();
		tex = new TextureRegion(new Texture(Gdx.files.internal("data/libgdx.png")), 0, 0, 512, 275);
		inputMultiplexer.addProcessor(gameStage);		
	}

	@Override
	public void render(float delta)
	{
		super.render(delta);

		gameStage.act();
		gameStage.draw();
	}

	@Override
	public void dispose()
	{
		super.dispose();
		batch.dispose();
		// gameStage.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		// gameStage.setViewport(width, height, true);
	}

}
