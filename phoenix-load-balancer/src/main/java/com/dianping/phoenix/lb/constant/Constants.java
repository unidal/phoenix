/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Nov 1, 2013
 * 
 */
package com.dianping.phoenix.lb.constant;

import java.util.Arrays;

/**
 * @author Leo Liang
 * 
 */
public class Constants {
    public static String   LOCATION_MATCHTYPE_PREFIX = "prefix";
    public static String   LOCATION_MATCHTYPE_REGEX  = "regex";
    public static String   LOCATION_MATCHTYPE_COMMON = "common";
    public static String[] LOCATION_MATCHTYPES       = new String[] { LOCATION_MATCHTYPE_PREFIX,
            LOCATION_MATCHTYPE_REGEX, LOCATION_MATCHTYPE_COMMON };

    public static void main(String[] args) {
        System.out.println(Arrays.asList(Constants.LOCATION_MATCHTYPES));
    }
}
