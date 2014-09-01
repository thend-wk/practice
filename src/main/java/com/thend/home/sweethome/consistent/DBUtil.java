package com.thend.home.sweethome.consistent;

import java.util.List;

public class DBUtil {
	
	private static ConsistentHash consistentHash;
	
	private static final int VIRTUAL_SIZE = 1024;
	
	public static void initConstHash(List<String> keys) {
		if(consistentHash == null) {
			consistentHash = new ConsistentHash(keys, VIRTUAL_SIZE);
		}
	}
	
	public static String getDistKey(long factor) {
		return consistentHash.getRealKey(String.valueOf(factor));
	}
}
