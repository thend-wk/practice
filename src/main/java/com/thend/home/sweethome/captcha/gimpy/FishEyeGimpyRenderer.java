package com.thend.home.sweethome.captcha.gimpy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FishEyeGimpyRenderer
  implements GimpyRenderer
{
  private final Color _hColor;
  private final Color _vColor;

  public FishEyeGimpyRenderer()
  {
    this(Color.BLACK, Color.BLACK);
  }

  public FishEyeGimpyRenderer(Color hColor, Color vColor) {
    this._hColor = hColor;
    this._vColor = vColor;
  }

  public void gimp(BufferedImage image) {
    int height = image.getHeight();
    int width = image.getWidth();

    int hstripes = height / 7;
    int vstripes = width / 7;

    int hspace = height / (hstripes + 1);
    int vspace = width / (vstripes + 1);

    Graphics2D graph = (Graphics2D)image.getGraphics();

    for (int i = hspace; i < height; i += hspace) {
      graph.setColor(this._hColor);
      graph.drawLine(0, i, width, i);
    }

    for (int i = vspace; i < width; i += vspace) {
      graph.setColor(this._vColor);
      graph.drawLine(i, 0, i, height);
    }

    int[] pix = new int[height * width];
    int j = 0;

    for (int j1 = 0; j1 < width; ++j1) {
      for (int k1 = 0; k1 < height; ++k1) {
        pix[j] = image.getRGB(j1, k1);
        ++j;
      }
    }

    double distance = ranInt(width / 4, width / 3);

    int wMid = image.getWidth() / 2;
    int hMid = image.getHeight() / 2;

    for (int x = 0; x < image.getWidth(); ++x) {
      for (int y = 0; y < image.getHeight(); ++y)
      {
        int relX = x - wMid;
        int relY = y - hMid;

        double d1 = Math.sqrt(relX * relX + relY * relY);
        if (d1 >= distance)
          continue;
        int j2 = wMid + (int)(fishEyeFormula(d1 / distance) * distance / d1 * (x - wMid));

        int k2 = hMid + (int)(fishEyeFormula(d1 / distance) * distance / d1 * (y - hMid));

        image.setRGB(x, y, pix[(j2 * height + k2)]);
      }

    }

    graph.dispose();
  }

  private final int ranInt(int i, int j) {
    double d = Math.random();
    return (int)(i + (j - i + 1) * d);
  }

  private final double fishEyeFormula(double s)
  {
    if (s < 0.0D) {
      return 0.0D;
    }
    if (s > 1.0D) {
      return s;
    }

    return (-0.75D * s * s * s + 1.5D * s * s + 0.25D * s);
  }
}
