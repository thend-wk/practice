package com.thend.home.sweethome.captcha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import com.thend.home.sweethome.captcha.backgrounds.BackgroundProducer;
import com.thend.home.sweethome.captcha.backgrounds.TransparentBackgroundProducer;
import com.thend.home.sweethome.captcha.gimpy.GimpyRenderer;
import com.thend.home.sweethome.captcha.gimpy.RippleGimpyRenderer;
import com.thend.home.sweethome.captcha.noise.CurvedLineNoiseProducer;
import com.thend.home.sweethome.captcha.noise.NoiseProducer;
import com.thend.home.sweethome.captcha.text.producer.DefaultTextProducer;
import com.thend.home.sweethome.captcha.text.producer.TextProducer;
import com.thend.home.sweethome.captcha.text.renderer.DefaultWordRenderer;
import com.thend.home.sweethome.captcha.text.renderer.WordRenderer;

public final class Captcha
  implements Serializable
{
  private static final long serialVersionUID = 617511236L;
  public static final String NAME = "simpleCaptcha";
  private Builder _builder;

  private Captcha(Builder builder)
  {
    this._builder = builder;
  }

  public boolean isCorrect(String answer)
  {
    return this._builder._answer.equals(answer);
  }

  public String getAnswer() {
    return this._builder._answer;
  }

  public BufferedImage getImage() {
    return this._builder._img;
  }

  public String toString()
  {
    return this._builder.toString();
  }

  public static class Builder
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    private String _answer = "";
    private BufferedImage _img;
    private BufferedImage _bg;
    private boolean _addBorder = false;

    public Builder(int width, int height) {
      this._img = new BufferedImage(width, height, 2);
    }

    public Builder(int width, int height, int bufferedImageType)
    {
      this._img = new BufferedImage(width, height, bufferedImageType);
    }

    public Builder addBackground()
    {
      return addBackground(new TransparentBackgroundProducer());
    }

    public Builder addBackground(BackgroundProducer bgProd)
    {
      this._bg = bgProd.getBackground(this._img.getWidth(), this._img.getHeight());

      return this;
    }

    public Builder addText()
    {
      return addText(new DefaultTextProducer());
    }

    public Builder addText(TextProducer txtProd)
    {
      return addText(txtProd, new DefaultWordRenderer());
    }

    public Builder addText(WordRenderer wRenderer)
    {
      return addText(new DefaultTextProducer(), wRenderer);
    }

    public Builder addText(TextProducer txtProd, WordRenderer wRenderer) {
      this._answer += txtProd.getText();
      wRenderer.render(this._answer, this._img);

      return this;
    }

    public Builder addNoise()
    {
      return addNoise(new CurvedLineNoiseProducer());
    }

    public Builder addNoise(NoiseProducer nProd)
    {
      nProd.makeNoise(this._img);
      return this;
    }

    public Builder gimp()
    {
      return gimp(new RippleGimpyRenderer());
    }

    public Builder gimp(GimpyRenderer gimpy)
    {
      gimpy.gimp(this._img);
      return this;
    }

    public Builder addBorder()
    {
      this._addBorder = true;

      return this;
    }

    public Captcha build()
    {
      if (this._bg == null) {
        this._bg = new TransparentBackgroundProducer().getBackground(this._img.getWidth(), this._img.getHeight());
      }

      Graphics2D g = this._bg.createGraphics();
      g.setComposite(AlphaComposite.getInstance(3, 1.0F));
      g.drawImage(this._img, null, null);

      if (this._addBorder) {
        int width = this._img.getWidth();
        int height = this._img.getHeight();

        g.setColor(Color.BLACK);
        g.drawLine(0, 0, 0, width);
        g.drawLine(0, 0, width, 0);
        g.drawLine(0, height - 1, width, height - 1);
        g.drawLine(width - 1, height - 1, width - 1, 0);
      }

      this._img = this._bg;

      return new Captcha(this);
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer();
      sb.append("[Answer: ");
      sb.append(this._answer);
      sb.append("][Image: ");
      sb.append(this._img);
      sb.append("]");

      return sb.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeObject(this._answer);
      ImageIO.write(this._img, "png", ImageIO.createImageOutputStream(out));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
      this._answer = ((String)in.readObject());
      this._img = ImageIO.read(ImageIO.createImageInputStream(in));
    }
  }
}