package com.thend.home.sweethome.captcha.backgrounds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public final class FlatColorBackgroundProducer
  implements BackgroundProducer
{
  private Color _color;

  public FlatColorBackgroundProducer()
  {
    this(Color.GRAY);
  }

  public FlatColorBackgroundProducer(Color color)
  {
    this._color = Color.GRAY;

    this._color = color;
  }

  public BufferedImage addBackground(BufferedImage bi) {
    int width = bi.getWidth();
    int height = bi.getHeight();
    return getBackground(width, height);
  }

  public BufferedImage getBackground(int width, int height) {
    BufferedImage img = new BufferedImage(width, height, 1);

    Graphics2D graphics = img.createGraphics();
    graphics.setPaint(this._color);
    graphics.fill(new Rectangle2D.Double(0.0D, 0.0D, width, height));
    graphics.drawImage(img, 0, 0, null);
    graphics.dispose();

    return img;
  }
}