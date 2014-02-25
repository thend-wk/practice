/**
 * @(#)AccessFreqPermitTest.java, 2012-5-18. 
 * 
 * Copyright 2012 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thend.home.sweethome.accessfrequency;

import com.thend.home.sweethome.localcache.HotLocalCache;

/**
 * access frequency permit test.
 * @author wangkai
 *
 */
public class AccessFreqPermitClient {

	//时间窗口
    private long window = 60*1000;
    //允许访问次数
    private int maxLen = 10;
    //惩罚时间
    private long punishTime = 10*1000;
    //本地缓存
    private HotLocalCache hlc;
    
    public AccessFreqPermitClient() {
        hlc = new HotLocalCache();
        if(!hlc.setLogLevel(HotLocalCache.class.getName(), "INFO")) {
            System.out.println("Failed in Setting HotLocalCache Log Level");
        }
    }
    
    public AccessFreqPermitClient(long timeWin, int accessFrec, long punishTime) {
    	this();
    	this.window = timeWin;
    	this.maxLen = accessFrec;
    	this.punishTime = punishTime;
    }

    public boolean permitAccess(long clientID) {
        String cacheKey = "hlc" + "-" + clientID;
        Object value = hlc.get(cacheKey);
        if(value == null) {
            AccessFrequency af = new AccessFrequency();
            af.insertRec(System.currentTimeMillis());
            hlc.put(cacheKey, af);
            return true;
        } else {
            AccessFrequency af = (AccessFrequency)value;
            long start = af.getStart();
            long now = System.currentTimeMillis();
            if(start > now) {
                return false;
            } else if(af.getAccessRecNum() < maxLen) {
                af.insertRec(now);
                hlc.put(cacheKey, af);
                return true;
            } else {
                if(now - start < window) {
                    af.clearRec();
                    af.insertRec(now + punishTime);
                    hlc.put(cacheKey, af);
                    return false;
                } else {
                    af.resetStart();
                    af.insertRec(now);
                    hlc.put(cacheKey, af);
                    return true;
                }
            }
        }
    }
}
