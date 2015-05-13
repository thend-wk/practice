package com.thend.home.sweethome.ip;

/*
 * IP地址定位工具类
 */
public class IpLocationUtil {
	
	private static final String ipLocationPath = "ip_location.txt";
	
	private static IpLocation ipLocation;
	
	private static void init() {
		if(ipLocation == null) {
			synchronized(IpLocationUtil.class) {
				if(ipLocation == null) {
					ipLocation = new IpLocation(ipLocationPath);
				}
			}
		}
	}

	public static void printIpLocation(String ip) {
		if(ipLocation == null) {
			init();
		}
		IpSegment locate = ipLocation.getLocate(ip);
		if (locate != null) {
			System.out.println(ip + " detail : "
					+ locate.getFormatLocation().getDetail());

			System.out.println(ip + " province : "
					+ locate.getFormatLocation().getProvince());

			System.out.println(ip + " city : "
					+ locate.getFormatLocation().getCity());

			System.out.println(ip + " detail to city : "
					+ locate.getFormatLocation().getProvinceCity());

			System.out.println(ip + " detail to province : "
					+ locate.getFormatLocation().getProvinceWithMoreInfo());

			System.out.println(ip + " com : "
					+ locate.getFormatLocation().getCom());
		}
	}
	
	public static void main(String[] args) {
		String ip = "61.135.255.84";
		ip = "59.104.167.12";
		IpLocationUtil.printIpLocation(ip);
	}
}
