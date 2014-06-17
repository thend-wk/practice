/**
 * @(#)HotLocalCacheTest.java, 2012-3-16. 
 * 
 * Copyright 2012 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thend.home.sweethome.localcache;

import java.util.Random;

/**
 * test.
 * @author wangkai
 *
 */
public class HotLocalCacheTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        HotLocalCache hlc = new HotLocalCache();
        for(int i=2000;i<3000;i++) {
            hlc.put(i+"",i);
        }
        System.out.println(hlc.getSize());
//        hlc.clear();
//        System.out.println(hlc.getSize());
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Random ran = new Random();
        int i = 10000;
        while(i-->0) {
            Object obj = hlc.get(ran.nextInt(6000) + "");
            if(obj != null) {
                System.out.println((Integer)obj);
            }
        }
        System.out.println(hlc.getSize());
    }

}
