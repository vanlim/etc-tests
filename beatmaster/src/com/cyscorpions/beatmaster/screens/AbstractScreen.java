package com.cyscorpions.beatmaster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.cyscorpions.beatmaster.BeatMaster;

public class AbstractScreen implements Screen
{
	public static final float width = Gdx.graphics.getWidth();
	public static final float height = Gdx.graphics.getHeight();

	protected final BeatMaster game;
	protected final Stage stage;
	
	protected InputMultiplexer inputMultiplexer = new InputMultiplexer();

	private SpriteBatch batch;
	private Skin skin;
	private BitmapFont font;
	private TextureAtlas atlas;

	private Table table;

	public AbstractScreen(BeatMaster game)
	{
		this.game = game;
		this.stage = new Stage(width, height, true);
	}

	public BitmapFont getFont()
	{
		return font;
	}

	public SpriteBatch getBatch()
	{
		if (batch == null)
		{
			batch = new SpriteBatch();
		}
		return batch;
	}

	public TextureAtlas getAtlas()
	{
		return atlas;
	}

	protected String getName()
	{
		return getClass().getSimpleName();
	}

	protected Skin getSkin()
	{
		return skin;
	}

	protected Table getTable()
	{
		if (table == null)
		{
			table = new Table(getSkin());
			table.setFillParent(true);
			stage.addActor(table);
		}
		return table;
	}

	protected boolean isGameScreen()
	{
		return false;
	}

	@Override
	public void show()
	{
		Gdx.app.log(BeatMaster.LOG, "Showing screen: " + getName());
		
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render(float delta)
	{
		// (1) process the game logic
		// update the actors
		stage.act(delta);

		// (2) draw the result
		// clear the screen with the given RGB color (black)
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// draw the actors
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		Gdx.app.log(BeatMaster.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height);
		stage.setViewport(width, height, true);
	}

	@Override
	public void hide()
	{
		Gdx.app.log(BeatMaster.LOG , "Hiding Screen: " + getName());
		dispose();
	}

	@Override
	public void pause()
	{
		Gdx.app.log(BeatMaster.LOG, "Pausing Screen: " + getName());
	}

	@Override
	public void resume()
	{
		Gdx.app.log(BeatMaster.LOG, "Resuming Screen: " + getName());
	}

	@Override
	public void dispose()
	{
		Gdx.app.log(BeatMaster.LOG, "Disposing Screen: " + getName());

		if (font != null)
			font.dispose();
		if (batch != null)
			batch.dispose();
		if (skin != null)
			skin.dispose();
		if (atlas != null)
			atlas.dispose();
	}

}
