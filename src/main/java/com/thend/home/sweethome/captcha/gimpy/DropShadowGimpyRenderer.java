package com.thend.home.sweethome.captcha.gimpy;

import java.awt.image.BufferedImage;

import com.jhlabs.image.ShadowFilter;
import com.thend.home.sweethome.captcha.util.ImageUtil;

public class DropShadowGimpyRenderer
  implements GimpyRenderer
{
  private static final int DEFAULT_RADIUS = 3;
  private static final int DEFAULT_OPACITY = 75;
  private final int _radius;
  private final int _opacity;

  public DropShadowGimpyRenderer()
  {
    this(3, 75);
  }

  public DropShadowGimpyRenderer(int radius, int opacity) {
    this._radius = radius;
    this._opacity = opacity;
  }

  public void gimp(BufferedImage image) {
    ShadowFilter sFilter = new ShadowFilter();
    sFilter.setRadius(this._radius);
    sFilter.setOpacity(this._opacity);
    ImageUtil.applyFilter(image, sFilter);
  }
}
