package com.thend.home.sweethome.base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class BASE64Test {
	
	public static void main(String[] args) throws Exception {
		String email = "carrollwk@163.com";
		System.out.println(email);
		BASE64Encoder encoder = new BASE64Encoder();
		String encodedSt = encoder.encodeBuffer(email.getBytes());
		System.out.println(encodedSt);
		BASE64Decoder decoder = new BASE64Decoder();
		System.out.println(new String(decoder.decodeBuffer(encodedSt)));
	}
}
