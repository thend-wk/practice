package com.thend.home.sweethome.captcha.text.producer;

public class ChineseTextProducer
implements TextProducer
{
static final int DEFAULT_LENGTH = 2;
private final TextProducer _txtProd;

public ChineseTextProducer()
{
  this(2);
}

public ChineseTextProducer(int length)
{
  this._txtProd = new DefaultTextProducer(length, 2);
}

public String getText() {
  return this._txtProd.getText();
}
}
