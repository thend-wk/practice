package com.thend.home.sweethome.captcha.noise;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class CurvedLineNoiseProducer
  implements NoiseProducer
{
  private static final Random RAND = new SecureRandom();
  private final Color _color;
  private final float _width;

  public CurvedLineNoiseProducer()
  {
    this(Color.BLACK, 2.0F);
  }

  public CurvedLineNoiseProducer(Color color, float width) {
    this._color = color;
    this._width = width;
  }

  public void makeNoise(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    CubicCurve2D cc = new CubicCurve2D.Float(width * 0.1F, height * RAND.nextFloat(), width * 0.1F, height * RAND.nextFloat(), width * 0.25F, height * RAND.nextFloat(), width * 0.9F, height * RAND.nextFloat());

    PathIterator pi = cc.getPathIterator(null, 2.0D);
    Point2D[] tmp = new Point2D[200];
    int i = 0;

    while (!(pi.isDone())) {
      float[] coords = new float[6];
      switch (pi.currentSegment(coords))
      {
      case 0:
      case 1:
        tmp[i] = new Point2D.Float(coords[0], coords[1]);
      }
      ++i;
      pi.next();
    }

    Point2D[] pts = new Point2D[i];

    System.arraycopy(tmp, 0, pts, 0, i);

    Graphics2D graph = (Graphics2D)image.getGraphics();
    graph.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

    graph.setColor(this._color);

    for (i = 0; i < pts.length - 1; ++i) {
      if (i < 3) {
        graph.setStroke(new BasicStroke(this._width));
      }
      graph.drawLine((int)pts[i].getX(), (int)pts[i].getY(), (int)pts[(i + 1)].getX(), (int)pts[(i + 1)].getY());
    }

    graph.dispose();
  }
}