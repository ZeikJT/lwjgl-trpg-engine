package TRPG;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Sprite{
	private String name;
	private File file;
	private Texture texture;
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;
	public boolean applyScaling = false;
	public float scale = 100.0f;

	public Sprite(String name){
		this.name = name;
		this.file = new File("assets/sprites/"+this.name+".png");
	}
	public Sprite(String name, float xpos, float ypos, float zpos){
		this.name = name;
		this.file = new File("assets/sprites/"+this.name+".png");
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
	}

	public boolean load(){
		return Sprite.load(this);
	}

	public void render(){
		Sprite.render(this);
	}

	public void render(float xpos, float ypos, float zpos){
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		Sprite.render(this);
	}

	public static boolean load(Sprite sprite){
		try{
			if(!sprite.file.exists()){
				System.out.println("ERROR: " + sprite.file.toString() + " could not be loaded.");
				return false;
			}
			// File input
			FileInputStream textureFile = new FileInputStream(sprite.file);
			sprite.texture = TextureLoader.getTexture("PNG", textureFile);
			textureFile.close();
			// Everything went better than expected
			return true;
		}catch(EOFException error){
			System.out.println("ERROR: EOF reached while processing" + sprite.file.toString());
			return false;
		}catch(FileNotFoundException error){
			System.out.println("ERROR: File could not be found while processing " + sprite.file.toString());
			return false;
		}catch(IOException error){
			System.out.println("ERROR: While processing " + sprite.file.toString());
			return false;
		}
	}

	public static void render(Sprite sprite){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.texture.getTextureID());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL20.GL_POINT_SPRITE);
		GL11.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_COORD_REPLACE, GL11.GL_TRUE);
		if(sprite.applyScaling){
			GL11.glPointSize(sprite.scale);
		}else{
			GL11.glPointSize(100.0f);
		}
		GL11.glBegin(GL11.GL_POINTS);
			GL11.glVertex3f(sprite.xpos,sprite.ypos+0.8f,sprite.zpos);
		GL11.glEnd();
		GL11.glDisable(GL20.GL_POINT_SPRITE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}