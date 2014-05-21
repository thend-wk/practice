package com.thend.home.sweethome.captcha.gimpy;

import java.awt.image.BufferedImage;

import com.jhlabs.image.RippleFilter;
import com.thend.home.sweethome.captcha.util.ImageUtil;

public class RippleGimpyRenderer
  implements GimpyRenderer
{
  public void gimp(BufferedImage image)
  {
    RippleFilter filter = new RippleFilter();
    filter.setWaveType(16);
    filter.setXAmplitude(2.599999904632568D);
    filter.setYAmplitude(1.700000047683716D);
    filter.setXWavelength(15.0D);
    filter.setYWavelength(5.0D);

    filter.setEdgeAction(1);

    ImageUtil.applyFilter(image, filter);
  }
}