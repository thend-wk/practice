package com.thend.home.sweethome.captcha.backgrounds;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class GradiatedBackgroundProducer
  implements BackgroundProducer
{
	private static final int delta = 40;
  private Color _fromColor;
  private Color _toColor;

  public GradiatedBackgroundProducer()
  {
    this._fromColor = Color.BLUE;

    this._toColor = Color.WHITE;
  }

  public BufferedImage getBackground(int width, int height) {
    BufferedImage img = new BufferedImage(width, height, 1);

    Graphics2D g = img.createGraphics();
    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setRenderingHints(hints);
    System.out.println(_fromColor.getRed() + " " + _fromColor.getGreen() + " " + _fromColor.getBlue());
    int red = _fromColor.getRed();
    if(red == 0) {
    	red = red + delta;
    } else {
    	red = red - delta;
    }
    int green = _fromColor.getGreen();
    if(green == 0) {
    	green = green + delta;
    } else {
    	green = green - delta;
    }
    int blue = _fromColor.getBlue();
    if(blue == 0) {
    	blue = blue + delta;
    } else {
    	blue = blue - delta;
    }
    System.out.print(red + " " + green + " " + blue);
    Color colorFrom = new Color(red, green, blue);
    GradientPaint ytow = new GradientPaint(0.0F, 0.0F, colorFrom, width, height, this._toColor);

    g.setPaint(ytow);

    g.fill(new Rectangle2D.Double(0.0D, 0.0D, width, height));

    g.drawImage(img, 0, 0, null);
    g.dispose();

    return img;
  }

  public BufferedImage addBackground(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    return getBackground(width, height);
  }

  public void setFromColor(Color fromColor) {
    this._fromColor = fromColor;
  }

  public void setToColor(Color toColor) {
    this._toColor = toColor;
  }
}
