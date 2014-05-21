/**
 * @(#)HotCache.java, 2012-3-16. 
 * 
 * Copyright 2012 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thend.home.sweethome.localcache;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * HotLocalCache for Special CacheRecord.
 * @author wangkai
 *
 */
public class HotLocalCache {
    
    private static final Logger LOG = Logger.getLogger(HotLocalCache.class.getName());
    
    private int size = 1000;
    
    private long expire_time = 2*60*1000;
    
    private LRUCache<String, CacheData> cache;
    
    private int missed = 0;
    
    private int hitted = 0;
    
    public HotLocalCache() {
        init();
    }
    
    public HotLocalCache(int size,long expireTime) {
        this.size = size;
        this.expire_time = expireTime;
        init();
    }
    
    private void init() {
        cache = new LRUCache<String,CacheData>(size);
    }
    public synchronized void put(String key,Object value) {
        cache.put(key, new CacheData(value, System.currentTimeMillis() + expire_time));
    }
    
    public synchronized Object get(String key) {
        long current = System.currentTimeMillis();
        CacheData cr = cache.get(key);
        if(cr == null) {
            missed++;
            LOG.info("record not hitted!");
            return null;
        }
        if(current > cr.getExpireTime()) {
            cache.remove(key);
            missed++;
            LOG.info("record hitted but expired!");
            return null;
        } else {
            hitted++;
            LOG.info("record hitted and return!");
            if(hitted % 1000 == 0) {
                LOG.info("@@ANALYSIS@@ Records Hitted in HotLocalCache->" + hitted);
                LOG.info("@@ANALYSIS@@ Records missed in HotLocalCache->" + missed);
                LOG.info("@@ANALYSIS@@ Records Hitted Percent in HotLocalCache->" + 
                        ((double)hitted*100)/(hitted + missed) + "%");
            }
            return cr.getValue();
        }
    }
    
    public synchronized void remove(String key) {
        cache.remove(key);
    }
    
    public synchronized void clear() {
        cache.clear();
    }
    
    public synchronized int getSize() {
        return cache.getSize();
    }
    
    private class CacheData {
        
        private Object value;
        
        private long expireTime;
        
        public CacheData(Object value,long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
    
    public boolean setLogLevel(String loggerName, String level) {
        try {
            Logger.getLogger(loggerName).setLevel(Level.parse(level));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
