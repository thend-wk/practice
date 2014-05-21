package com.thend.home.sweethome.ip;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IPUtils {

	private static final Log logger = LogFactory.getLog(IPUtils.class);

	/**
	 * 获取IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null && ip.length() > 0) {
			String ips[] = ip.split(",");
			int i = 0;
			int length = ips.length;
			// 取第一个不是unknown且不是内网IP的IP
			for (; i < length; i++) {
				if (ips[i].trim().equalsIgnoreCase("unknown")
						|| isInnerIP(ips[i].trim()))
					continue;
				else
					break;
			}
			if (i == length) {
				ip = ips[ips.length - 1];
			} else {
				ip = ips[i];
				logger.info("After parsing. IP: " + ips[i] + "\n");
			}
		}
		return null == ip ? "" : ip.trim();
	}

	/**
	 * 判断是否是内网IP
	 * @param ipAddress
	 * @return
	 */
	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		try {
			long ipNum = getIpNum(ipAddress);
			/**
			 * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
			 * 192.168.0.0-192.168.255.255 当然，还有127这个网段是环回地址
			 */
			long aBegin = getIpNum("10.0.0.0");
			long aEnd = getIpNum("10.255.255.255");
			long bBegin = getIpNum("172.16.0.0");
			long bEnd = getIpNum("172.31.255.255");
			long cBegin = getIpNum("192.168.0.0");
			long cEnd = getIpNum("192.168.255.255");
			isInnerIp = isInner(ipNum, aBegin, aEnd)
					|| isInner(ipNum, bBegin, bEnd)
					|| isInner(ipNum, cBegin, cEnd)
					|| ipAddress.equals("127.0.0.1");
			return isInnerIp;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error when parsing Ip. IP: " + ipAddress);
			return false;
		}
	}
	
    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }
	
    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }
    
	/**
	 * 判断IP是否在允许的列表中.
	 * 如果单个IP,直接加入到允许列表中.
	 * 如果需要开放IP段,可以使用正则表达式，开发需要的IP段.
	 * 如{61.135.251.\d{0,3}}就是开放61.135.251.*范围内的所有主机.
	 * @param ip
	 * @param ipList
	 * @return
	 */
	public static boolean isAllowedIP(String ip, List<String> ipList) {
	    if(StringUtils.isBlank(ip)||ipList==null){
	        return false;
	    }
	    for(String regex:ipList){
	        Pattern purl = Pattern.compile(regex);
	        Matcher murl = purl.matcher(ip);
	        if(murl.matches()){
	            return true;
	        }
	    }
	    return false;
	}

}
