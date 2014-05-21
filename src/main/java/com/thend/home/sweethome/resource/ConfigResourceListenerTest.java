package com.thend.home.sweethome.resource;
public class ConfigResourceListenerTest {
	
	public static void main(String[] args) throws Exception {
		/*
		String filePath = ConfigResourceListenerTest.class.getClassLoader().getResource("config.txt").getFile();
		System.out.println(filePath);
		System.out.println(FileUtils.readFileToString(new File(filePath)));
		*/
		String filePath = "labourDayRank";
		String url = "http://frontend1.live.163.com/rank/getLabourDayRank?limit=6";
		ConfigResourceListener configResourceListener = new ConfigResourceListener(url, filePath);
		
		Thread.sleep(10000000);
	}

}
