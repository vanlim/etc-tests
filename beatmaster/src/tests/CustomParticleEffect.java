package tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CustomParticleEffect implements Disposable {
	private final Array<CustomParticleEmitter> emitters;

	public CustomParticleEffect () {
		emitters = new Array<CustomParticleEmitter>(8);
	}

	public CustomParticleEffect (CustomParticleEffect effect) {
		emitters = new Array<CustomParticleEmitter>(true, effect.emitters.size);
		for (int i = 0, n = effect.emitters.size; i < n; i++)
			emitters.add(new CustomParticleEmitter(effect.emitters.get(i)));
	}

	public void start () {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).start();
	}

	public void update (float delta) {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).update(delta);
	}

	public void draw (SpriteBatch spriteBatch) {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).draw(spriteBatch);
	}

	public void draw (SpriteBatch spriteBatch, float delta) {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).draw(spriteBatch, delta);
	}

	public void allowCompletion () {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).allowCompletion();
	}

	public boolean isComplete () {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
			if (emitter.isContinuous()) return false;
			if (!emitter.isComplete()) return false;
		}
		return true;
	}

	public void setDuration (int duration) {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
			emitter.setContinuous(false);
			emitter.duration = duration;
			emitter.durationTimer = 0;
		}
	}

	public void setPosition (float x, float y) {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).setPosition(x, y);
	}

	public void setFlip (boolean flipX, boolean flipY) {
		for (int i = 0, n = emitters.size; i < n; i++)
			emitters.get(i).setFlip(flipX, flipY);
	}

	public Array<CustomParticleEmitter> getEmitters () {
		return emitters;
	}

	/** Returns the emitter with the specified name, or null. */
	public CustomParticleEmitter findEmitter (String name) {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
			if (emitter.getName().equals(name)) return emitter;
		}
		return null;
	}

	public void save (File file) {
		Writer output = null;
		try {
			output = new FileWriter(file);
			int index = 0;
			for (int i = 0, n = emitters.size; i < n; i++) {
				CustomParticleEmitter emitter = emitters.get(i);
				if (index++ > 0) output.write("\n\n");
				emitter.save(output);
				output.write("- Image Path -\n");
				output.write(emitter.getImagePath() + "\n");
			}
		} catch (IOException ex) {
			throw new GdxRuntimeException("Error saving effect: " + file, ex);
		} finally {
			try {
				if (output != null) output.close();
			} catch (IOException ex) {
			}
		}
	}

	public void load (FileHandle effectFile, FileHandle imagesDir) {
		loadEmitters(effectFile);
		loadEmitterImages(imagesDir);
	}

	public void load (FileHandle effectFile, TextureAtlas atlas) {
		loadEmitters(effectFile);
		loadEmitterImages(atlas);
	}

	public void loadEmitters (FileHandle effectFile) {
		InputStream input = effectFile.read();
		emitters.clear();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(input), 512);
			while (true) {
				CustomParticleEmitter emitter = new CustomParticleEmitter(reader);
				reader.readLine();
				emitter.setImagePath(reader.readLine());
				emitters.add(emitter);
				if (reader.readLine() == null) break;
				if (reader.readLine() == null) break;
			}
		} catch (IOException ex) {
			throw new GdxRuntimeException("Error loading effect: " + effectFile, ex);
		} finally {
			try {
				if (reader != null) reader.close();
				if (input != null) input.close();
			} catch (IOException ex) {
			}
		}
	}

	public void loadEmitterImages (TextureAtlas atlas) {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
			String imagePath = emitter.getImagePath();
			if (imagePath == null) continue;
			String imageName = new File(imagePath.replace('\\', '/')).getName();
			int lastDotIndex = imageName.lastIndexOf('.');
			if (lastDotIndex != -1) imageName = imageName.substring(0, lastDotIndex);
			Sprite sprite = atlas.createSprite(imageName);
			if (sprite == null) throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
			emitter.setSprite(sprite);
			sprite = null;
		}
	}

	public void loadEmitterImages (FileHandle imagesDir) {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
			String imagePath = emitter.getImagePath();
			if (imagePath == null) continue;
			String imageName = new File(imagePath.replace('\\', '/')).getName();
			emitter.setSprite(new Sprite(loadTexture(imagesDir.child(imageName))));
		}
	}

	protected Texture loadTexture (FileHandle file) {
		return new Texture(file, false);
	}
	
	/** Disposes the texture for each sprite for each ParticleEmitter. */
	public void dispose () {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
//			emitter.getSprite().getTexture().dispose();
			emitter.dispose();
		}
		emitters.clear();
	}
	

	public void disposeTextures () {
		for (int i = 0, n = emitters.size; i < n; i++) {
			CustomParticleEmitter emitter = emitters.get(i);
			emitter.getSprite().getTexture().dispose();
		}
	}
}

