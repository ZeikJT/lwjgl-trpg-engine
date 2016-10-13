package TRPG;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class PointSprite extends Sprite {
  @override
  protected void renderInternal() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture.getTextureID());
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL20.GL_POINT_SPRITE);
    GL11.glTexEnvi(GL20.GL_POINT_SPRITE, GL20.GL_COORD_REPLACE, GL11.GL_TRUE);
    if (this.applyScaling) {
      GL11.glPointSize(this.scale);
    } else {
      GL11.glPointSize(100f);
    }
    GL11.glBegin(GL11.GL_POINTS);
      GL11.glVertex3f(this.xpos, this.ypos, this.zpos);
    GL11.glEnd();
    GL11.glDisable(GL20.GL_POINT_SPRITE);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  }
}
