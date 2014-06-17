package com.thend.home.sweethome.captcha.backgrounds;

import java.awt.image.BufferedImage;

public abstract interface BackgroundProducer
{
  public abstract BufferedImage addBackground(BufferedImage paramBufferedImage);

  public abstract BufferedImage getBackground(int paramInt1, int paramInt2);
}