package TRPG;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class BillboardSprite extends Sprite {
  public BillboardSprite(String name) {
    super(name);
  }

  @Override
  protected void renderInternal() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture.getTextureID());
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    /* Old calculation with vertex at bottom center
    // Calculate vertex mods to adjust for camera rotation
    if (this.applyScaling) {
      // Apply scaling if needed
      right0 = Main.rightMod[0] * 0.5f * this.xscale;
      right1 = Main.rightMod[1] * 0.5f * this.xscale;
      right2 = Main.rightMod[2] * 0.5f * this.xscale;
      rightup0p = right0 + (Main.upMod[0] * this.yscale);
      rightup1p = right1 + (Main.upMod[1] * this.yscale);
      rightup2p = right2 + (Main.upMod[2] * this.yscale);
      rightup0n = -right0 + (Main.upMod[0] * this.yscale);
      rightup1n = -right1 + (Main.upMod[1] * this.yscale);
      rightup2n = -right2 + (Main.upMod[2] * this.yscale);
    } else {
      right0 = Main.rightMod[0] * 0.5f;
      right1 = Main.rightMod[1] * 0.5f;
      right2 = Main.rightMod[2] * 0.5f;
      rightup0p = right0 + Main.upMod[0];
      rightup1p = right1 + Main.upMod[1];
      rightup2p = right2 + Main.upMod[2];
      rightup0n = -right0 + Main.upMod[0];
      rightup1n = -right1 + Main.upMod[1];
      rightup2n = -right2 + Main.upMod[2];
    }
    GL11.glTexCoord2f(this.tL, this.tB);
    GL11.glVertex3f(this.xpos - right0, this.ypos - right1, this.zpos - right2);
    GL11.glTexCoord2f(this.tR, this.tB);
    GL11.glVertex3f(this.xpos + right0, this.ypos + right1, this.zpos + right2);
    GL11.glTexCoord2f(this.tR, this.tT);
    GL11.glVertex3f(this.xpos + rightup0p, this.ypos + rightup1p, this.zpos + rightup2p);
    GL11.glTexCoord2f(this.tL, this.tT);
    GL11.glVertex3f(this.xpos + rightup0n, this.ypos + rightup1n, this.zpos + rightup2n);
    //*/
    if (this.applyScaling) {
      rightup0p = (Main.rightMod[0] * this.xscale) + (Main.upMod[0] * this.yscale);
      rightup1p = (Main.rightMod[1] * this.xscale) + (Main.upMod[1] * this.yscale);
      rightup2p = (Main.rightMod[2] * this.xscale) + (Main.upMod[2] * this.yscale);
      rightup0n = (Main.rightMod[0] * this.xscale) - (Main.upMod[0] * this.yscale);
      rightup1n = (Main.rightMod[1] * this.xscale) - (Main.upMod[1] * this.yscale);
      rightup2n = (Main.rightMod[2] * this.xscale) - (Main.upMod[2] * this.yscale);
    } else {
      rightup0p = (Main.rightMod[0] + Main.upMod[0]);
      rightup1p = (Main.rightMod[1] + Main.upMod[1]);
      rightup2p = (Main.rightMod[2] + Main.upMod[2]);
      rightup0n = (Main.rightMod[0] - Main.upMod[0]);
      rightup1n = (Main.rightMod[1] - Main.upMod[1]);
      rightup2n = (Main.rightMod[2] - Main.upMod[2]);
    }
    GL11.glBegin(GL11.GL_QUADS);
      GL11.glTexCoord2f(this.tL, this.tB);
      GL11.glVertex3f(this.xpos - rightup0p, this.ypos - rightup1p, this.zpos - rightup2p);
      GL11.glTexCoord2f(this.tR, this.tB);
      GL11.glVertex3f(this.xpos + rightup0n, this.ypos + rightup1n, this.zpos + rightup2n);
      GL11.glTexCoord2f(this.tR, this.tT);
      GL11.glVertex3f(this.xpos + rightup0p, this.ypos + rightup1p, this.zpos + rightup2p);
      GL11.glTexCoord2f(this.tL, this.tT);
      GL11.glVertex3f(this.xpos - rightup0n, this.ypos - rightup1n, this.zpos - rightup2n);
    GL11.glEnd();
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  }
}
