package com.klabcyscorpions.beatsmaster.objects;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Button extends Actor
{
	ShapeRenderer renderer;
	TextureRegion tex;
	
	private Vector3 position = new Vector3(0, 0, 0);
	private Vector2 size = new Vector2(0, 0);
	private Vector2 scale = new Vector2(0, 0);
	private Vector2 ratio = new Vector2(0, 0);
	private Camera camera;
	
	
	Matrix4 matrix;

	public Button(Texture texture, Camera camera, float x, float y, float z, float width, float height, float scaleX,
			float scaleY, float horizRatio, float vertRatio)
	{
		this.camera = camera;
		
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		
		this.camera.update();
		this.camera.project(position);
		
		size.x = width;
		size.y = height;

		scale.x = scaleX;
		scale.y = scaleY;

		ratio.x = horizRatio;
		ratio.y = vertRatio;
		
		tex = new TextureRegion(texture);

		setPosition(position.x, position.y);
		setWidth(size.x * ratio.x * scale.x);
		setHeight(size.y * ratio.y * scale.y);
		
		this.addListener(pressListener);
	}

	@Override
	public void act(float delta)
	{
		super.act(delta);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha)
	{
		// batch.setTransformMatrix(matrix);
		batch.draw(tex, position.x - size.x * scale.x * ratio.x / 2, position.y - size.y * scale.y * ratio.y / 2, 0, 0, size.x * ratio.x, size.y * ratio.y,
				scale.x, scale.y, 0);
	}

	InputListener pressListener = new InputListener() {
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
		{
			System.out.println("down");
			return true;
		}

		public void touchUp(InputEvent event, float x, float y, int pointer, int button)
		{
			System.out.println("up");
		}
	};
}
