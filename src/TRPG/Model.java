package TRPG;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Model{
	private String name;
	private File file;
	private boolean hasNormal = false;
	private boolean hasColor = false;
	private boolean hasTexture = false;
	private int pieceCount;
	private int totalQuadCount;
	private int[] quadCount;
	private float[] quadNormal;
	private byte[] quadColor;
	private float[] quadTexCoord;
	private float[] quadData;
	private Texture[] textures;
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;

	public Model(String name){
		this(name, 0f, 0f, 0f);
	}
	public Model(String name, float xpos, float ypos, float zpos){
		this.name = name;
		this.file = new File("assets/models/"+this.name+"/model.obj");
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
	}

	public boolean load(){
		return Model.load(this);
	}
	
	public void render(){
		Model.render(this);
	}

	public static boolean load(Model model){
		try{
			if(!model.file.exists()){
				System.out.println("ERROR: " + model.file.toString() + " could not be loaded.");
				return false;
			}
			//System.out.println("Loading Model: " + model.file.toString());
			DataInputStream dbFile = new DataInputStream(new BufferedInputStream(new FileInputStream(model.file), 100));
			String obj = "OBJ";
			if(!obj.equals(""+dbFile.readChar()+dbFile.readChar()+dbFile.readChar())){
				System.out.println(model.file.toString() + " incorrect header.");
			}
			model.hasNormal = dbFile.readBoolean();
			model.hasColor = dbFile.readBoolean();
			model.pieceCount = dbFile.readByte();
			if(model.hasTexture = dbFile.readBoolean()){
				// Allocate textures
				model.textures = new Texture[model.pieceCount];
			}
			// Allocate quadCount array
			model.quadCount = new int[model.pieceCount];
			// Variable to make sure file is long enough to load all the quads
			// Header is 10 bytes up until now
			long byteSize = 10l;
			// Allocate variable for texture file String array
			String textureName;
			// Load textures names
			short charCount;
			// File input
			FileInputStream textureFile;
			model.totalQuadCount = 0;
			// Time to load texture names and quads for each piece
			for(int p=0; p<model.pieceCount; p++){
				if(model.hasTexture){
					textureName = "";
					// Fetch string length
					charCount = (short)dbFile.readByte();
					byteSize += 1l + charCount;
					// Fetch characters
					while(charCount>0){
						textureName += TRPG.ASCII[dbFile.readByte()-32];
						charCount -= 1;
					}
					//System.out.println(" Loading Model Texture: assets\\models\\" + model.name + "\\" + textureName + ".png");
					// Load the texture into memory
					textureFile = new FileInputStream("assets/models/" + model.name + "/" + textureName + ".png");
					model.textures[p] = TextureLoader.getTexture("PNG", textureFile);
					textureFile.close();
				}
				model.quadCount[p] = dbFile.readUnsignedShort();
				model.totalQuadCount += model.quadCount[p];
			}
			// Finish calculating file size
			byteSize += model.totalQuadCount*48l;
			if(model.hasNormal){
				byteSize += model.totalQuadCount*48l;
			}
			if(model.hasColor){
				byteSize += model.totalQuadCount*16l;
			}
			if(model.hasTexture){
				byteSize += model.totalQuadCount*32l;
			}
			// Check for correct file size
			if(byteSize == model.file.length()){
				System.out.println(model.file.toString() + " is the wrong size. " + byteSize + " bytes expected.");
			}
			// Allocate array space
			if(model.hasNormal){
				model.quadNormal = new float[model.totalQuadCount*12];
			}
			if(model.hasColor){
				model.quadColor = new byte[model.totalQuadCount*16];
			}
			if(model.hasTexture){
				model.quadTexCoord = new float[model.totalQuadCount*32];
			}
			model.quadData = new float[model.totalQuadCount*12];
			// Load quads from file
			int voff = 0;
			int coff = 0;
			int toff = 0;
			for(int q=0; q<model.totalQuadCount*4; q++){
				voff = q*3;
				if(model.hasNormal){
					model.quadNormal[voff] = dbFile.readFloat();
					model.quadNormal[(voff)+1] = dbFile.readFloat();
					model.quadNormal[(voff)+2] = dbFile.readFloat();
				}
				if(model.hasColor){
					coff = q*4;
					model.quadColor[coff] = dbFile.readByte();
					model.quadColor[(coff)+1] = dbFile.readByte();
					model.quadColor[(coff)+2] = dbFile.readByte();
					model.quadColor[(coff)+3] = dbFile.readByte();
				}
				if(model.hasTexture){
					toff = q*2;
					model.quadTexCoord[toff] = dbFile.readFloat();
					model.quadTexCoord[toff+1] = dbFile.readFloat();
				}
				model.quadData[voff] = dbFile.readFloat();
				model.quadData[(voff)+1] = dbFile.readFloat();
				model.quadData[(voff)+2] = dbFile.readFloat();
			}
			// Release file
			dbFile.close();
			// Everything went better than expected
			return true;
		}catch(EOFException error){
			System.out.println("ERROR: EOF reached while processing" + model.file.toString());
			return false;
		}catch(FileNotFoundException error){
			System.out.println("ERROR: File could not be found while processing " + model.file.toString());
			return false;
		}catch(IOException error){
			System.out.println("ERROR: While processing " + model.file.toString());
			return false;
		}
	}

	public static void render(Model model){
		if(TRPG.lights && !model.hasTexture){
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		long offset = 0l;
		int piece = 0;
		int vertsToGo = model.quadCount[piece]*4;
		// QuadCount * Verticies
		int voff = 0;
		int coff = 0;
		int toff = 0;
		// Bind texture if needed
		if(model.hasTexture){
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.textures[piece].getTextureID());
		}
		GL11.glBegin(GL11.GL_QUADS);
		for(int q=0; q<model.totalQuadCount*4; q++){
			if(vertsToGo == 0){
				piece += 1;
				vertsToGo = model.quadCount[piece]*4;
				// Re-Bind texture if needed
				if(model.hasTexture){
					GL11.glEnd();
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.textures[piece].getTextureID());
					GL11.glBegin(GL11.GL_QUADS);
				}
			}
			vertsToGo -= 1;
			voff = q*3;
			if(model.hasNormal){
				GL11.glNormal3f(model.quadNormal[voff],model.quadNormal[voff+1],model.quadNormal[voff+2]);
			}
			if(model.hasColor && (!TRPG.lights && !model.hasTexture)){
				coff = q*4;
				GL11.glColor4b(model.quadColor[coff],model.quadColor[coff+1],model.quadColor[coff+2],model.quadColor[coff+3]);
			}
			if(model.hasTexture){
				toff = q*2;
				GL11.glTexCoord2f(model.quadTexCoord[toff], model.quadTexCoord[toff+1]);
			}
			GL11.glVertex3f(model.quadData[voff]+model.xpos,model.quadData[voff+1]+model.ypos,model.quadData[voff+2]+model.zpos);
		}
		GL11.glEnd();
		// Unbind texture if necessary
		if(model.hasTexture){
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
		if(TRPG.lights && !model.hasTexture){
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
}