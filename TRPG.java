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
	private static boolean lights = false;
	private static Random rand = new Random();
	private static Texture tex_grass;
	private static Texture tex_grassside;
	private static Texture tex_crate;
	
	private static float[][] heightMap = new float[40][40];

	// Cube data from example 2-16
   private static final float[][] vertices = {
		{0f, 0f, 0f},// 0
		{1f, 0f, 0f},//  1
		{1f, 1f, 0f},//   2
		{0f, 1f, 0f},//  3
		{0f, 0f, 1f},//  4
		{1f, 0f, 1f},//   5
		{1f, 1f, 1f},//    6
		{0f, 1f, 1f}//    7
   };
   private static final float[][] normals = {
		{0f, 0f, 1f},
		{0f, 0f, -1f},
		{1f, 0f, 0f},
		{-1f, 0f, 0f},
		{0f, 1f, 0f},
		{0f, -1f, 0f}
   };
   private static final byte[][] indices = {
		{4, 5, 6, 7},
		{0, 3, 2, 1},
		{1, 2, 6, 5},
		{0, 4, 7, 3},
		{3, 7, 6, 2},
		{0, 1, 5, 4}
   };
	private static final float[][] tex = {
		{0f, 0f},
		{1f, 0f},
		{1f, 1f},
		{0f, 1f}
	};
	private static final byte[][] texIndex = {
		{0, 1, 2, 3},
		{1, 2, 3, 0},
		{1, 2, 3, 0},
		{0, 1, 2, 3},
		{3, 0, 1, 2},
		{2, 3, 0, 1}
	};
	private static void modelCube(float size, float xpos, float ypos, float zpos){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex_crate.getTextureID());
		GL11.glBegin(GL11.GL_QUADS);
		// Draw all six sides of the cube.
		for (int i = 0; i < 6; i++) {
			// Draw all four vertices of the current side.
			for (int m = 0; m < 4; m++) {
				float[] temp = vertices[indices[i][m]];
				float[] temp2 = tex[texIndex[i][m]];
				GL11.glNormal3f(normals[i][0], normals[i][1], normals[i][2]);
				GL11.glTexCoord2f(temp2[0], temp2[1]);
				GL11.glVertex3f((temp[0] * size)+xpos, (temp[1] * size)+ypos, (temp[2] * size)+zpos);
			}
		}
		GL11.glEnd();
	}
	private static void terrainBlock(float xpos, float zpos, float height){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex_grassside.getTextureID());
		GL11.glBegin(GL11.GL_QUADS);
		// Draw all six sides of the cube.
		for (int i = 0; i < 5; i++) {
			if(height == 0f){
				i = 4;
			}
			if(i == 4){
				GL11.glEnd();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex_grass.getTextureID());
				GL11.glBegin(GL11.GL_QUADS);
			}
			// Draw all four vertices of the current side.
			for (int m = 0; m < 4; m++) {
				float[] temp = vertices[indices[i][m]];
				float[] temp2 = tex[texIndex[i][m]];
				GL11.glNormal3f(normals[i][0], normals[i][1], normals[i][2]);
				GL11.glTexCoord2f(temp2[0], temp2[1]);
				GL11.glVertex3f(temp[0] + xpos, (temp[1] * height), temp[2] + zpos);
			}
		}
		GL11.glEnd();
	}

	public void start() {
		// init Display window
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.setTitle("3D Test");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Load image
		try {
			tex_grass = TextureLoader.getTexture("PNG", new FileInputStream("assets/grass16.png"));
			tex_grassside = TextureLoader.getTexture("PNG", new FileInputStream("assets/grassside16.png"));
			tex_crate = TextureLoader.getTexture("PNG", new FileInputStream("assets/crate16.png"));
		} catch (IOException ex) {
			System.out.println("Failed to load texture");
			return;
		}

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
			// Clear texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			// Draw Quad
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor3f(0f,0f,0f);
			GL11.glNormal3f(0f,0f,0f);
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
			// set the color of the quad (R,G,B,A)
			GL11.glColor3f(1f,1f,1f);
			
			pollInput();
			/* After keys are checked, move box based on facing direction
			if(wDown){
				xpos += ((float)Math.sin(vang * degToRad))/7f;
				zpos -= ((float)Math.cos(vang * degToRad))/7f;
			}else if(sDown){
				xpos -= ((float)Math.sin(vang * degToRad))/7f;
				zpos += ((float)Math.cos(vang * degToRad))/7f;
			}
			if(aDown){
				xpos -= ((float)Math.sin((vang+90d) * degToRad))/7f;
				zpos += ((float)Math.cos((vang+90d) * degToRad))/7f;
			}else if(dDown){
				xpos += ((float)Math.sin((vang+90d) * degToRad))/7f;
				zpos -= ((float)Math.cos((vang+90d) * degToRad))/7f;
			}//*///* Move one step per button press, relative to facing direction
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
			
			//* Camera position
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
			GL11.glTranslatef(-vpos[0]-0.5f, -vpos[1]-0.5f, -vpos[2]-0.5f);
			
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
			for(int j=0; j<40; j++){
				for(int i=0; i<40; i++){
					terrainBlock(j-20,i-20,heightMap[j][i]);
				}
			}
			//*/
			/* Material
			floatArray[0] = 1f;
			floatArray[1] = 0f;
			floatArray[2] = 1f;
			floatArray[3] = 0.1f;
			floatBuffer.clear();
			floatBuffer.put(floatArray);
			floatBuffer.flip();
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, floatBuffer);//*/
			if(pos[0] >= -20f && pos[0] < 20f && pos[2] >= -20f && pos[2] < 20f){
				modelCube(1f, pos[0], heightMap[(int)pos[0]+20][(int)pos[2]+20], pos[2]);
			}else{
				modelCube(1f, pos[0], 0f, pos[2]);
			}
			
			// Axes
			if(axes){
				// Reset depth bit
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				// Clear texture
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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
				GL11.glVertex3f(vpos[0]+0.5f, vpos[1]+0.5f, vpos[2]+0.5f);
				GL11.glVertex3f(1f+vpos[0]+0.5f, vpos[1]+0.5f, vpos[2]+0.5f);
		
				GL11.glColor3f(0f, 1f, 0f);
				GL11.glVertex3f(vpos[0]+0.5f, vpos[1]+0.5f, vpos[2]+0.5f);
				GL11.glVertex3f(vpos[0]+0.5f, 1f+vpos[1]+0.5f, vpos[2]+0.5f);
	
				GL11.glColor3f(0f, 0f, 1f);
				GL11.glVertex3f(vpos[0]+0.5f, vpos[1]+0.5f, vpos[2]+0.5f);
				GL11.glVertex3f(vpos[0]+0.5f, vpos[1]+0.5f, 1f+vpos[2]+0.5f);
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
		// Create random landscape
		for(int i=0; i<40; i++){
			for(int j=0; j<40; j++){
				heightMap[i][j] = rand.nextFloat();
			}
		}
		// Start test
		TRPG engine = new TRPG();
		engine.start();
	}
}