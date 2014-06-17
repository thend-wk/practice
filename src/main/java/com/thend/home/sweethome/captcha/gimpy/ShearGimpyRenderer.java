package com.thend.home.sweethome.captcha.gimpy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class ShearGimpyRenderer
  implements GimpyRenderer
{
  private final Random _gen;
  private final Color _color;

  public ShearGimpyRenderer()
  {
    this(Color.GRAY);
  }

  public ShearGimpyRenderer(Color color)
  {
    this._gen = new SecureRandom();

    this._color = color;
  }

  public void gimp(BufferedImage bi) {
    Graphics2D g = bi.createGraphics();
    shearX(g, bi.getWidth(), bi.getHeight());
    shearY(g, bi.getWidth(), bi.getHeight());
    g.dispose();
  }

  private void shearX(Graphics2D g, int w1, int h1) {
    int period = this._gen.nextInt(10) + 5;

    boolean borderGap = true;
    int frames = 15;
    int phase = this._gen.nextInt(5) + 2;

    for (int i = 0; i < h1; ++i) {
      double d = (period >> 1) * Math.sin(i / period + 6.283185307179586D * phase / frames);

      g.copyArea(0, i, w1, 1, (int)d, 0);
      if (borderGap) {
        g.setColor(this._color);
        g.drawLine((int)d, i, 0, i);
        g.drawLine((int)d + w1, i, w1, i);
      }
    }
  }

  private void shearY(Graphics2D g, int w1, int h1) {
    int period = this._gen.nextInt(30) + 10;

    boolean borderGap = true;
    int frames = 15;
    int phase = 7;
    for (int i = 0; i < w1; ++i) {
      double d = (period >> 1) * Math.sin(i / period + 6.283185307179586D * phase / frames);

      g.copyArea(i, 0, 1, h1, 0, (int)d);
      if (borderGap) {
        g.setColor(this._color);
        g.drawLine(i, (int)d, i, 0);
        g.drawLine(i, (int)d + h1, i, h1);
      }
    }
  }
}