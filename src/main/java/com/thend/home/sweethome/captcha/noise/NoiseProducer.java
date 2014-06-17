package com.thend.home.sweethome.captcha.noise;

import java.awt.image.BufferedImage;

public abstract interface NoiseProducer
{
  public abstract void makeNoise(BufferedImage paramBufferedImage);
}
