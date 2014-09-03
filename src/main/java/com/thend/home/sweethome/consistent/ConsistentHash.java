package com.thend.home.sweethome.consistent;

import java.security.MessageDigest;
import java.util.TreeMap;

/**
 * 一致性哈希
 * 
 * @author wangkai
 * 
 */
public class ConsistentHash {

	private TreeMap<Long, String> keysMap;

	/**
	 * 构造函数,指定需要散列的键值以及虚拟节点数
	 * 
	 * @param keys
	 * @param virtualSize
	 */
	public ConsistentHash(Iterable<String> keys, int virtualSize) {
		keysMap = new TreeMap<Long, String>();
		for (String key : keys) {
			for (int i = 0; i < virtualSize / 4; i++) {
				byte[] digest = md5(key + i);
				for (int h = 0; h < 4; h++) {
					long m = hash(digest, h);
					keysMap.put(m, key);
				}
			}
		}
	}

	/**
	 * 获取命中的键值
	 * 
	 * @param data
	 * @return
	 */
	public String getRealKey(String data) {
		byte[] digest = md5(data);
		Long key = hash(digest, 0);
		if (!keysMap.containsKey(key)) {
			key = keysMap.ceilingKey(key);
			if (key == null) {
				key = keysMap.firstKey();
			}
		}
		return keysMap.get(key);
	}

	/**
	 * 每隔四位取值求Hash值
	 * 
	 * @param bytes
	 *            16位数组
	 * @param nTime
	 *            0<=nTime<=3
	 * @return
	 */
	private long hash(byte[] bytes, int nTime) {
		long rv = ((long) (bytes[3 + nTime * 4] & 0xFF) << 24)
				| ((long) (bytes[2 + nTime * 4] & 0xFF) << 16)
				| ((long) (bytes[1 + nTime * 4] & 0xFF) << 8)
				| (bytes[0 + nTime * 4] & 0xFF);

		return rv & 0xffffffffL; /* Truncate to 32-bits */
	}

	/**
	 * 计算md5值
	 */
	private byte[] md5(String str) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			byte[] keyBytes = str.getBytes("UTF-8");
			md5.update(keyBytes);
			return md5.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
