package com.thend.home.sweethome.captcha.backgrounds;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TransparentBackgroundProducer
  implements BackgroundProducer
{
  public BufferedImage addBackground(BufferedImage image)
  {
    return getBackground(image.getWidth(), image.getHeight());
  }

  public BufferedImage getBackground(int width, int height) {
    BufferedImage bg = new BufferedImage(width, height, 3);
    Graphics2D g = bg.createGraphics();

    g.setComposite(AlphaComposite.getInstance(1, 0.0F));
    g.fillRect(0, 0, width, height);

    return bg;
  }
}