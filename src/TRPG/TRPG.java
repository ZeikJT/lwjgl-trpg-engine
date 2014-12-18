package TRPG;

import TRPG.Model;
import TRPG.Sprite;
import TRPG.SpriteSheet;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class TRPG {
	private static float pos[] = new float[] {0f, 0f, 0f};
	private static int moveCoolDown = 0;
	private static double vang = 45d;
	private static double tang = TRPG.vang;
	private static double dang = 0d;
	public static int camDir = 0;
	private static float vpos[] = new float[] {0f, 0f, 0f};
	private static float dpos = 0f;
	private static double degToRad = Math.PI / 180d;
	private static boolean aDown = false;
	private static boolean dDown = false;
	private static boolean wDown = false;
	private static boolean sDown = false;
	private static boolean qDown = false;
	private static boolean eDown = false;
	private static boolean axes = false;
	public static boolean lights = false;
	private static Random rand = new Random();
	private static int worldWidth = rand.nextInt(40) + 10;
	private static int worldHeight = rand.nextInt(40) + 10;
	private static ArrayList<Model> models = new ArrayList<Model>();
	private static Model lastModel;
	private static ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	private static Sprite lastSprite;
	private static SpriteSheet laharl;
	public static float[] rightMod;
	public static float[] upMod;
	private static boolean camChange = true;

	private static float[][] heightMap = new float[worldWidth][worldHeight];

	// Ascii table to ensure consistency accross platforms
	public static final char[] ASCII = {
		' ', '!', '"', '#', '$', '%', '&', "'", '(', ')', '*', '+', ', ', '-', '.', '/',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
		'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
		'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
		'`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm', 'm', 'o',
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'
	};

	public void start() {
		// init Display window
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
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
		for (int i = 0; i < worldWidth; i += 1) {
			for (int j = 0; j < worldHeight; j += 1) {
				lastModel = new Model("Grass");
				lastModel.yscale = heightMap[i][j] = rand.nextFloat();
				lastModel.xpos = i;
				lastModel.zpos = j;
				lastModel.applyScaling = true;
				models.add(lastModel);
			}
		}
		// Random box locations
		for (int i = 0; i < 15; i += 1) {
			lastModel = new Model("Box");
			lastModel.xscale = 0.75f;
			lastModel.yscale = 0.75f;
			lastModel.zscale = 0.75f;
			lastModel.applyScaling = true;
			lastModel.xpos = rand.nextInt(worldWidth);
			lastModel.zpos = rand.nextInt(worldHeight);
			lastModel.ypos = heightMap[(int) lastModel.xpos][(int) lastModel.zpos];
			models.add(lastModel);
			// Load Point Sprite
			lastSprite = new Sprite("Arrow", true);
			lastSprite.xpos = lastModel.xpos;
			lastSprite.ypos = lastModel.ypos + 1.2f;
			lastSprite.zpos = lastModel.zpos;
			lastSprite.scale = 25f;
			lastSprite.applyScaling = true;
			sprites.add(lastSprite);
		}
		// Load Billboard SpriteSheet
		laharl = new SpriteSheet("Laharl");
		sprites.add(laharl);

		// init OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(-13f, 13f, -10f, 10f, -30f, 100f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glRotatef(30f, 1f, 0f, 0f);
		GL11.glPushMatrix();
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

			pollInput();
			//* Move one step per button press, relative to facing direction
			if (moveCoolDown > 0) {
				moveCoolDown -= 1;
			} else {
				if (wDown && !sDown) {
					if (camDir == 0) {
						pos[0] -= 1f;
					} else if (camDir == 1) {
						pos[2] += 1f;
					} else if (camDir == 2) {
						pos[0] += 1f;
					} else {
						pos[2] -= 1f;
					}
					moveCoolDown = 10;
					camChange = true;
				} else if (sDown && !wDown) {
					if (camDir == 0) {
						pos[0] += 1f;
					} else if (camDir == 1) {
						pos[2] -= 1f;
					} else if (camDir == 2) {
						pos[0] -= 1f;
					} else {
						pos[2] += 1f;
					}
					moveCoolDown = 10;
					camChange = true;
				}
				if (aDown && !dDown) {
					if (camDir == 0) {
						pos[2] += 1f;
					} else if (camDir == 1) {
						pos[0] += 1f;
					} else if (camDir == 2) {
						pos[2] -= 1f;
					} else {
						pos[0] -= 1f;
					}
					moveCoolDown = 10;
					camChange = true;
				} else if (dDown && !aDown) {
					if (camDir == 0) {
						pos[2] -= 1f;
					} else if (camDir == 1) {
						pos[0] -= 1f;
					} else if (camDir == 2) {
						pos[2] += 1f;
					} else {
						pos[0] += 1f;
					}
					moveCoolDown = 10;
					camChange = true;
				}
			}
			//* Camera XZ position
			dpos = pos[0] - vpos[0];
			if (dpos >= 0.1f) {
				vpos[0] += 0.1f;
				camChange = true;
			} else if (dpos <= -0.1f) {
				vpos[0] -= 0.1f;
				camChange = true;
			} else if (dpos != 0f) {
				vpos[0] = pos[0];
				camChange = true;
			}
			dpos = pos[2] - vpos[2];
			if (dpos >= 0.1f) {
				vpos[2] += 0.1f;
				camChange = true;
			} else if (dpos <= -0.1f) {
				vpos[2] -= 0.1f;
				camChange = true;
			} else if (dpos != 0f) {
				vpos[2] = pos[2];
				camChange = true;
			}
			//*/
			//* Camera Y position
			if (pos[0] >= 0f && pos[0] < 40f && pos[2] >= 0f && pos[2] < 40f) {
				dpos = heightMap[(int) pos[0]][(int) pos[2]] - vpos[1];
				if (dpos >= 0.1f) {
					vpos[1] += 0.1f;
					camChange = true;
				} else if (dpos <= -0.1f) {
					vpos[1] -= 0.1f;
					camChange = true;
				} else if (dpos != 0f) {
					vpos[1] = heightMap[(int) pos[0]][(int) pos[2]];
					camChange = true;
				}
			} else {
				dpos = 0f - vpos[1];
				if (dpos >= 0.1f) {
					vpos[1] += 0.1f;
					camChange = true;
				} else if (dpos <= -0.1f) {
					vpos[1] -= 0.1f;
					camChange = true;
				} else if (dpos != 0f) {
					vpos[1] = 0f;
					camChange = true;
				}
			}
			//*/
			// Camera rotation
			dang = tang - vang;
			if (dang != 0d) {
				if (Math.abs(dang) > 0.5d) {
					dang /= 6d;
				}
				vang += dang;
				// Set camera facing direction
				if (vang >= 0d && vang < 90d) {
					camDir = 0;
					laharl.setIndex(0);
				} else if (vang >= 90d && vang < 180d) {
					camDir = 1;
					laharl.setIndex(1);
				} else if (vang >= 180d && vang < 270d) {
					camDir = 2;
					laharl.setIndex(3);
				} else if (vang >= 270d && vang < 360d) {
					camDir = 3;
					laharl.setIndex(2);
				} else if (vang > -90d && vang < 0d) {
					camDir = 3;
					laharl.setIndex(2);
					vang += 360d;
					tang += 360d;
				} else {
					camDir = 0;
					laharl.setIndex(0);
					vang -= 360d;
					tang -= 360d;
				}
				camChange = true;
			} else {
				if (qDown && !eDown) {
					tang -= 90d;
				} else if (eDown && !qDown) {
					tang += 90d;
				}
			}
			if (camChange) {
				// Pop/Push matrix for a reset edit
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				// Update rotation and position matrix
				GL11.glRotatef(-(float) vang, 0f, 1f, 0f);
				GL11.glTranslatef(-vpos[0], -vpos[1], -vpos[2]);
				// Update up and right vectors
				GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, mmBuffer);
				rightMod[0] = mmBuffer.get(0);
				upMod[0] = mmBuffer.get(1);
				rightMod[1] = mmBuffer.get(4);
				upMod[1] = mmBuffer.get(5);
				rightMod[2] = mmBuffer.get(8);
				upMod[2] = mmBuffer.get(9);
				mmBuffer.clear();
				camChange = false;
				sortSprites();
			}

			// Draw Background Quad
			// Check for lights, disable if necessary
			if (lights) {
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glColor3f(0.5f, 1f, 0.5f);
				GL11.glVertex3f(vpos[0] - ((TRPG.rightMod[0] * 13f) + (TRPG.upMod[0] * 10f)), vpos[1] - ((TRPG.rightMod[1] * 13f) + (TRPG.upMod[1] * 10f)), vpos[2] - ((TRPG.rightMod[2] * 13f) + (TRPG.upMod[2] * 10f)));
				GL11.glVertex3f(vpos[0] + (TRPG.rightMod[0] * 13f) - (TRPG.upMod[0] * 10f), vpos[1] + (TRPG.rightMod[1] * 13f) - (TRPG.upMod[1] * 10f), vpos[2] + (TRPG.rightMod[2] * 13f) - (TRPG.upMod[2] * 10f));
				GL11.glColor3f(0f, 0f, 0f);
				GL11.glVertex3f(vpos[0] + (TRPG.rightMod[0] * 13f) + (TRPG.upMod[0] * 10f), vpos[1] + (TRPG.rightMod[1] * 13f) + (TRPG.upMod[1] * 10f), vpos[2] + (TRPG.rightMod[2] * 13f) + (TRPG.upMod[2] * 10f));
				GL11.glVertex3f(vpos[0] - ((TRPG.rightMod[0] * 13f) - (TRPG.upMod[0] * 10f)), vpos[1] - ((TRPG.rightMod[1] * 13f) - (TRPG.upMod[1] * 10f)), vpos[2] - ((TRPG.rightMod[2] * 13f) - (TRPG.upMod[2] * 10f)));
			GL11.glEnd();
			// Restore lights if necessary
			if (lights) {
				GL11.glEnable(GL11.GL_LIGHTING);
			}
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

			// set the color of the quad (R, G, B) - Alpha assumed to be 1f
			GL11.glColor3f(1f, 1f, 1f);

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

			// Update positions then render loaded Box model
			if (pos[0] >= 0f && pos[0] < 40f && pos[2] >= 0f && pos[2] < 40f) {
				laharl.xpos = pos[0];
				laharl.ypos = heightMap[(int) pos[0]][(int) pos[2]];
				laharl.zpos = pos[2];
			} else {
				laharl.xpos = pos[0];
				laharl.ypos = 0f;
				laharl.zpos = pos[2];
			}
			// Adjust for center of image
			laharl.ypos += 1f;

			// Render all Models
			for (int m = 0; m < models.size(); m += 1) {
				models.get(m).render();
			}

			// Render all sprites
			GL11.glDepthMask(false);
			for (int m = 0; m < sprites.size(); m += 1) {
				sprites.get(m).render();
			}
			GL11.glDepthMask(true);

			// Axes
			if (axes) {
				// Reset depth bit
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				// Check for lights, disable if necessary
				if (lights) {
					GL11.glDisable(GL11.GL_LIGHTING);
				}
				GL11.glBegin(GL11.GL_LINES);
					//* Moves with camera
					GL11.glColor3f(1f, 0f, 0f);
					GL11.glVertex3f(vpos[0], vpos[1], vpos[2]);
					GL11.glVertex3f(1f + vpos[0], vpos[1], vpos[2]);

					GL11.glColor3f(0f, 1f, 0f);
					GL11.glVertex3f(vpos[0], vpos[1], vpos[2]);
					GL11.glVertex3f(vpos[0], 1f + vpos[1], vpos[2]);

					GL11.glColor3f(0f, 0f, 1f);
					GL11.glVertex3f(vpos[0], vpos[1], vpos[2]);
					GL11.glVertex3f(vpos[0], vpos[1], 1f + vpos[2]);
					//*/
				GL11.glEnd();
				// Restore lights if necessary
				if (lights) {
					GL11.glEnable(GL11.GL_LIGHTING);
				}
			}

			Display.update();
		}

		Display.destroy();
	}

	public void pollInput() {
		/* Mouse coords
		if (Mouse.isButtonDown(0)) {
			int x = Mouse.getX();
			int y = Mouse.getY();
			System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
		}
		//*/
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			if (Keyboard.getEventKeyState()) {
				if (key == Keyboard.KEY_A) {
					aDown = true;
				} else if (key == Keyboard.KEY_D) {
					dDown = true;
				} else if (key == Keyboard.KEY_W) {
					wDown = true;
				} else if (key == Keyboard.KEY_S) {
					sDown = true;
				} else if (key == Keyboard.KEY_Q) {
					qDown = true;
				} else if (key == Keyboard.KEY_E) {
					eDown = true;
				} else if (key == Keyboard.KEY_SPACE) {
					if (lights) {
						GL11.glDisable(GL11.GL_LIGHTING);
						lights = false;
					} else {
						GL11.glEnable(GL11.GL_LIGHTING);
						lights = true;
					}
				} else if (key == Keyboard.KEY_RETURN) {
					if (axes) {
						axes = false;
					} else {
						axes = true;
					}
				}
			} else {
				// Handle key releases
				if (key == Keyboard.KEY_A) {
					aDown = false;
				} else if (key == Keyboard.KEY_D) {
					dDown = false;
				} else if (key == Keyboard.KEY_W) {
					wDown = false;
				} else if (key == Keyboard.KEY_S) {
					sDown = false;
				} else if (key == Keyboard.KEY_Q) {
					qDown = false;
				} else if (key == Keyboard.KEY_E) {
					eDown = false;
				}
			}
		}
	}

	public static void sortSprites() {
		// Update depth index and sort sprites
		for (int m = 0; m < sprites.size(); m += 1) {
			sprites.get(m).updateDepthIndex();
		}
		Collections.sort(sprites);
	}

	public static void main(String[] argv) {
		// Start test
		TRPG engine = new TRPG();
		engine.start();
	}
}
