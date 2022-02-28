package TRPG;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class SpriteSheet extends BillboardSprite {
	private short maxindex;
	private float[] indexData;
	private float xtexel;
	private float ytexel;

	public SpriteSheet(String name) {
		super(name);
	}

	public boolean setIndex(int i) {
		if (i >= maxindex) {
			return false;
		}
		int offset = i * 6;
		this.xscale = this.indexData[offset];
		this.yscale = this.indexData[offset + 1];
		this.tL = this.indexData[offset + 2] + this.xtexel;
		this.tT = this.indexData[offset + 3] + this.ytexel;
		this.tR = this.indexData[offset + 4] + this.xtexel;
		this.tB = this.indexData[offset + 5] + this.ytexel;
		return true;
	}

	@Override
	protected boolean load() {
		if (!super.load()) {
			return false;
		}
		String filepath = "assets/sprites/" + this.name + ".sheet";
		try {
			File file = new File(filepath);
			if (!file.exists()) {
				System.out.println("ERROR: " + file.toString() + " could not be loaded.");
				return false;
			}
			// File input
			DataInputStream sheetFile = new DataInputStream(new BufferedInputStream(new FileInputStream(file), 100));
			if (!"SPR".equals("" + sheetFile.readChar() + sheetFile.readChar() + sheetFile.readChar())) {
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
				int offset = i * 6;
				indexData[offset] = sheetFile.readFloat();
				indexData[offset + 1] = sheetFile.readFloat();
				indexData[offset + 2] = sheetFile.readFloat();
				indexData[offset + 3] = sheetFile.readFloat();
				indexData[offset + 4] = sheetFile.readFloat();
				indexData[offset + 5] = sheetFile.readFloat();
			}
			this.maxindex--;
			this.setIndex(0);
			sheetFile.close();
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
}
