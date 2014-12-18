package TRPG;

import TRPG.Sprite;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class SpriteSheet extends Sprite {
	private short maxindex;
	private float[] indexData;
	private float xtexel;
	private float ytexel;

	public SpriteSheet(String name) {
		this(name, 0f, 0f, 0f);
	}

	public SpriteSheet(String name, float xpos, float ypos, float zpos) {
		super(name, xpos, ypos, zpos);
		if (!this.failedLoad) {
			this.failedLoad = !this.load();
		}
	}

	public boolean setIndex(int i) {
		if (i > maxindex) {
			return false;
		}
		this.xscale = this.indexData[i * 6];
		this.yscale = this.indexData[(i * 6) + 1];
		this.tL = this.indexData[(i * 6) + 2] + this.xtexel;
		this.tT = this.indexData[(i * 6) + 3] + this.ytexel;
		this.tR = this.indexData[(i * 6) + 4] + this.xtexel;
		this.tB = this.indexData[(i * 6) + 5] + this.ytexel;
		return true;
	}

	private boolean load() {
		File file = new File("assets/sprites/" + this.name + ".sheet");
		try {
			if (!file.exists()) {
				System.out.println("ERROR: " + file.toString() + " could not be loaded.");
				return false;
			}
			// File input
			DataInputStream sheetFile = new DataInputStream(new BufferedInputStream(new FileInputStream(file), 100));
			String spr = "SPR";
			if (!spr.equals("" + sheetFile.readChar() + sheetFile.readChar() + sheetFile.readChar())) {
				System.out.println(file.toString() + " incorrect header.");
				return false;
			}
			this.maxindex = sheetFile.readShort();
			if (this.maxindex <= 0) {
				System.out.println(file.toString() + " has no indexes.");
				return false;
			}
			this.xtexel = 1f / (2f * this.texture.getImageWidth());
			this.ytexel = 1f / (2f * this.texture.getImageHeight());
			this.applyScaling = true;
			long byteSize = 8l + (this.maxindex * 24l);
			if (byteSize != file.length()) {
				System.out.println(file.toString() + " is the wrong size. " + byteSize + " bytes expected.");
				return false;
			}
			indexData = new float[this.maxindex * 6];
			for (int i = 0; i < maxindex; i += 1) {
				indexData[i * 6] = sheetFile.readFloat();
				indexData[(i * 6) + 1] = sheetFile.readFloat();
				indexData[(i * 6) + 2] = sheetFile.readFloat();
				indexData[(i * 6) + 3] = sheetFile.readFloat();
				indexData[(i * 6) + 4] = sheetFile.readFloat();
				indexData[(i * 6) + 5] = sheetFile.readFloat();
			}
			this.maxindex--;
			this.setIndex(0);
			sheetFile.close();
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
}
