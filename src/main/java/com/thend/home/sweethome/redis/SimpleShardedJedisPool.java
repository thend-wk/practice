package com.thend.home.sweethome.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

public class SimpleShardedJedisPool extends ShardedJedisPool {

	public SimpleShardedJedisPool(List<String> shards) {
		super(new JedisPoolConfig(), parseShardInfo(shards));
	}

	public SimpleShardedJedisPool(Config poolConfig, List<String> shards,
			String keyTagPattern) {
		super(poolConfig, parseShardInfo(shards), Pattern
				.compile(keyTagPattern));
	}

	private static List<JedisShardInfo> parseShardInfo(List<String> shards) {
		List<JedisShardInfo> result = new ArrayList<JedisShardInfo>();
		for (String s : shards) {
			String[] parts = s.split(":");
			if (parts.length > 1) {
				result.add(new JedisShardInfo(parts[0], Integer
						.parseInt(parts[1])));
			} else {
				result.add(new JedisShardInfo(parts[0]));
			}
		}
		return result;
	}

}
