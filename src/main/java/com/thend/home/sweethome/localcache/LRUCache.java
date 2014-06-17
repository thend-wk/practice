/**
 * @(#)LRUCache.java, 2012-3-16. 
 * 
 * Copyright 2012 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thend.home.sweethome.localcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Latest Recently Used Cache for Different Key and Value.
 * Thread Safe.
 * @author wangkai
 *
 */
public class LRUCache<K,V>{
    
    private static final float FACTOR = 0.75f;
    
    private Map<K,V> map;
    
    private int cacheSize;
    
    /**
     * 构造函数1
     */
    public LRUCache(int cacheSize,boolean accessOrder) {
        this.cacheSize = cacheSize;
        map = new LinkedHashMap<K,V>(cacheSize,FACTOR,accessOrder){
            private static final long serialVersionUID = 3815132098247551663L;
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
                boolean todel = size() > LRUCache.this.cacheSize;
                return todel;
            }
        };
    }
    
    /**
     * 构造函数2
     */
    public LRUCache(int cacheSize) {
        this(cacheSize,true);
    }
    
    public synchronized void put(K key,V value) {
        map.put(key, value);
    }
    
    public synchronized V get(K key) {
        return map.get(key);
    }
    
    public synchronized void clear() {
        map.clear();
    }
    
    public synchronized Collection<Entry<K,V>> getAllRecords() {
        return new ArrayList<Entry<K,V>>(map.entrySet());
    }
    
    public synchronized void remove(K key) {
        map.remove(key);
    }
    
    public synchronized int getSize() {
        return map.size();
    }
}
