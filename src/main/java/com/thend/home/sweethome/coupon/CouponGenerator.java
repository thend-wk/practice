package com.thend.home.sweethome.coupon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class CouponGenerator {
	
	private static final char[] chars = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D',
		'E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	private static final int COUPON_LEN = 13;
	
	private static Random random = new Random();
	
	private static Map<String,Integer> oldCouponHash = new HashMap<String,Integer>();
	
	private static Map<String,Integer> couponHash = new HashMap<String,Integer>();
	
	private static List<String> couponList = new ArrayList<String>();

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		List<String> oldCoupons = FileUtils.readLines(new File("coupons.txt"));
		for(String oldCoupon : oldCoupons) {
			oldCouponHash.put(oldCoupon, 1);
		}
		int len = chars.length;
		while(couponHash.size() < 5000) {
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<COUPON_LEN;i++) {
				int idx = random.nextInt(len);
				sb.append(chars[idx]);
			}
			String coupon = sb.toString();
			if(!couponHash.containsKey(coupon) && !oldCouponHash.containsKey(coupon)) {
				couponHash.put(coupon, 1);
				System.out.println(couponHash.size() + " " + coupon);
			}
		}
		FileUtils.writeLines(new File("coupons6.txt"), couponHash.keySet());
	}

}
