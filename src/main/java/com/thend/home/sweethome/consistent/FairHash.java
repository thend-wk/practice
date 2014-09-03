package com.thend.home.sweethome.consistent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
/**
 * 均分哈希
 * @author wangkai
 *
 */
public class FairHash {
	
	private TreeMap<Integer, String> keysMap;
	
	public FairHash(List<String> keys, int bucketSize) {
		keysMap = new TreeMap<Integer, String>();
		int keySize = keys.size();
		if(keySize > 0 && bucketSize > 0) {
			int interval = bucketSize % keySize == 0 ? bucketSize / keySize : (bucketSize / keySize) + 1;
			int slash = interval;
			List<Integer> boundary = new ArrayList<Integer>();
			while(slash < bucketSize) {
				boundary.add(slash);
				slash += interval;
			}
			boundary.add(bucketSize);
			int bondSize = boundary.size();
			for(int i=0;i<bucketSize;i++) {
				int idx = 0;
				for(int j=0;j<bondSize;j++) {
					if(i < boundary.get(j)) {
						idx = j;
						break;
					}
				}
				keysMap.put(i, keys.get(idx));
			}
		}
	}
	
	public String getRealKey(int mod) {
		return keysMap.get(mod);
	}

	public void print() {
		if(keysMap != null) {
			Set<Integer> keySet = keysMap.keySet();
			for(Integer key : keySet) {
				System.out.println(key + "#" + keysMap.get(key));
			}
		}
	}
}
