package com.thend.home.sweethome.consistent;

import java.util.List;

public class DBUtil {
	
	private static ConsistentHash consistentHash;
	
	private static FairHash fairHash;
	
	private static final int VIRTUAL_SIZE = 1024;
	
	private static final int BUCKET_SIZE = 1024;
	
	public static void initConstHash(List<String> keys) {
		if(consistentHash == null) {
			consistentHash = new ConsistentHash(keys, VIRTUAL_SIZE);
		}
	}
	
	public static void initFairHash(List<String> keys) {
		if(fairHash == null) {
			fairHash = new FairHash(keys, BUCKET_SIZE);
		}
	}
	
	public static String getConstDistKey(long factor) {
		return consistentHash.getRealKey(String.valueOf(factor));
	}
	
	public static String getFairDistKey(long factor) {
    	long factorUnsigned = Math.abs(factor);
    	int mod = (int)(factorUnsigned % BUCKET_SIZE);
		return fairHash.getRealKey(mod);
	}
	
	public static void printFairHash() {
		if(fairHash != null) {
			fairHash.print();
		}
	}
	
}
