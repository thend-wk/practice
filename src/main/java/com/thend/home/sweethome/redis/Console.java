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
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;

public class Console {
	
	private static List<String> redisHostList;
	
	private static String configRedisHost;
	
	private static int configRedisPort;
	
	private SimpleShardedJedisPool shardedJedisPool;
	
	private JedisPool configJedisPool;
	
	static {
		redisHostList = new ArrayList<String>();
		redisHostList.add("123.58.176.106:6379");
		redisHostList.add("123.58.176.106:6380");
		redisHostList.add("123.58.176.106:6381");
		redisHostList.add("123.58.176.106:6382");
		redisHostList.add("123.58.176.106:6383");
		redisHostList.add("123.58.176.106:6384");
		redisHostList.add("123.58.176.107:6379");
		redisHostList.add("123.58.176.107:6380");
		redisHostList.add("123.58.176.107:6381");
		redisHostList.add("123.58.176.107:6382");
		redisHostList.add("123.58.176.107:6383");
		redisHostList.add("123.58.176.107:6384");
		configRedisHost = "123.58.176.106";
		configRedisPort = 6379;
	}
	
	public void run() {
		if(null == shardedJedisPool) {
			shardedJedisPool = new SimpleShardedJedisPool(redisHostList);
		}
		BufferedReader reader = null;
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
					} else {
						ShardedJedis shardedJedis = null;
						try {
							shardedJedis = shardedJedisPool.getResource();
							if(cmd.equalsIgnoreCase("keys")) {
								Collection<Jedis> shards = shardedJedis.getAllShards();
								Set<String> allKeys = new HashSet<String>();
								for(Jedis jedis : shards) {
									Set<String> keys = jedis.keys("*");
									allKeys.addAll(keys);
								}
								for(String key : allKeys) {
									System.out.println(Serializer.toJson(key, true));
								}
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
								    	System.out.println(Serializer.toJson(value, true));
								    } catch (Exception e) {
								    	System.out.println("invalid method!");
								    }
								}
							}
						} finally {
							if(null != shardedJedisPool && null != shardedJedis) {
								shardedJedisPool.returnResource(shardedJedis);
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
		}
	}
	
	public void runTest() {
		if(null == shardedJedisPool) {
			shardedJedisPool = new SimpleShardedJedisPool(redisHostList);
		}
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = shardedJedisPool.getResource();
			Object value = shardedJedis.hgetAll("user#-9187505337059418980#");
			System.out.println(Serializer.toJson(value, true));
		} finally {
			shardedJedisPool.returnResource(shardedJedis);
		}
	}
	
	public void runConfig() {
		if(null == configJedisPool) {
			configJedisPool = new JedisPool(configRedisHost,configRedisPort);
		}
		BufferedReader reader = null;
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
					} else {
						Jedis jedis = null;
						try {
							jedis = configJedisPool.getResource();
							if(cmd.equalsIgnoreCase("keys")) {
								Set<String> allKeys = new HashSet<String>();
								Set<String> keys = jedis.keys("*");
								allKeys.addAll(keys);
								for(String key : allKeys) {
									System.out.println(Serializer.toJson(key, true));
								}
							} else {
								if(paramLen > 1) {
									Object[] args = ArrayUtils.subarray(params, 1, paramLen);
									Object[] newArgs = new Object[paramLen - 1];
									Class ownerClass = jedis.getClass();
								    Class[] argsClass = new Class[args.length];
								    for (int i = 0, j = args.length; i < j; i++) {
							    		String param = args[i].toString();
								    	try {
								    		if(param.contains("L")) {
								    			argsClass[i] = long.class;
								    			newArgs[i] = new Long(param.replace("L", ""));
								    		} else {
								    			newArgs[i] = new Integer(param);
								    			argsClass[i] = int.class;
								    		}
								    	} catch (Exception e) {
								    		argsClass[i] = args[i].getClass();
								    		newArgs[i] = param;
								    	}
								    }
								    try {
								    	Method method = ownerClass.getMethod(cmd,argsClass);
								    	Object value = method.invoke(jedis, newArgs);
								    	System.out.println(Serializer.toJson(value, true));
								    } catch (Exception e) {
								    	System.out.println("invalid method!");
								    }
								}
							}
						} finally {
							if(null != configJedisPool && null != jedis) {
								configJedisPool.returnResource(jedis);
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
		}
	}
	
	public static void main(String[] args) {
		printUsage();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			String readLine=reader.readLine();
			if(readLine.equalsIgnoreCase("1")) {
				new Console().runConfig();
			} else if(readLine.equalsIgnoreCase("2")) {
				new Console().run();
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
		}
//		new Console().runTest();
	}
	
	private static void printUsage() {
		System.out.println("1 for config;2 for common");
	}

}