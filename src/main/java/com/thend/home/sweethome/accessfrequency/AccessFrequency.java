/**
 * @(#)AccessFrequency.java, 2012-5-18. 
 * 
 * Copyright 2012 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.thend.home.sweethome.accessfrequency;

import java.util.ArrayList;
import java.util.List;

/**
 * access frequency control.
 * @author wangkai
 *
 */
public class AccessFrequency {
        
    private List<Long> accessRec;
    
    /**
     * constructor function
     */
    public AccessFrequency() {
        accessRec = new ArrayList<Long>();
    }

    /**
     * @return the start
     */
    public long getStart() {
        return accessRec.get(0);
    }
    
    public void insertRec(long time) {
        accessRec.add(time);
    }
    
    public void clearRec() {
        accessRec.clear();
    }
    
    public int getAccessRecNum() {
        return accessRec.size();
    }
    
    public void resetStart() {
        accessRec.remove(0);
    }
    
}
