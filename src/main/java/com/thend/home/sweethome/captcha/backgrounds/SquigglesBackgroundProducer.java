package com.thend.home.sweethome.captcha.backgrounds;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

public class SquigglesBackgroundProducer
  implements BackgroundProducer
{
  public BufferedImage addBackground(BufferedImage image)
  {
    int width = image.getWidth();
    int height = image.getHeight();
    return getBackground(width, height);
  }

  public BufferedImage getBackground(int width, int height) {
    BufferedImage result = new BufferedImage(width, height, 1);

    Graphics2D graphics = result.createGraphics();

    BasicStroke bs = new BasicStroke(2.0F, 0, 0, 2.0F, new float[] { 2.0F, 2.0F }, 0.0F);

    graphics.setStroke(bs);
    AlphaComposite ac = AlphaComposite.getInstance(3, 0.75F);

    graphics.setComposite(ac);

    graphics.translate(width * -1.0D, 0.0D);
    double delta = 5.0D;

    double ts = 0.0D;
    for (double xt = 0.0D; xt < 2.0D * width; xt += delta) {
      Arc2D arc = new Arc2D.Double(0.0D, 0.0D, width, height, 0.0D, 360.0D, 0);

      graphics.draw(arc);
      graphics.translate(delta, 0.0D);
      ts += delta;
    }
    graphics.dispose();
    return result;
  }
}
