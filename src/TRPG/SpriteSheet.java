package TRPG;

import TRPG.Sprite;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.WeakHashMap;

public class SpriteSheet extends Sprite{
	private File file;
	private short maxindex;
	private float[] indexData;
	private float xtexel;
	private float ytexel;

	public SpriteSheet(String name){
		super(name);
		this.file = new File("assets/sprites/"+this.name+".sheet");
		this.load();
	}
	public SpriteSheet(String name, float xpos, float ypos, float zpos){
		super(name,xpos,ypos,zpos);
		this.file = new File("assets/sprites/"+this.name+".sheet");
		this.load();
	}

	public boolean setIndex(int i){
		if(i > maxindex){
			return false;
		}
		this.xscale = this.indexData[i*6];
		this.yscale = this.indexData[(i*6)+1];
		this.tL = this.indexData[(i*6)+2]+this.xtexel;
		this.tT = this.indexData[(i*6)+3]+this.ytexel;
		this.tR = this.indexData[(i*6)+4]+this.xtexel;
		this.tB = this.indexData[(i*6)+5]+this.ytexel;
		return true;
	}

	private void load(){
		try{
			if(!this.file.exists()){
				System.out.println("ERROR: " + this.file.toString() + " could not be loaded.");
				return;
			}
			// File input
			DataInputStream sheetFile = new DataInputStream(new BufferedInputStream(new FileInputStream(this.file), 100));
			String spr = "SPR";
			if(!spr.equals(""+sheetFile.readChar()+sheetFile.readChar()+sheetFile.readChar())){
				System.out.println(this.file.toString() + " incorrect header.");
			}
			this.maxindex = sheetFile.readShort();
			if(this.maxindex <= 0){
				System.out.println(this.file.toString() + " has no indexes.");
				return;
			}
			this.xtexel = 1f/(2f*this.texture.getImageWidth());
			this.ytexel = 1f/(2f*this.texture.getImageHeight());
			this.applyScaling = true;
			long byteSize = 8l + (this.maxindex * 24l);
			if(byteSize != this.file.length()){
				System.out.println(this.file.toString() + " is the wrong size. " + byteSize + " bytes expected.");
				return;
			}
			indexData = new float[this.maxindex * 6];
			for(int i=0;i<maxindex;i++){
				indexData[i*6] = sheetFile.readFloat();
				indexData[(i*6)+1] = sheetFile.readFloat();
				indexData[(i*6)+2] = sheetFile.readFloat();
				indexData[(i*6)+3] = sheetFile.readFloat();
				indexData[(i*6)+4] = sheetFile.readFloat();
				indexData[(i*6)+5] = sheetFile.readFloat();
			}
			this.maxindex--;
			this.setIndex(0);
			sheetFile.close();
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
		this.renderBillboard();
	}

	public void render(float xpos, float ypos, float zpos){
		this.xpos = xpos;
		this.ypos = ypos;
		this.zpos = zpos;
		this.renderBillboard();
	}
}