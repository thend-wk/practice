package com.thend.home.sweethome.captcha.gimpy;

import java.awt.image.BufferedImage;

import com.jhlabs.image.BlockFilter;
import com.thend.home.sweethome.captcha.util.ImageUtil;

public class BlockGimpyRenderer
  implements GimpyRenderer
{
  private static final int DEF_BLOCK_SIZE = 3;
  private final int _blockSize;

  public BlockGimpyRenderer()
  {
    this(3);
  }

  public BlockGimpyRenderer(int blockSize) {
    this._blockSize = blockSize;
  }

  public void gimp(BufferedImage image) {
    BlockFilter filter = new BlockFilter();
    filter.setBlockSize(this._blockSize);
    ImageUtil.applyFilter(image, filter);
  }
}
