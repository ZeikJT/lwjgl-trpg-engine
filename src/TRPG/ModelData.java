package TRPG;

import TRPG.Main;
import TRPG.Model;

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

public class ModelData {
	private String name;
	private File file;
	private boolean failedLoad = false;
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
	// Rendering variables, to be reused
	private static long offset;
	private static int piece;
	private static int vertsToGo;
	// QuadCount * Verticies
	private static int voff;
	private static int coff;
	private static int toff;
	// Calculated vertex positions
	private static float xpos;
	private static float ypos;
	private static float zpos;

	public ModelData(String name) {
		this.name = name;
		this.failedLoad = !this.load();
	}

	public boolean load() {
		File file = new File("assets/models/" + this.name + "/model.obj");
		try {
			if (!file.exists()) {
				System.out.println("ERROR: " + file.toString() + " could not be found.");
				return false;
			}
			//System.out.println("Loading Model: " + file.toString());
			DataInputStream dbFile = new DataInputStream(new BufferedInputStream(new FileInputStream(file), 100));
			String obj = "OBJ";
			if (!obj.equals("" + dbFile.readChar() + dbFile.readChar() + dbFile.readChar())) {
				System.out.println(file.toString() + " incorrect header.");
				return false;
			}
			this.hasNormal = dbFile.readBoolean();
			this.hasColor = dbFile.readBoolean();
			this.pieceCount = dbFile.readByte();
			if (this.hasTexture = dbFile.readBoolean()) {
				// Allocate textures
				this.textures = new Texture[this.pieceCount];
			}
			// Allocate quadCount array
			this.quadCount = new int[this.pieceCount];
			// Variable to make sure file is long enough to load all the quads
			// Header is 10 bytes up until now
			long byteSize = 10l;
			// Allocate variable for texture file String array
			StringBuilder textureName;
			// Load textures names
			short charCount;
			// File input
			FileInputStream textureFile;
			this.totalQuadCount = 0;
			// Time to load texture names and quads for each piece
			for (int p = 0; p < this.pieceCount; p += 1) {
				if (this.hasTexture) {
					textureName = new StringBuilder();
					// Fetch string length
					charCount = (short) dbFile.readByte();
					byteSize += 3l + charCount;
					// Fetch characters
					while (charCount > 0) {
						textureName.append(Main.ASCII[dbFile.readByte()]);
						charCount -= 1;
					}
					//System.out.println(" Loading Model Texture: assets\\models\\" + this.name + "\\" + textureName + ".png");
					// Load the texture into memory
					textureFile = new FileInputStream("assets/models/" + this.name + "/" + textureName.toString() + ".png");
					this.textures[p] = TextureLoader.getTexture("PNG", textureFile);
					textureFile.close();
				}
				this.quadCount[p] = dbFile.readUnsignedShort();
				this.totalQuadCount += this.quadCount[p];
			}
			// Finish calculating file size
			byteSize += this.totalQuadCount * 48l;
			if (this.hasNormal) {
				byteSize += this.totalQuadCount * 48l;
			}
			if (this.hasColor) {
				byteSize += this.totalQuadCount * 16l;
			}
			if (this.hasTexture) {
				byteSize += this.totalQuadCount * 32l;
			}
			// Check for correct file size
			if (byteSize != file.length()) {
				System.out.println(file.toString() + " is the wrong size. " + byteSize + " bytes expected.");
				return false;
			}
			// Allocate array space
			if (this.hasNormal) {
				this.quadNormal = new float[this.totalQuadCount * 12];
			}
			if (this.hasColor) {
				this.quadColor = new byte[this.totalQuadCount * 16];
			}
			if (this.hasTexture) {
				this.quadTexCoord = new float[this.totalQuadCount * 32];
			}
			this.quadData = new float[this.totalQuadCount * 12];
			// Load quads from file
			int voff = 0;
			int coff = 0;
			int toff = 0;
			for (int q = 0; q < (this.totalQuadCount * 4); q += 1) {
				voff = q * 3;
				if (this.hasNormal) {
					this.quadNormal[voff] = dbFile.readFloat();
					this.quadNormal[voff + 1] = dbFile.readFloat();
					this.quadNormal[voff + 2] = dbFile.readFloat();
				}
				if (this.hasColor) {
					coff = q * 4;
					this.quadColor[coff] = dbFile.readByte();
					this.quadColor[coff + 1] = dbFile.readByte();
					this.quadColor[coff + 2] = dbFile.readByte();
					this.quadColor[coff + 3] = dbFile.readByte();
				}
				if (this.hasTexture) {
					toff = q * 2;
					this.quadTexCoord[toff] = dbFile.readFloat();
					this.quadTexCoord[toff + 1] = dbFile.readFloat();
				}
				this.quadData[voff] = dbFile.readFloat();
				this.quadData[voff + 1] = dbFile.readFloat();
				this.quadData[voff + 2] = dbFile.readFloat();
			}
			// Release file
			dbFile.close();
		} catch (EOFException error) {
			System.out.println("ERROR: EOF reached while processing" + file.toString());
			return false;
		} catch (FileNotFoundException error) {
			System.out.println("ERROR: File could not be found while processing " + file.toString());
			return false;
		} catch (IOException error) {
			System.out.println("ERROR: While processing " + file.toString());
			return false;
		}

		// Everything went better than expected
		return true;
	}

	public void render(Model model) {
		// Don't render if failed to load
		if (this.failedLoad) {
			return;
		}
		if (Main.lights && !this.hasTexture) {
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		ModelData.offset = 0l;
		ModelData.piece = 0;
		ModelData.vertsToGo = this.quadCount[ModelData.piece] * 4;
		// QuadCount * Verticies
		ModelData.voff = 0;
		ModelData.coff = 0;
		ModelData.toff = 0;
		// Bind texture if needed
		if (this.hasTexture) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textures[ModelData.piece].getTextureID());
		}
		GL11.glBegin(GL11.GL_QUADS);
			for (int q = 0; q < (this.totalQuadCount * 4); q += 1) {
				if (ModelData.vertsToGo == 0) {
					ModelData.piece += 1;
					ModelData.vertsToGo = this.quadCount[ModelData.piece] * 4;
					// Re-Bind texture if needed
					if (this.hasTexture) {
						GL11.glEnd();
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textures[ModelData.piece].getTextureID());
						GL11.glBegin(GL11.GL_QUADS);
					}
				}
				ModelData.vertsToGo -= 1;
				ModelData.voff = q * 3;
				if (this.hasNormal) {
					GL11.glNormal3f(this.quadNormal[ModelData.voff], this.quadNormal[ModelData.voff + 1], this.quadNormal[ModelData.voff + 2]);
				}
				if (this.hasColor) {
					ModelData.coff = q * 4;
					GL11.glColor4b(this.quadColor[ModelData.coff], this.quadColor[ModelData.coff + 1], this.quadColor[ModelData.coff + 2], this.quadColor[ModelData.coff + 3]);
				}
				if (this.hasTexture) {
					ModelData.toff = q * 2;
					GL11.glTexCoord2f(this.quadTexCoord[ModelData.toff], this.quadTexCoord[ModelData.toff + 1]);
				}
				if (model.applyScaling) {
					ModelData.xpos = (this.quadData[ModelData.voff] * model.xscale) + model.xpos;
					ModelData.ypos = (this.quadData[ModelData.voff + 1] * model.yscale) + model.ypos;
					ModelData.zpos = (this.quadData[ModelData.voff + 2] * model.zscale) + model.zpos;
				} else {
					ModelData.xpos = this.quadData[ModelData.voff] + model.xpos;
					ModelData.ypos = this.quadData[ModelData.voff + 1] + model.ypos;
					ModelData.zpos = this.quadData[ModelData.voff + 2] + model.zpos;
				}
				GL11.glVertex3f(ModelData.xpos, ModelData.ypos, ModelData.zpos);
			}
		GL11.glEnd();
		// Unbind texture if necessary
		if (this.hasTexture) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
		if (Main.lights && !this.hasTexture) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
}
