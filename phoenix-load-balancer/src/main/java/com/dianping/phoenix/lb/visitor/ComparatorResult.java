/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Nov 8, 2013
 * 
 */
package com.dianping.phoenix.lb.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leo Liang
 * 
 */
public class ComparatorResult {
    
    public static enum ChangeType {
        ADD, UPDATE, DELETE;
    }

    private Map<ChangeType, List<Object>> diffResult = new HashMap<ChangeType, List<Object>>();
    
}
