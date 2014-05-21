package com.thend.home.sweethome.captcha.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.thend.home.sweethome.captcha.Captcha;
import com.thend.home.sweethome.captcha.backgrounds.GradiatedBackgroundProducer;
import com.thend.home.sweethome.captcha.gimpy.DropShadowGimpyRenderer;
import com.thend.home.sweethome.captcha.noise.StraightLineNoiseProducer;
import com.thend.home.sweethome.captcha.text.producer.DefaultTextProducer;
import com.thend.home.sweethome.captcha.text.renderer.ChineseWordRenderer;
import com.thend.home.sweethome.captcha.text.renderer.DefaultWordRenderer;

public final class CaptchaUtil
{
	private static int MAX_CHARACTER_NUM = 15;
	private static Color[] c_en = { Color.BLACK, Color.RED, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.CYAN };
	private static Color[] d_en = { Color.BLUE, Color.DARK_GRAY, Color.BLUE, Color.DARK_GRAY, Color.BLUE };
	private static List<Font> f_cn = new ArrayList();
	private static List<Font> f_en = new ArrayList();
	
	static {
		f_cn.add(new Font("宋体", 0, 24));
	    f_en.add(new Font("Courier", 2, 40));
	}

//  public static void writeImage(HttpServletResponse response, BufferedImage bi)
//  {
//    response.setHeader("Pragma", "No-cache");
//    response.setHeader("Cache-Control", "private,no-cache,no-store");
//    response.setDateHeader("Expires", 0L);
//    response.setContentType("image/jpeg; charset=utf-8");
//    try {
//      writeImage(response.getOutputStream(), bi);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  public static void writeImage(OutputStream os, BufferedImage bi)
  {
    try {
      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
      encoder.encode(bi);
      os.flush();
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static Captcha drawImage(int type, int num, int width, int height)
  {
    Captcha captcha = null;
    try {
      int imageWidth = width;
      int imageHeight = height;
      int characterNum = num;
      int characterType = type;

      if (2 == characterType) {
    	  if (characterNum > 2 && characterNum <= MAX_CHARACTER_NUM) {
    		  imageWidth = 41 * characterNum;
    	  } else {
    		  characterNum = 2;
    		  imageWidth = 100;
    	  }
      } else  {
          if ((characterNum > MAX_CHARACTER_NUM) || (characterNum <= 5)) {
        	  characterNum = 5;
          } else {
            imageWidth = 30 * characterNum;
          }
      }

      if (2 == characterType) {
        captcha = new Captcha.Builder(imageWidth, imageHeight, 5).addText(new DefaultTextProducer(characterNum, characterType), new ChineseWordRenderer(f_cn)).addBackground(new GradiatedBackgroundProducer()).addBorder().gimp().build();
      }
      else {
        GradiatedBackgroundProducer bgd = new GradiatedBackgroundProducer();
        bgd.setFromColor(d_en[new Random().nextInt(d_en.length)]);

        int viewTpye = new Random().nextInt(5);

        switch (viewTpye)
        {
        case 0:
          captcha = new Captcha.Builder(imageWidth, imageHeight).addText(new DefaultTextProducer(characterNum, characterType), new DefaultWordRenderer(c_en[new Random().nextInt(c_en.length)], f_en)).addBackground(bgd).gimp(new DropShadowGimpyRenderer()).addBorder().addNoise().build();

          break;
        case 1:
          captcha = new Captcha.Builder(imageWidth, imageHeight).addText(new DefaultTextProducer(characterNum, characterType), new DefaultWordRenderer(c_en[new Random().nextInt(c_en.length)], f_en)).addBackground(bgd).gimp(new DropShadowGimpyRenderer()).addBorder().build();

          break;
        case 2:
          captcha = new Captcha.Builder(imageWidth, imageHeight).addText(new DefaultTextProducer(characterNum, characterType), new DefaultWordRenderer(c_en[new Random().nextInt(c_en.length)], f_en)).addBackground(bgd).addBorder().addNoise().gimp().build();

          break;
        case 3:
          captcha = new Captcha.Builder(imageWidth, imageHeight).addText(new DefaultTextProducer(characterNum, characterType), new DefaultWordRenderer(c_en[new Random().nextInt(c_en.length)], f_en)).addBackground(bgd).addBorder().addNoise(new StraightLineNoiseProducer()).gimp().build();

          break;
        default:
          captcha = new Captcha.Builder(imageWidth, imageHeight).addText(new DefaultTextProducer(characterNum, characterType), new DefaultWordRenderer(c_en[new Random().nextInt(c_en.length)], f_en)).addBackground(bgd).gimp(new DropShadowGimpyRenderer()).addBorder().addNoise(new StraightLineNoiseProducer()).build();
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return captcha;
  }

  static
  {
    f_cn.add(new Font("宋体", 0, 24));
    f_en.add(new Font("Courier", 2, 40));
  }
}