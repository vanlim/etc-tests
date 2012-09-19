package tests;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ArrowButton extends Actor
{

	private Vector3 position = new Vector3(0, 0, 0);
	private Vector2 size = new Vector2(0, 0);
	private Vector2 scale = new Vector2(0, 0);
	private Vector2 ratio = new Vector2(0, 0);

	private PerspectiveCamera camera;
	
	public int id = 0;

	boolean isPressed = false;
	boolean pressOnce = false;
	boolean hit;
	boolean longPress;
	boolean touchDown;
	boolean touchUp;
	
	boolean delay;

	long lastTimePressed;
	
	private TextureRegion textureRegion;

	String name;

	ShapeRenderer renderer;

	CustomParticleEffect effect;
	CustomParticleEffect tempFx;
	Texture tempTexture;

	ArrayList<CustomParticleEffect> effects;

	FileHandle effectFile;
	FileHandle effectImgDir;

	public ArrowButton(int id,Texture texture, String name, PerspectiveCamera camera, float x, float y, float z, float width, float height, float scaleX,
			float scaleY, float horizRatio, float vertRatio)
	{
		this.camera = camera;
		
		this.id = id;

		position.x = x;
		position.y = y;
		position.z = z;

		this.camera.update();
		this.camera.project(position);

		size.x = width;
		size.y = height;

		scale.x = scaleX;
		scale.y = scaleY;

		ratio.x = horizRatio;
		ratio.y = vertRatio;

		this.name = name;

		textureRegion = new TextureRegion(texture);

		setPosition(position.x, position.y);
		setWidth(size.x * ratio.x * scale.x);
		setHeight(size.y * ratio.y * scale.y);

		effect = new CustomParticleEffect();
		effectFile = Gdx.files.internal("particle/" + name + ".p");
		effectImgDir = Gdx.files.internal("texture");
		effect.load(effectFile, effectImgDir);
		tempTexture = effect.getEmitters().get(effect.getEmitters().size-1).getSprite().getTexture();

		effects = new ArrayList<CustomParticleEffect>();

		this.addListener(pressListener);
	}

	public boolean isPressed()
	{
		return isPressed;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha)
	{
		super.draw(batch, parentAlpha);

		batch.draw(textureRegion, position.x - size.x * scale.x * ratio.x / 2, position.y - size.y * scale.y * ratio.y / 2, 0, 0, size.x * ratio.x, size.y
				* ratio.y, scale.x, scale.y, 0);

		
		for (CustomParticleEffect effect : effects)
		{
			effect.draw(batch, Gdx.graphics.getDeltaTime());
			if (effect.isComplete())
			{
				effect.dispose();
			}
		}

		if (hit)
		{
			
			if (tempFx != null && tempFx.isComplete())
			{
				tempFx.dispose();
				tempFx = null;
				effects.clear(); 
			}
			tempFx = new CustomParticleEffect(effect);
//			tempFx.load(effectFile, effectImgDir);
			tempFx.setPosition(position.x, position.y);
			tempFx.allowCompletion();
			
			if(longPress == false)
			{
				for (CustomParticleEmitter emitter : tempFx.getEmitters())
				{
					emitter.horizRatio = ratio.x;
					emitter.vertRatio = ratio.y;
				}
			}
			else
			{
				for (CustomParticleEmitter emitter : tempFx.getEmitters())
				{
					emitter.horizRatio = ratio.x;
					emitter.vertRatio = ratio.y;
					emitter.setMaxParticleCount(1);
				}
			}
			if(longPress == false)
			{
				hit = false;
			}
			else
				hit = true;
			
			effects.add(tempFx);
			effects.get(effects.size()-1).start();
		}
		batch.end();
		batch.begin();
	}

	@Override
	public void act(float delta)
	{
		super.act(delta);
	}

	CustomParticleEmitter emitter;

	InputListener pressListener = new InputListener() {
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		{
			setScale(1.2f, 1.2f);
			isPressed = true;
			touchUp = false;
			touchDown = true;
			return true;
		}

		public void touchUp(InputEvent event, float x, float y, int pointer, int button)
		{
			setScale(1, 1);
			longPress = false;
			isPressed = false;
			touchUp = true;
			touchDown = true;
		}
	};

	@Override
	public void setPosition(float x, float y)
	{
		super.setPosition(x - size.x * scale.x * ratio.x / 2, y - size.y * scale.y * ratio.y / 2);
		position.x = x;
		position.y = y;
	}
	
	public void dispose()
	{
		effect.disposeTextures();
		effect.dispose();
		effects.clear();
		effects = null;
	}

	@Override
	public boolean remove()
	{
		return super.remove();
	}

	public Vector3 getWorldPosition()
	{
		return position;
	}

	@Override
	public void setScaleX(float scaleX)
	{
		super.setScaleX(scaleX);
		scale.x = scaleX;
	}

	@Override
	public void setScaleY(float scaleY)
	{
		super.setScaleY(scaleY);
		scale.y = scaleY;
	}

	@Override
	public void setScale(float scale)
	{
		super.setScale(scale);
		this.scale.x = scale;
		this.scale.y = scale;
	}

	@Override
	public void setScale(float scaleX, float scaleY)
	{
		super.setScale(scaleX, scaleY);
		scale.x = scaleX;
		scale.y = scaleY;
	}

}
