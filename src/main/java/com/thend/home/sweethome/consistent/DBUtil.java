package com.thend.home.sweethome.consistent;

import java.util.ArrayList;
import java.util.List;

public class DBUtil {
	
	private static ConsistentHash consistentHash;
	
	private static FairHash fairHash;
	
	private static final int VIRTUAL_SIZE = 8;
	
	private static final int BUCKET_SIZE = 4096;
	
	public static void initDBHash(List<String> keys) {
		List<Integer> buckets = new ArrayList<Integer>(BUCKET_SIZE);
		for(int i=0;i<BUCKET_SIZE;i++) {
			buckets.add(i);
		}
		consistentHash = new ConsistentHash(buckets, VIRTUAL_SIZE);
		fairHash = new FairHash(keys, BUCKET_SIZE);
	}
	
	public static String getDistKey(long factor) {
		int bucket = consistentHash.getRealKey("#" + factor + "#");
		return fairHash.getRealKey(bucket);
	}
	
	public static void printFairHash() {
		if(fairHash != null) {
			fairHash.print();
		}
	}
}
