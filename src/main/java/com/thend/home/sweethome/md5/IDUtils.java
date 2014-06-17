package com.thend.home.sweethome.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IDUtils {
	
    private static final ThreadLocal<MessageDigest> DIGESTER_CONTEXT = new ThreadLocal<MessageDigest>() {
        protected synchronized MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    public static long genID(String str) {
    	String factor = str.trim().toLowerCase();
    	byte[] data = factor.getBytes();
        byte[] digest;
        MessageDigest digester = DIGESTER_CONTEXT.get();
        digester.update(data, 0, data.length);
        digest = digester.digest();
        return (((long) digest[0] << 56) | ((long) (digest[1] & 0xFF) << 48)
                | ((long) (digest[2] & 0xFF) << 40)
                | ((long) (digest[3] & 0xFF) << 32)
                | ((long) (digest[4] & 0xFF) << 24)
                | ((long) (digest[5] & 0xFF) << 16)
                | ((long) (digest[6] & 0xFF) << 8) | ((long) digest[7] & 0xFF));
    }
}
