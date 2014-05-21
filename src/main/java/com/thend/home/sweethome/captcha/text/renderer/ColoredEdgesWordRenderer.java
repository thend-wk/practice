package com.thend.home.sweethome.captcha.text.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColoredEdgesWordRenderer
  implements WordRenderer
{
  private static final List<Color> DEFAULT_COLORS = new ArrayList();
  private static final List<Font> DEFAULT_FONTS = new ArrayList();
  private static final float DEFAULT_STROKE_WIDTH = 0.5F;
  private static final double YOFFSET = 0.25D;
  private static final double XOFFSET = 0.05D;
  private final List<Font> _fonts;
  private final List<Color> _colors;
  private final float _strokeWidth;

  public ColoredEdgesWordRenderer()
  {
    this(DEFAULT_COLORS, DEFAULT_FONTS, 0.5F);
  }

  public ColoredEdgesWordRenderer(List<Color> colors, List<Font> fonts) {
    this(colors, fonts, 0.5F);
  }

  public ColoredEdgesWordRenderer(List<Color> colors, List<Font> fonts, float strokeWidth) {
    this._colors = ((colors != null) ? colors : DEFAULT_COLORS);
    this._fonts = ((fonts != null) ? fonts : DEFAULT_FONTS);
    this._strokeWidth = ((strokeWidth < 0.0F) ? 0.5F : strokeWidth);
  }

  public void render(String word, BufferedImage image) {
    Graphics2D g = image.createGraphics();

    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

    g.setRenderingHints(hints);

    AttributedString as = new AttributedString(word);
    as.addAttribute(TextAttribute.FONT, getRandomFont());

    FontRenderContext frc = g.getFontRenderContext();
    AttributedCharacterIterator aci = as.getIterator();

    TextLayout tl = new TextLayout(aci, frc);
    int xBaseline = (int)Math.round(image.getWidth() * 0.05D);
    int yBaseline = image.getHeight() - (int)Math.round(image.getHeight() * 0.25D);
    Shape shape = tl.getOutline(AffineTransform.getTranslateInstance(xBaseline, yBaseline));

    g.setColor(getRandomColor());
    g.setStroke(new BasicStroke(this._strokeWidth));

    g.draw(shape);
  }

  private Color getRandomColor() {
    return ((Color)getRandomObject(this._colors));
  }

  private Font getRandomFont() {
    return ((Font)getRandomObject(this._fonts));
  }

  private Object getRandomObject(List<? extends Object> objs) {
    if (objs.size() == 1) {
      return objs.get(0);
    }

    Random gen = new SecureRandom();
    int i = gen.nextInt(objs.size());
    return objs.get(i);
  }

  static
  {
    DEFAULT_FONTS.add(new Font("Arial", 1, 40));
    DEFAULT_COLORS.add(Color.BLUE);
  }
}
