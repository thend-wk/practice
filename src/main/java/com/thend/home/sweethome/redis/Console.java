package com.thend.home.sweethome.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

public class Console {
	
	private static List<String> redisHostList;
	
	private SimpleShardedJedisPool shardedJedisPool;
	
	static {
		redisHostList = new ArrayList<String>();
		redisHostList.add("localhost:6379");
		redisHostList.add("localhost:6380");
	}
	
	public void run() {
		if(null == shardedJedisPool) {
			shardedJedisPool = new SimpleShardedJedisPool(redisHostList);
		}
		BufferedReader reader = null;
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			String readLine = null;
			while((readLine=reader.readLine()) != null) {
				Object[] params = readLine.split(" ");
				int paramLen = params.length;
				if(paramLen > 0) {
					String cmd = String.valueOf(params[0]);
					if(cmd.equalsIgnoreCase("exit")) {
						break;
					} else if(cmd.equalsIgnoreCase("keys")) {
						Collection<Jedis> shards = shardedJedis.getAllShards();
						Set<String> allKeys = new HashSet<String>();
						for(Jedis jedis : shards) {
							Set<String> keys = jedis.keys("*");
							allKeys.addAll(keys);
						}
						System.out.println(Serializer.json.writeValueAsString(allKeys));
					} else {
						if(paramLen > 1) {
							Object[] args = ArrayUtils.subarray(params, 1, paramLen);
							Class ownerClass = shardedJedis.getClass();
						    Class[] argsClass = new Class[args.length];  
						    for (int i = 0, j = args.length; i < j; i++) {
						        argsClass[i] = args[i].getClass();  
						    }  
						    try {
						    	Method method = ownerClass.getMethod(cmd,argsClass);
						    	Object value = method.invoke(shardedJedis, args);
						    	System.out.println(Serializer.json.writeValueAsString(value));
						    } catch (Exception e) {
						    	System.out.println("invalid method!");
						    }
						}
					}
				}
			}   
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != shardedJedisPool) {
				shardedJedisPool.returnResource(shardedJedis);
			}
		}
	}
	
	public static void main(String[] args) {
		new Console().run();
	}

}