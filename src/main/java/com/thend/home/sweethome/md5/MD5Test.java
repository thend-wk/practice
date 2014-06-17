package com.thend.home.sweethome.md5;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Test {
	
	public static void main(String[] args) {
		String key = "netease163";
		System.out.println(DigestUtils.md5Hex(key));
	}

}
