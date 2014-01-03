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

public class Sprite implements Comparable<Sprite>{
	protected String name;
	private File file;
	public Texture texture;
	private static WeakHashMap<String,Texture> textures = new WeakHashMap<String,Texture>();
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;
	private boolean isPointSprite = false;
	public boolean applyScaling = false;
	public float scale = 100.0f;
	public float xscale = 1f;
	public float yscale = 1f;
	public float tL = 0f;
	public float tT = 0f;
	public float tR = 1f;
	public float tB = 1f;
	/* Old vars for billboard origin at bottom center
	private static float right0;
	private static float right1;
	private static float right2;
	//*/
	private static float rightup0p;
	private static float rightup0n;
	private static float rightup1p;
	private static float rightup1n;
	private static float rightup2p;
	private static float rightup2n;
	private float depthIndex;
	private static float compareIndex;

	public int compareTo(Sprite s){
		compareIndex = this.depthIndex - s.depthIndex;
		if(compareIndex < 0f){
			return -1;
		}else if(compareIndex > 0f){
			return 1;
		}else{
			return 0;
		}
	}

	public Sprite(String name){
		this.name = name;
		this.file = new File("assets/sprites/"+this.name+".png");
		this.load();
	}
	public Sprite(String name, boolean isPointSprite){
		this.name = name;
		this.file = new File("assets/sprites/"+this.name+".png");
		this.isPointSprite = isPointSprite;
		this.load();
	}
	public Sprite(String name, float xpos, float ypos, float zpos){
		this.name = name;
		this.file = new File("assets/sprites/"+this.name+".png");
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.load();
	}
	public Sprite(String name, boolean isPointSprite, float xpos, float ypos, float zpos){
		this.name = name;
		this.file = new File("assets/sprites/"+this.name+".png");
		this.isPointSprite = isPointSprite;
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.load();
	}

	public void updateDepthIndex(float xpos, float ypos, float zpos){
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.updateDepthIndex();
	}

	public void updateDepthIndex(){
		switch(TRPG.camDir){
			case 0:	this.depthIndex = this.ypos + this.xpos + this.zpos; break;
			case 1:	this.depthIndex = this.ypos + this.xpos - this.zpos; break;
			case 2:	this.depthIndex = this.ypos - this.xpos - this.zpos; break;
			case 3:	this.depthIndex = this.ypos - this.xpos + this.zpos; break;
		}
	}

	private void load(){
		try{
			// Check for sprite existance
			this.texture = (Texture)Sprite.textures.get(this.name);
			if(this.texture != null){
				return;
			}
			if(!this.file.exists()){
				System.out.println("ERROR: " + this.file.toString() + " could not be loaded.");
				return;
			}
			// File input
			FileInputStream textureFile = new FileInputStream(this.file);
			this.texture = TextureLoader.getTexture("PNG", textureFile, GL11.GL_LINEAR);
			textureFile.close();
			// Store in hashmap
			Sprite.textures.put(this.name, this.texture);
			// Everything went better than expected
			return;
		}catch(EOFException error){
			System.out.println("ERROR: EOF reached while processing" + this.file.toString());
			return;
		}catch(FileNotFoundException error){
			System.out.println("ERROR: File could not be found while processing " + this.file.toString());
			return;
		}catch(IOException error){
			System.out.println("ERROR: While processing " + this.file.toString());
			return;
		}
	}

	public void render(){
		if(isPointSprite){
			this.renderPointSprite();
		}else{
			this.renderBillboard();
		}
	}

	public void render(float xpos, float ypos, float zpos){
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		if(isPointSprite){
			this.renderPointSprite();
		}else{
			this.renderBillboard();
		}
	}

	public void renderPointSprite(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture.getTextureID());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL20.GL_POINT_SPRITE);
		GL11.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_COORD_REPLACE, GL11.GL_TRUE);
		if(this.applyScaling){
			GL11.glPointSize(this.scale);
		}else{
			GL11.glPointSize(100f);
		}
		GL11.glBegin(GL11.GL_POINTS);
			GL11.glVertex3f(this.xpos,this.ypos,this.zpos);
		GL11.glEnd();
		GL11.glDisable(GL20.GL_POINT_SPRITE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void renderBillboard(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture.getTextureID());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		/* Old calculation with vertex at bottom center
		// Calculate vertex mods to adjust for camera rotation
		if(this.applyScaling){
			// Apply scaling if needed
			right0 = TRPG.rightMod[0] * 0.5f * this.xscale;
			right1 = TRPG.rightMod[1] * 0.5f * this.xscale;
			right2 = TRPG.rightMod[2] * 0.5f * this.xscale;
			rightup0p = right0 + (TRPG.upMod[0] * this.yscale);
			rightup1p = right1 + (TRPG.upMod[1] * this.yscale);
			rightup2p = right2 + (TRPG.upMod[2] * this.yscale);
			rightup0n = -right0 + (TRPG.upMod[0] * this.yscale);
			rightup1n = -right1 + (TRPG.upMod[1] * this.yscale);
			rightup2n = -right2 + (TRPG.upMod[2] * this.yscale);
		}else{
			right0 = TRPG.rightMod[0] * 0.5f;
			right1 = TRPG.rightMod[1] * 0.5f;
			right2 = TRPG.rightMod[2] * 0.5f;
			rightup0p = right0 + TRPG.upMod[0];
			rightup1p = right1 + TRPG.upMod[1];
			rightup2p = right2 + TRPG.upMod[2];
			rightup0n = -right0 + TRPG.upMod[0];
			rightup1n = -right1 + TRPG.upMod[1];
			rightup2n = -right2 + TRPG.upMod[2];
		}
			GL11.glTexCoord2f(this.tL,this.tB);
			GL11.glVertex3f(this.xpos - right0,this.ypos - right1,this.zpos - right2);
			GL11.glTexCoord2f(this.tR,this.tB);
			GL11.glVertex3f(this.xpos + right0,this.ypos + right1,this.zpos + right2);
			GL11.glTexCoord2f(this.tR,this.tT);
			GL11.glVertex3f(this.xpos + rightup0p,this.ypos + rightup1p,this.zpos + rightup2p);
			GL11.glTexCoord2f(this.tL,this.tT);
			GL11.glVertex3f(this.xpos + rightup0n,this.ypos + rightup1n,this.zpos + rightup2n);
		//*/
		if(this.applyScaling){
			rightup0p = (TRPG.rightMod[0] * this.xscale) + (TRPG.upMod[0] * this.yscale);
			rightup1p = (TRPG.rightMod[1] * this.xscale) + (TRPG.upMod[1] * this.yscale);
			rightup2p = (TRPG.rightMod[2] * this.xscale) + (TRPG.upMod[2] * this.yscale);
			rightup0n = (TRPG.rightMod[0] * this.xscale) - (TRPG.upMod[0] * this.yscale);
			rightup1n = (TRPG.rightMod[1] * this.xscale) - (TRPG.upMod[1] * this.yscale);
			rightup2n = (TRPG.rightMod[2] * this.xscale) - (TRPG.upMod[2] * this.yscale);
		}else{
			rightup0p = (TRPG.rightMod[0] + TRPG.upMod[0]);
			rightup1p = (TRPG.rightMod[1] + TRPG.upMod[1]);
			rightup2p = (TRPG.rightMod[2] + TRPG.upMod[2]);
			rightup0n = (TRPG.rightMod[0] - TRPG.upMod[0]);
			rightup1n = (TRPG.rightMod[1] - TRPG.upMod[1]);
			rightup2n = (TRPG.rightMod[2] - TRPG.upMod[2]);
		}
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(this.tL,this.tB);
			GL11.glVertex3f(this.xpos - rightup0p,this.ypos - rightup1p,this.zpos - rightup2p);
			GL11.glTexCoord2f(this.tR,this.tB);
			GL11.glVertex3f(this.xpos + rightup0n,this.ypos + rightup1n,this.zpos + rightup2n);
			GL11.glTexCoord2f(this.tR,this.tT);
			GL11.glVertex3f(this.xpos + rightup0p,this.ypos + rightup1p,this.zpos + rightup2p);
			GL11.glTexCoord2f(this.tL,this.tT);
			GL11.glVertex3f(this.xpos - rightup0n,this.ypos - rightup1n,this.zpos - rightup2n);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}