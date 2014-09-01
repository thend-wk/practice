package com.thend.home.sweethome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import com.thend.home.sweethome.blowfish.BlowFish;
import com.thend.home.sweethome.captcha.Captcha;
import com.thend.home.sweethome.captcha.util.CaptchaUtil;
import com.thend.home.sweethome.config.ConfigUtils;
import com.thend.home.sweethome.consistent.DBUtil;
import com.thend.home.sweethome.exception.LogicException;
import com.thend.home.sweethome.exception.LogicException.LogicExpStatus;
import com.thend.home.sweethome.httpclient.HttpClientUtil;
import com.thend.home.sweethome.md5.IDUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        App app = new App();
//        try {
//        	app.test(false);
//        } catch (LogicException e){
//        	System.out.println(e.getErrorCode() + ":" + e.getMessage());
//        }
//        app.captcha();
//        app.xmlConfig();
//        app.genIDTest("carrollwk@yahoo.com.cn");
//        app.blowfish();
//        app.updateCover();
//        app.captcha();
//        app.xmlConfig();
//        app.genIDTest("carrollwk@yahoo.com.cn");
//        app.blowfish();
        app.hash();
    }
    
    public void hash() {
    	List<String> keys = new ArrayList<String>();
    	keys.add("feed001");
    	keys.add("feed002");
    	keys.add("feed003");
    	keys.add("feed004");
    	keys.add("feed005");
    	keys.add("feed006");
    	DBUtil.initConstHash(keys);
    	for(int i=0;i<10;i++) {
    		System.out.println(DBUtil.getDistKey(i));
    	}
    }
    
    public void test(boolean b) throws LogicException {
    	if(b) {
    		throw new LogicException(LogicExpStatus.LACK_OF_MONEY);
    	}
    	try {
    		throw new NullPointerException();
    	} catch (Exception e) {
    		throw new LogicException(LogicExpStatus.SERVER_ERROR,e);
    	}
    }
    
    public void captcha() {
    	Captcha captcha = CaptchaUtil.drawImage(1, 6, 120, 50);
        String key = captcha.getAnswer();
        System.out.println("-------------key-------------" + key);
        
        try {
			CaptchaUtil.writeImage(new FileOutputStream(new File("pic.jpg")), captcha.getImage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public void propConfig() {
    	Configuration conf = null;
    	try {
    		conf = ConfigUtils.parsePropertyConfig(new File(this.getClass().getResource("/").getFile()), new String[]{"conf.properties"});
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	if(conf != null) {
    		Iterator<String> iter = conf.getKeys();
    		while(iter.hasNext()) {
    			String key = iter.next();
        		System.out.println(key + "=" + conf.getString(key));
    		}
    	}
    }
    
    public void xmlConfig() {
    	XMLConfiguration conf = null;
    	try {
    		conf = (XMLConfiguration) ConfigUtils.parseXmlConfig(new File(this.getClass().getResource("/").getFile()), new String[]{"config.xml"}).getConfiguration(0);
    		conf.setExpressionEngine(new XPathExpressionEngine());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	Iterator<String> iter = conf.getKeys();
    	while(iter.hasNext()) {
    		System.out.println(iter.next());
    	}
    	if(conf != null) {
    		List<Object> list = conf.getList("services/group[@name='central']/hosts/host");
    		for(Object obj : list) {
    			System.out.println(obj.toString());
    		}
    		String str = conf.getString("services/group[@name='central']/functions/function[@name='delete']/@path");
    		System.out.println(str);
    	}
    }
    
    public void genIDTest(String email) {
		System.out.println(IDUtils.genID(email));
    }
    
    public void blowfish() {
		String token = "429927b290452fecc49d61effa485698";
		BlowFish bf = new BlowFish("thend");
		String encodedStr = bf.encryptString(token);
		System.out.println(encodedStr);
		String ori = bf.decryptString(encodedStr);
		System.out.println(ori);
    }
    
    public void updateCover() {
	  String url = "http://localhost:8280/updatecover";
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      builder.setCharset(Charset.forName("utf-8"));
      builder.addTextBody("roomId", "100015");
      builder.addTextBody("iscover", "1");
//      builder.addBinaryBody("file", new File("obama.jpg"));
      builder.addPart("file", new FileBody(new File("obama.jpg")));
	  String ret = HttpClientUtil.getInstance().execute(url, builder.build());
      System.out.println(ret);
    }
}
