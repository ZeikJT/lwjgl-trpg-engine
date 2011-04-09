package TRPG;

import java.lang.Throwable;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.File;
import org.lwjgl.opengl.GL11;

public class Model{
	private String name;
	private File file;
	private int quadCount;
	private float[] quadData;
	private float[] quadNormal;
	private boolean hasNormal = false;
	private byte[] quadColor;
	private boolean hasColor = false;
	private float[] quadTexCoord;
	private byte textureCount;
	private boolean hasTexture = false;
	public float xpos = 0f;
	public float ypos = 0f;
	public float zpos = 0f;

	public Model(String name){
		this(name, 0f, 0f, 0f);
	}
	public Model(String name, float xpos, float ypos, float zpos){
		this.name = name;
		this.file = new File("assets/models/"+this.name+".obj");
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
	
	public boolean hasTexture(){
		return this.hasTexture;
	}

	public static boolean load(Model model){
		try{
			if(!model.file.exists()){
				System.out.println("ERROR: " + model.file.toString() + " could not be loaded.");
				return false;
			}
			DataInputStream dbFile = new DataInputStream(new BufferedInputStream(new FileInputStream(model.file), 100));
			String obj = "OBJ";
			if(obj.equals(""+dbFile.readChar()+dbFile.readChar()+dbFile.readChar())){
				dbFile.skipBytes(1);
			}else{
				System.out.println(model.file.toString() + " incorrect header.");
			}
			model.quadCount = dbFile.readUnsignedShort();
			model.hasNormal = dbFile.readBoolean();
			model.hasColor = dbFile.readBoolean();
			model.textureCount = dbFile.readByte();
			if(model.textureCount > (byte)0){
				model.hasTexture = true;
			}
			// Make sure file is long enough to load all the quads
			// Header is 5bytes for now
			long byteSize = 12l;
			/* Unknown implementation yet
			if(model.hasTexture){
			}//*/
			if(model.hasNormal){
				byteSize += model.quadCount*48l;
			}
			if(model.hasColor){
				byteSize += model.quadCount*16l;
			}
			byteSize += model.quadCount*48l;
			if(byteSize > model.file.length()){
				System.out.println(model.file.toString() + " file is too short. " + byteSize + " bytes expected.");
			}
			// Allocate array space
			if(model.hasNormal){
				model.quadNormal = new float[model.quadCount*12];
			}
			if(model.hasColor){
				model.quadColor = new byte[model.quadCount*16];
			}
			model.quadData = new float[model.quadCount*12];
			// Load quads from file
			int voff = 0;
			int coff = 0;
			for(int q=0; q<model.quadCount*4; q++){
				voff = q*3;
				coff = q*4;
				if(model.hasNormal){
					model.quadNormal[q*3] = dbFile.readFloat();
					model.quadNormal[(q*3)+1] = dbFile.readFloat();
					model.quadNormal[(q*3)+2] = dbFile.readFloat();
				}
				if(model.hasColor){
					model.quadColor[q*4] = dbFile.readByte();
					model.quadColor[(q*4)+1] = dbFile.readByte();
					model.quadColor[(q*4)+2] = dbFile.readByte();
					model.quadColor[(q*4)+3] = dbFile.readByte();
				}
				model.quadData[q*3] = dbFile.readFloat();
				model.quadData[(q*3)+1] = dbFile.readFloat();
				model.quadData[(q*3)+2] = dbFile.readFloat();
			}
			return true;
		}catch(EOFException error){
			System.out.println("ERROR: " + model.file.toString() + ".obj EOF reached early.");
			return false;
		}catch(Throwable error){
			System.out.println("ERROR: " + model.file.toString() + ".obj could not be read.");
			return false;
		}
	}

	public static void render(Model model){
		if(TRPG.lights && !model.hasTexture){
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		GL11.glBegin(GL11.GL_QUADS);
		// QuadCount * Verticies
		int voff = 0;
		int coff = 0;
		for(int q=0; q<model.quadCount*4; q++){
			voff = q*3;
			coff = q*4;
			if(model.hasNormal){
				GL11.glNormal3f(model.quadNormal[voff],model.quadNormal[voff+1],model.quadNormal[voff+2]);
			}
			if(model.hasColor){
				GL11.glColor4b(model.quadColor[coff],model.quadColor[coff+1],model.quadColor[coff+2],model.quadColor[coff+3]);
			}
			GL11.glVertex3f(model.quadData[voff]+model.xpos,model.quadData[voff+1]+model.ypos,model.quadData[voff+2]+model.zpos);
		}
		GL11.glEnd();
		if(TRPG.lights && !model.hasTexture){
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
}