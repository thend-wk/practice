package com.thend.home.sweethome.captcha.noise;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

public class StraightLineNoiseProducer
  implements NoiseProducer
{
  private final Color _color;
  private final int _thickness;
  private final SecureRandom _gen;

  public StraightLineNoiseProducer()
  {
    this(Color.BLACK, 2);
  }

  public StraightLineNoiseProducer(Color color, int thickness)
  {
    this._gen = new SecureRandom();

    this._color = color;
    this._thickness = thickness;
  }

  public void makeNoise(BufferedImage image) {
    Graphics2D graphics = image.createGraphics();
    int height = image.getHeight();
    int width = image.getWidth();
    int y1 = this._gen.nextInt(height) + 1;
    int y2 = this._gen.nextInt(height) + 1;
    drawLine(graphics, y1, width, y2);
  }

  private void drawLine(Graphics g, int y1, int x2, int y2) {
    int X1 = 0;

    g.setColor(this._color);
    int dX = x2 - X1;
    int dY = y2 - y1;

    double lineLength = Math.sqrt(dX * dX + dY * dY);

    double scale = this._thickness / 2.0D * lineLength;

    double ddx = -scale * dY;
    double ddy = scale * dX;
    ddx += ((ddx > 0.0D) ? 0.5D : -0.5D);
    ddy += ((ddy > 0.0D) ? 0.5D : -0.5D);
    int dx = (int)ddx;
    int dy = (int)ddy;

    int[] xPoints = new int[4];
    int[] yPoints = new int[4];

    xPoints[0] = (X1 + dx);
    yPoints[0] = (y1 + dy);
    xPoints[1] = (X1 - dx);
    yPoints[1] = (y1 - dy);
    xPoints[2] = (x2 - dx);
    yPoints[2] = (y2 - dy);
    xPoints[3] = (x2 + dx);
    yPoints[3] = (y2 + dy);

    g.fillPolygon(xPoints, yPoints, 4);
  }
}
