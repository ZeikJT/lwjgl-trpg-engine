package TRPG;

import TRPG.Billboard;
import TRPG.Model;
import TRPG.Sprite;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TRPG{
	private static float pos[] = new float[] {0f,0f,0f};
	private static int moveCoolDown = 0;
	private static double vang = 45d;
	private static double tang = 45d;
	private static double dang = 0d;
	public static int camDir = 0;
	private static float vpos[] = new float[] {0f,0f,0f};
	private static float dpos = 0f;
	private static double degToRad = Math.PI/180d;
	private static boolean aDown = false;
	private static boolean dDown = false;
	private static boolean wDown = false;
	private static boolean sDown = false;
	private static boolean qDown = false;
	private static boolean eDown = false;
	private static boolean axes = false;
	public static boolean lights = false;
	private static Random rand = new Random();
	private static Model box;
	private static Model grass;
	private static Sprite arrow;
	private static Billboard laharl;
	private static float[] boxPos;
	public static float[] rightMod;
	public static float[] upMod;

	private static float[][] heightMap = new float[40][40];

	// Ascii table to ensure consistency accross platforms
	public static final char[] ASCII = {
		' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
		'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
		'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
		'`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm', 'm', 'o',
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'
	};

	public void start() {
		// init Display window
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.setTitle("3D Test");
			Display.setVSyncEnabled(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Initialize static right and up vector variables
		FloatBuffer mmBuffer = BufferUtils.createFloatBuffer(16);
		rightMod = new float[3];
		upMod = new float[3];

		// Create random landscape
		for(int i=0; i<40; i++){
			for(int j=0; j<40; j++){
				heightMap[i][j] = rand.nextFloat();
			}
		}
		// Random box locations
		boxPos = new float[(rand.nextInt(11)+15)*3];
		int i3 = 0;
		for(int i=0; i<boxPos.length/3; i++){
			i3 = i*3;
			boxPos[i3] = rand.nextInt(40);
			boxPos[i3+2] = rand.nextInt(40);
			boxPos[i3+1] = heightMap[(int)boxPos[i3]][(int)boxPos[i3+2]];
			boxPos[i3] -= 20;
			boxPos[i3+2] -= 20;
		}
		// Load Model test
		box = new Model("Box");
		box.load();
		box.xscale = 0.75f;
		box.yscale = 0.75f;
		box.zscale = 0.75f;
		box.applyScaling = true;
		grass = new Model("Grass");
		grass.load();
		// Load Sprite
		arrow = new Sprite("Arrow");
		arrow.load();
		arrow.scale = 25f;
		arrow.applyScaling = true;
		// Load Billboard
		laharl = new Billboard("Laharl");
		laharl.load();
		laharl.tR = 0.5f;
		laharl.xscale = 0.5f;
		laharl.applyScaling = true;

		// init OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		//GL11.glOrtho(1, 800, 800, 0, 800, -800);
		//GLU.gluLookAt(0.5774f,0.5774f,0.5774f,0f,0f,0f,0f,1f,0f);
		GL11.glOrtho(-13f, 13f, -10f, 10f, -30f, 100f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		//GL11.glScalef( 1f, 1f, -1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// Render in visual order
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		// Light
		float[] floatArray = new float[] {1f, 1f, 1f, 1f};
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4).put(floatArray);
		floatBuffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, floatBuffer);
		floatBuffer.clear();
		floatBuffer.put(floatArray);
		floatBuffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, floatBuffer);
		floatBuffer.clear();
		floatBuffer.put(floatArray);
		floatBuffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, floatBuffer);
		GL11.glEnable(GL11.GL_LIGHT0);
		while (!Display.isCloseRequested()) {
			// Clear the screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

			// Check for lights, disable if necessary
			if(lights){
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			// Draw Background Quad
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(0f,0f,0f);
			GL11.glNormal3f(0f,1f,0f);
			GL11.glVertex3f(-13f,10f,0f);
			GL11.glNormal3f(0f,1f,0f);
			GL11.glVertex3f(13f,10f,0f);
			GL11.glColor3f(0.5f,1f,0.5f);
			GL11.glNormal3f(0f,1f,0f);
			GL11.glVertex3f(13f,-10f,0f);
			GL11.glNormal3f(0f,1f,0f);
			GL11.glVertex3f(-13f,-10f,0f);
			GL11.glEnd();
			// Restore lights if necessary
			if(lights){
				GL11.glEnable(GL11.GL_LIGHTING);
			}

			// Reset depth bit
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			// set the color of the quad (R,G,B) - A assumed to be 1f
			GL11.glColor3f(1f,1f,1f);

			pollInput();
			//* Move one step per button press, relative to facing direction
			if(moveCoolDown > 0){
				moveCoolDown -= 1;
			}else{
				if(wDown && !sDown){
					if(tang == 45d || tang == 405d){
						pos[2] -= 1f;
					}else if(tang == 135d){
						pos[0] += 1f;
					}else if(tang == 225d){
						pos[2] += 1f;
					}else{
						pos[0] -= 1f;
					}
					moveCoolDown = 10;
				}else if(sDown && !wDown){
					if(tang == 45d || tang == 405d){
						pos[2] += 1f;
					}else if(tang == 135d){
						pos[0] -= 1f;
					}else if(tang == 225d){
						pos[2] -= 1f;
					}else{
						pos[0] += 1f;
					}
					moveCoolDown = 10;
				}
				if(aDown && !dDown){
					if(tang == 45d || tang == 405d){
						pos[0] -= 1f;
					}else if(tang == 135d){
						pos[2] -= 1f;
					}else if(tang == 225d){
						pos[0] += 1f;
					}else{
						pos[2] += 1f;
					}
					moveCoolDown = 10;
				}else if(dDown && !aDown){
					if(tang == 45d || tang == 405d){
						pos[0] += 1f;
					}else if(tang == 135d){
						pos[2] += 1f;
					}else if(tang == 225d){
						pos[0] -= 1f;
					}else{
						pos[2] -= 1f;
					}
					moveCoolDown = 10;
				}
			}

			//* Camera XZ position
			dpos = pos[0] - vpos[0];
			if(dpos >= 0.1f){
				vpos[0] += 0.1f;
			}else if(dpos <= -0.1f){
				vpos[0] -= 0.1f;
			}else{
				vpos[0] = pos[0];
			}
			dpos = pos[2] - vpos[2];
			if(dpos >= 0.1f){
				vpos[2] += 0.1f;
			}else if(dpos <= -0.1f){
				vpos[2] -= 0.1f;
			}else{
				vpos[2] = pos[2];
			}//*/
			//* Camera Y position
			if(pos[0] >= -20f && pos[0] < 20f && pos[2] >= -20f && pos[2] < 20f){
				dpos = heightMap[(int)pos[0]+20][(int)pos[2]+20] - vpos[1];
				if(dpos >= 0.1){
					vpos[1] += 0.1f;
				}else if(dpos <= -0.1f){
					vpos[1] -= 0.1f;
				}else{
					vpos[1] = heightMap[(int)pos[0]+20][(int)pos[2]+20];
				}
			}else{
				dpos = 0f - vpos[1];
				if(dpos >= 0.1){
					vpos[1] += 0.1f;
				}else if(dpos <= -0.1f){
					vpos[1] -= 0.1f;
				}else{
					vpos[1] = 0f;
				}
			}//*/
			// Camera rotation
			dang = tang-vang;
			if(dang != 0d){
				if(Math.abs(dang) > 0.5d){
					dang /= 6d;
				}
				vang += dang;
				// Set camera facing direction
				if(vang > 90d && vang <= 180d){
					camDir = 1;
				}else if(vang > 180d && vang <= 270d){
					camDir = 2;
				}else if((vang > 270d && vang <= 360) || (vang > -90d && vang <= 0d)){
					camDir = 3;
				}else{
					camDir = 0;
				}
			}else{
				if(vang < 0d){
					vang += 360d;
					tang = vang;
				}else if(vang >= 360d){
					vang -= 360d;
					tang = vang;
				}
				if(qDown && !eDown){
					tang += 90d;
				}else if(eDown && !qDown){
					tang -= 90d;
				}
			}
			// Update rotation and position matrix
			GL11.glPushMatrix();
			GL11.glRotatef(30f, 1f, 0f, 0f);
			GL11.glRotatef((float)vang, 0f, 1f, 0f);
			GL11.glTranslatef(-vpos[0], -vpos[1], -vpos[2]);
			// Update up and right vectors
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX , mmBuffer);
			rightMod[0] = mmBuffer.get(0);
			upMod[0] = mmBuffer.get(1);
			rightMod[1] = mmBuffer.get(4);
			upMod[1] = mmBuffer.get(5);
			rightMod[2] = mmBuffer.get(8);
			upMod[2] = mmBuffer.get(9);
			mmBuffer.clear();

			//* Refresh light
			floatArray[0] = 0f;
			floatArray[1] = 15f;
			floatArray[2] = 0f;
			floatArray[3] = 1f;
			floatBuffer.clear();
			floatBuffer.put(floatArray);
			floatBuffer.flip();
			GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, floatBuffer);
			//*/

			//* Draw Landscape
			grass.applyScaling = true;
			for(int j=0; j<40; j++){
				for(int i=0; i<40; i++){
					grass.xpos = j-20;
					grass.zpos = i-20;
					grass.yscale = heightMap[j][i];
					grass.render();
				}
			}
			//*/

			// Render box at it's random coords
			for(int i=0; i<boxPos.length/3; i++){
				box.render(boxPos[i*3], boxPos[(i*3)+1], boxPos[(i*3)+2]);
				// Render arrows pointing at boxes;
				arrow.render(boxPos[i*3], boxPos[(i*3)+1]+0.4f, boxPos[(i*3)+2]);
			}

			// Update positions then render loaded Box model
			if(pos[0] >= -20f && pos[0] < 20f && pos[2] >= -20f && pos[2] < 20f){
				//box.xpos = pos[0];
				//box.ypos = heightMap[(int)pos[0]+20][(int)pos[2]+20];
				//box.zpos = pos[2];
				laharl.xpos = pos[0];
				laharl.ypos = heightMap[(int)pos[0]+20][(int)pos[2]+20];
				laharl.zpos = pos[2];
			}else{
				//box.xpos = pos[0];
				//box.ypos = 0f;
				//box.zpos = pos[2];
				laharl.xpos = pos[0];
				laharl.ypos = 0f;
				laharl.zpos = pos[2];
			}
			//box.render()

			// Sprite Test
			// Adjust for center of image
			laharl.ypos += 1f;
			laharl.render();
			//laharl.render(0f,heightMap[20][20],0f);

			// Axes
			if(axes){
				// Reset depth bit
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				// Check for lights, disable if necessary
				if(lights){
					GL11.glDisable(GL11.GL_LIGHTING);
				}
				GL11.glBegin(GL11.GL_LINES);
				/* Static position
				GL11.glColor3f(1f, 0f, 0f);
				GL11.glVertex3f(0f, 0f, 0f);
				GL11.glVertex3f(2f, 0f, 0f);

				GL11.glColor3f(0f, 1f, 0f);
				GL11.glVertex3f(0f, 0f, 0f);
				GL11.glVertex3f(0f, 2f, 0f);

				GL11.glColor3f(0f, 0f, 1f);
				GL11.glVertex3f(0f, 0f, 0f);
				GL11.glVertex3f(0f, 0f, 2f);
				//*/
				//* Moves with camera
				GL11.glColor3f(1f, 0f, 0f);
				GL11.glVertex3f(vpos[0], vpos[1], vpos[2]);
				GL11.glVertex3f(1f+vpos[0], vpos[1], vpos[2]);

				GL11.glColor3f(0f, 1f, 0f);
				GL11.glVertex3f(vpos[0], vpos[1], vpos[2]);
				GL11.glVertex3f(vpos[0], 1f+vpos[1], vpos[2]);

				GL11.glColor3f(0f, 0f, 1f);
				GL11.glVertex3f(vpos[0], vpos[1], vpos[2]);
				GL11.glVertex3f(vpos[0], vpos[1], 1f+vpos[2]);
				//*/
				GL11.glEnd();
				// Restore lights if necessary
				if(lights){
					GL11.glEnable(GL11.GL_LIGHTING);
				}
			}

			// Pop matrix
			GL11.glPopMatrix();

			Display.update();
		}

		Display.destroy();
	}

	public void pollInput() {
		/* Mouse coords
		if(Mouse.isButtonDown(0)){
			int x = Mouse.getX();
			int y = Mouse.getY();
			System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
		}//*/
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			if(Keyboard.getEventKeyState()) {
				if(key == Keyboard.KEY_A){
					aDown = true;
				}else if(key == Keyboard.KEY_D){
					dDown = true;
				}else if(key == Keyboard.KEY_W){
					wDown = true;
				}else if(key == Keyboard.KEY_S){
					sDown = true;
				}else if(key == Keyboard.KEY_Q){
					qDown = true;
				}else if(key == Keyboard.KEY_E){
					eDown = true;
				}else if(key == Keyboard.KEY_SPACE){
					if(lights){
						GL11.glDisable(GL11.GL_LIGHTING);
						lights = false;
					}else{
						GL11.glEnable(GL11.GL_LIGHTING);
						lights = true;
					}
				}else if(key == Keyboard.KEY_RETURN){
					if(axes){
						axes = false;
					}else{
						axes = true;
					}
				}
			}else{
				// Handle key releases
				if(key == Keyboard.KEY_A){
					aDown = false;
				}else if(key == Keyboard.KEY_D){
					dDown = false;
				}else if(key == Keyboard.KEY_W){
					wDown = false;
				}else if(key == Keyboard.KEY_S){
					sDown = false;
				}else if(key == Keyboard.KEY_Q){
					qDown = false;
				}else if(key == Keyboard.KEY_E){
					eDown = false;
				}
			}
		}
	}

	public static void main(String[] argv) {
		// Start test
		TRPG engine = new TRPG();
		engine.start();
	}
}