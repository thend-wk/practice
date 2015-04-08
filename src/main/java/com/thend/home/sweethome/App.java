package com.thend.home.sweethome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.thend.home.sweethome.async.AsyncExecutor;
import com.thend.home.sweethome.blowfish.BlowFish;
import com.thend.home.sweethome.captcha.Captcha;
import com.thend.home.sweethome.captcha.util.CaptchaUtil;
import com.thend.home.sweethome.config.ConfigUtils;
import com.thend.home.sweethome.consistent.DBUtil;
import com.thend.home.sweethome.exception.LogicException;
import com.thend.home.sweethome.exception.LogicException.LogicExpStatus;
import com.thend.home.sweethome.httpclient.HttpClientUtil;
import com.thend.home.sweethome.md5.IDUtils;
import com.thend.home.sweethome.md5.ShortenUtil;
import com.thend.home.sweethome.redis.Serializer;

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
//        app.constHash();
//        app.dbHash();
//        app.doTask();
//        app.doSerialize();
//        app.doShort();
//        app.sendMsg();
//        app.giveCCurrency();
//        app.analyze();
        app.doCharacter();
    }
    
    public void doCharacter() {
  		String str = "\u202E明天";
  		str = str.replaceAll("\\p{C}", "");
  		System.out.println(str);
  		Calendar c = Calendar.getInstance();
  		c.set(2015, 3, 18, 0, 0, 0);
  		System.out.println(c.getTime());
      }
    
    public void doShort() {
		int num = (int) IDUtils.genID("052fdaeafa7e49f7d05c36b29b630de9");
		System.out.println(ShortenUtil.numberToString(num));
		
    }
    
    public void doSerialize() {
    	TestEntity testEntity = new TestEntity();
    	testEntity.setName("thend");
    	testEntity.setUserId(3680169311752123499L);
    	System.out.println(Serializer.toJson(testEntity, true));
    }
    
    class TestEntity {
    	private String name;
    	private long userId;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getUserId() {
			return userId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}
    }
    
    class TestTask implements Runnable {
    	
    	private String name;
    	
    	public TestTask(String name) {
    		this.name = name;
    	}

		public void run() {
			System.out.println("test task!" + name);
		}
    	
    }
    
    public void doTask() {
    	AsyncExecutor.submit(new TestTask("thend"));
    }
    
    public void dbHash() {
    	List<String> keys = new ArrayList<String>();
    	keys.add("feed001");
    	keys.add("feed002");
    	keys.add("feed003");
    	keys.add("feed004");
    	keys.add("feed005");
    	keys.add("feed006");
    	DBUtil.initDBHash(keys);
    	for(int i=0;i<200;i++) {
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
    
//    public void updateCover() {
//	  String url = "http://localhost:8280/updatecover";
//      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//      builder.setCharset(Charset.forName("utf-8"));
//      builder.addTextBody("roomId", "100015");
//      builder.addTextBody("iscover", "1");
////      builder.addBinaryBody("file", new File("obama.jpg"));
//      builder.addPart("file", new FileBody(new File("obama.jpg")));
//	  String ret = HttpClientUtil.getInstance().execute(url, builder.build());
//      System.out.println(ret);
//    }
    
    @SuppressWarnings("unchecked")
	public void sendMsg() {
    	try {
	    	String url = "http://www.bobo.com/message/send.do";
	    	String toUserId = "";
	    	List<String> anchorMessages = FileUtils.readLines(new File("anchor_message.txt"));
	    	for(String anchorMessage : anchorMessages) {
		    	String message = "恭喜您获得《美好的你有未来》——官方节目大房间送出的电影卷福利，兑换码：%s，请打开连接http://piao.163.com/code/exchangeCode.html，按提示方法兑换使用。下期官方节目还有更多福利送出，不要错过哦！";
	    		String[] items = anchorMessage.split(" ");
	    		toUserId = items[1];
	    		message = String.format(message, items[0]);
		    	List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		    	nvPairs.add(new BasicNameValuePair("toUserId", toUserId));
		    	nvPairs.add(new BasicNameValuePair("message", message));
		    	UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvPairs, Consts.UTF_8);
				List<Header> headers = new ArrayList<Header>();
				headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
				headers.add(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:33.0) Gecko/20100101 Firefox/33.0"));
				headers.add(new BasicHeader("Cookie", "_ntes_nnid=eadbed6008a8e0d2654861ba7ed48b7e,1409044217354; __NETEASE_DC_A_JURASSIC_UID__=737696851@10.120.147.92; P_INFO=bobo_gift@163.com|1417054264|1|bobo|00&99|bej&1416997033&bobo#bej&null#10#0#0|&0||bobo_gift@163.com; ANONYMOUS_TEMP_USERID=temp73027153; CHECK_163BOBO=0; NTES_SESS=msL_FlufYBPHI_Bv2_gFSm4J01C9unLY_7l3kGdDFu5bnO47rnp41.jU1zAsJqkYcixLOYz3sQCEzlHEpCqI2adLmkYc8wSqyvTN44JrKqzLVytbY_o1oyhATJPuRx4J7o3yTAieO5Yju; ANTICSRF=1129fc58a3d0006bc3e45f03bc49bcb4; S_INFO=1417054264|0|2&10##|bobo_gift; NTES_PASSPORT=dwKnhHu6jZK4g0IsgE5uLAaKUP56or.AAMuqxDDpBN4TPdtC9PJt3acy3Gk5sh4HlNC_Cvc.qDW3Ou7A89Ygvnv5P"));
				headers.add(new BasicHeader("Referer","http://www.bobo.com/"));
				String ret = HttpClientUtil.getInstance().execute(url, formEntity, headers.toArray(new Header[0]));
			    System.out.println(ret);
			    Thread.sleep(10);
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
//  public void giveCCurrency() {
//	  String url = "http://cms.live.netease.com/bj/finance/whoisdaddy.do";
//	  MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//	  List<Header> headers = new ArrayList<Header>();
//	  headers.add(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:33.0) Gecko/20100101 Firefox/33.0"));
//	  headers.add(new BasicHeader("Cookie", "VASESSIONID=fd67b71d-9151-4e93-88cd-761bb8ea8142"));
//	  headers.add(new BasicHeader("Referer","http://cms.live.netease.com/"));
//	  headers.add(new BasicHeader("Authorization", "Basic bGl2ZTpxNSZedVRpPHJkMGdGbSs="));
//	  builder.setCharset(Charset.forName("utf-8"));
//	  builder.addTextBody("ammount", "50000");
//	  builder.addPart("file", new FileBody(new File("emails.txt"), ContentType.TEXT_PLAIN));
//	  String ret = HttpClientUtil.getInstance().execute(url, builder.build(), headers.toArray(new Header[0]));
//	  System.out.println(ret);
//  }
    public void analyze() {
    	try {
    		List<String> users = FileUtils.readLines(new File("12306.txt"));
    		String url = "http://v.showji.com/locating/showji.com1118.aspx?m=%s&output=json";
    		List<String> bjPhones = new ArrayList<String>();
    		for(String user : users) {
    			String[] items = user.split("----");
    			String phone = items[5];
		    	List<NameValuePair> nvPairs = new ArrayList<NameValuePair>();
		    	UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvPairs, Consts.UTF_8);
				List<Header> headers = new ArrayList<Header>();
				headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
				headers.add(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 5.1; rv:33.0) Gecko/20100101 Firefox/33.0"));
				headers.add(new BasicHeader("Referer","http://v.showji.com"));
    			String ret = HttpClientUtil.getInstance().execute(String.format(url, phone), formEntity, headers.toArray(new Header[0]));
    			try {
	    			Map<String,String> map = Serializer.json.readValue(ret, Map.class);
	    			String province = map.get("Province");
	    			if(StringUtils.isNotBlank(province) && province.equals("北京")) {
	    				bjPhones.add(phone);
		    			System.out.println(province + "->" + phone);
	    			}
//	    			Thread.sleep(2000);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    		FileUtils.writeLines(new File("bjPhones.txt"), bjPhones);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
