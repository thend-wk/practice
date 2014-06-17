package com.thend.home.sweethome.captcha.text.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChineseWordRenderer
  implements WordRenderer
{
  private static final List<Font> DEFAULT_FONTS = new ArrayList();

  private static Color[] fontcolor = { Color.RED, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.MAGENTA, Color.PINK };
  private final List<Font> _fonts;

  public ChineseWordRenderer()
  {
    this(DEFAULT_FONTS);
  }

  public ChineseWordRenderer(List<Font> fonts) {
    this._fonts = ((fonts != null) ? fonts : DEFAULT_FONTS);
  }

  public void render(String word, BufferedImage image)
  {
    Graphics2D g = image.createGraphics();

    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

    g.setRenderingHints(hints);

    FontRenderContext frc = g.getFontRenderContext();
    int startPosX = 20;
    Random generator = new Random();
    int j = generator.nextInt(3);
    for (int i = 0; i < word.length(); ++i) {
      char element = word.charAt(i);
      char[] itchar = { element };
      int choiceFont = generator.nextInt(this._fonts.size());
      Font itFont = (Font)this._fonts.get(choiceFont);
      g.setFont(itFont);

      Color color = fontcolor[(j % fontcolor.length)];
      ++j;
      g.setColor(color);

      AffineTransform fontAT = new AffineTransform();
      double shx = (Math.random() - 0.5D) * 0.6D;
      fontAT.setToShear(shx, 0.0D);
      g.transform(fontAT);

      GlyphVector gv = itFont.createGlyphVector(frc, itchar);
      double charWitdth = gv.getVisualBounds().getWidth();

      g.drawChars(itchar, 0, itchar.length, startPosX, 35);
      startPosX += (int)charWitdth;
    }
  }

  static
  {
    DEFAULT_FONTS.add(new Font("宋体", 0, 40));
  }
}
