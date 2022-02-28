package TRPG;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.lang.Comparable;
import java.util.WeakHashMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

abstract public class Sprite implements Comparable<Sprite> {
	protected String name;
	protected boolean failedLoad = false;
	public Texture texture;
	private static WeakHashMap<String, Texture> textures = new WeakHashMap<String, Texture>();
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;
	public boolean applyScaling = false;
	public float xscale = 1f;
	public float yscale = 1f;
	public float tL = 0f;
	public float tT = 0f;
	public float tR = 1f;
	public float tB = 1f;
	/* Old vars for billboard origin at bottom center
	protected static float right0;
	protected static float right1;
	protected static float right2;
	//*/
	protected static float rightup0p;
	protected static float rightup0n;
	protected static float rightup1p;
	protected static float rightup1n;
	protected static float rightup2p;
	protected static float rightup2n;
	private float depthIndex;
	private static float compareIndex;

	public int compareTo(Sprite s) {
		compareIndex = this.depthIndex - s.depthIndex;
		if (compareIndex < 0f) {
			return -1;
		} else if (compareIndex > 0f) {
			return 1;
		} else {
			return 0;
		}
	}

	public Sprite(String name) {
		this.name = name;
		this.failedLoad = !this.load();
	}
	public Sprite position(float xpos, float ypos, float zpos) {
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.updateDepthIndex();
		return this;
	}
	public Sprite scale(float scale) {
		return scale(scale, scale);
	}
	public Sprite scale(float xscale, float yscale) {
		this.xscale = xscale;
		this.yscale = yscale;
		this.applyScaling = (xscale != 1f || yscale != 1f);
		return this;
	}

	public void updateDepthIndex() {
		switch (Main.camDir) {
			case 0:
				this.depthIndex = this.ypos + this.xpos + this.zpos;
				break;
			case 1:
				this.depthIndex = this.ypos + this.xpos - this.zpos;
				break;
			case 2:
				this.depthIndex = this.ypos - this.xpos - this.zpos;
				break;
			case 3:
				this.depthIndex = this.ypos - this.xpos + this.zpos;
				break;
		}
	}

	protected boolean load() {
		String filepath = "assets/sprites/" + this.name + ".png";
		try {
			File file = new File(filepath);
			// Check for sprite existance
			this.texture = (Texture) Sprite.textures.get(this.name);
			if (this.texture != null) {
				return true;
			}
			if (!file.exists()) {
				System.out.println("ERROR: " + file.toString() + " could not be loaded.");
				return false;
			}
			// File input
			FileInputStream textureFile = new FileInputStream(file);
			this.texture = TextureLoader.getTexture("PNG", textureFile, GL11.GL_LINEAR);
			textureFile.close();
			// Store in hashmap
			Sprite.textures.put(this.name, this.texture);
		} catch (EOFException error) {
			System.out.println("ERROR: EOF reached while processing" + filepath);
			return false;
		} catch (FileNotFoundException error) {
			System.out.println("ERROR: File could not be found while processing " + filepath);
			return false;
		} catch (IOException error) {
			System.out.println("ERROR: While processing " + filepath);
			return false;
		}

		// Everything went better than expected
		return true;
	}

	public void render() {
		// Don't render if failed to load
		if (this.failedLoad) {
			return;
		}
		this.renderInternal();
	}

	public void render(float xpos, float ypos, float zpos) {
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.render();
	}

	abstract protected void renderInternal();
}
