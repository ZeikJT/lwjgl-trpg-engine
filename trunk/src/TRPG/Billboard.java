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

public class Billboard{
	private String name;
	private File file;
	private Texture texture;
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;
	public boolean applyScaling = false;
	public float xscale = 1f;
	public float yscale = 1f;
	public float tL = 0f;
	public float tR = 1f;
	public float tT = 0f;
	public float tB = 1f;
	/* Old vars for origin at bottom center
	private static float right0;
	private static float right1;
	private static float right2;
	private static float rightup0p;
	private static float rightup1p;
	private static float rightup2p;
	private static float rightup0n;
	private static float rightup1n;
	private static float rightup2n;
	//*/
	private static float rightup0p;
	private static float rightup0n;
	private static float rightup1p;
	private static float rightup1n;
	private static float rightup2p;
	private static float rightup2n;

	public Billboard(String name){
		this.name = name;
		this.file = new File("assets/billboards/"+this.name+".png");
	}
	public Billboard(String name, float xpos, float ypos, float zpos){
		this.name = name;
		this.file = new File("assets/billboards/"+this.name+".png");
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
	}

	public boolean load(){
		return Billboard.load(this);
	}

	public void render(){
		Billboard.render(this);
	}

	public void render(float xpos, float ypos, float zpos){
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		Billboard.render(this);
	}

	public static boolean load(Billboard billboard){
		try{
			if(!billboard.file.exists()){
				System.out.println("ERROR: " + billboard.file.toString() + " could not be loaded.");
				return false;
			}
			// File input
			FileInputStream textureFile = new FileInputStream(billboard.file);
			billboard.texture = TextureLoader.getTexture("PNG", textureFile, GL11.GL_NEAREST);
			textureFile.close();
			// Everything went better than expected
			return true;
		}catch(EOFException error){
			System.out.println("ERROR: EOF reached while processing" + billboard.file.toString());
			return false;
		}catch(FileNotFoundException error){
			System.out.println("ERROR: File could not be found while processing " + billboard.file.toString());
			return false;
		}catch(IOException error){
			System.out.println("ERROR: While processing " + billboard.file.toString());
			return false;
		}
	}

	public static void render(Billboard billboard){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, billboard.texture.getTextureID());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		/* Old calculation with vertex at bottom center
		// Calculate vertex mods to adjust for camera rotation
		if(billboard.applyScaling){
			// Apply scaling if needed
			right0 = TRPG.rightMod[0] * 0.5f * billboard.xscale;
			right1 = TRPG.rightMod[1] * 0.5f * billboard.xscale;
			right2 = TRPG.rightMod[2] * 0.5f * billboard.xscale;
			rightup0p = right0 + (TRPG.upMod[0] * billboard.yscale);
			rightup1p = right1 + (TRPG.upMod[1] * billboard.yscale);
			rightup2p = right2 + (TRPG.upMod[2] * billboard.yscale);
			rightup0n = -right0 + (TRPG.upMod[0] * billboard.yscale);
			rightup1n = -right1 + (TRPG.upMod[1] * billboard.yscale);
			rightup2n = -right2 + (TRPG.upMod[2] * billboard.yscale);
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
			GL11.glTexCoord2f(billboard.tL,billboard.tB);
			GL11.glVertex3f(billboard.xpos - right0,billboard.ypos - right1,billboard.zpos - right2);
			GL11.glTexCoord2f(billboard.tR,billboard.tB);
			GL11.glVertex3f(billboard.xpos + right0,billboard.ypos + right1,billboard.zpos + right2);
			GL11.glTexCoord2f(billboard.tR,billboard.tT);
			GL11.glVertex3f(billboard.xpos + rightup0p,billboard.ypos + rightup1p,billboard.zpos + rightup2p);
			GL11.glTexCoord2f(billboard.tL,billboard.tT);
			GL11.glVertex3f(billboard.xpos + rightup0n,billboard.ypos + rightup1n,billboard.zpos + rightup2n);
		//*/
		if(billboard.applyScaling){
			rightup0p = (TRPG.rightMod[0] * billboard.xscale) + (TRPG.upMod[0] * billboard.yscale);
			rightup1p = (TRPG.rightMod[1] * billboard.xscale) + (TRPG.upMod[1] * billboard.yscale);
			rightup2p = (TRPG.rightMod[2] * billboard.xscale) + (TRPG.upMod[2] * billboard.yscale);
			rightup0n = (TRPG.rightMod[0] * billboard.xscale) - (TRPG.upMod[0] * billboard.yscale);
			rightup1n = (TRPG.rightMod[1] * billboard.xscale) - (TRPG.upMod[1] * billboard.yscale);
			rightup2n = (TRPG.rightMod[2] * billboard.xscale) - (TRPG.upMod[2] * billboard.yscale);
		}else{
			rightup0p = (TRPG.rightMod[0] + TRPG.upMod[0]);
			rightup1p = (TRPG.rightMod[1] + TRPG.upMod[1]);
			rightup2p = (TRPG.rightMod[2] + TRPG.upMod[2]);
			rightup0n = (TRPG.rightMod[0] - TRPG.upMod[0]);
			rightup1n = (TRPG.rightMod[1] - TRPG.upMod[1]);
			rightup2n = (TRPG.rightMod[2] - TRPG.upMod[2]);
		}
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(billboard.tL,billboard.tB);
			GL11.glVertex3f(billboard.xpos - rightup0p,billboard.ypos - rightup1p,billboard.zpos - rightup2p);
			GL11.glTexCoord2f(billboard.tR,billboard.tB);
			GL11.glVertex3f(billboard.xpos + rightup0n,billboard.ypos + rightup1n,billboard.zpos + rightup2n);
			GL11.glTexCoord2f(billboard.tR,billboard.tT);
			GL11.glVertex3f(billboard.xpos + rightup0p,billboard.ypos + rightup1p,billboard.zpos + rightup2p);
			GL11.glTexCoord2f(billboard.tL,billboard.tT);
			GL11.glVertex3f(billboard.xpos - rightup0n,billboard.ypos - rightup1n,billboard.zpos - rightup2n);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}