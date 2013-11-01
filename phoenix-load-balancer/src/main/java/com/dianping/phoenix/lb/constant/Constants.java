/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Nov 1, 2013
 * 
 */
package com.dianping.phoenix.lb.constant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Leo Liang
 * 
 */
public class Constants {
    public static String   LOCATION_MATCHTYPE_PREFIX    = "prefix";
    public static String   LOCATION_MATCHTYPE_REGEX     = "regex";
    public static String   LOCATION_MATCHTYPE_COMMON    = "common";
    public static String[] LOCATION_MATCHTYPES;

    public static String   DIRECTIVE_TYPE_PROXYPASS     = "proxy_pass";
    public static String   DIRECTIVE_TYPE_REWRITE       = "rewrite";
    public static String   DIRECTIVE_TYPE_STATICRES     = "static-resource";
    public static String   DIRECTIVE_TYPE_RETURN        = "return";
    public static String[] DIRECTIVE_TYPES;

    public static String   DIRECTIVE_PROXYPASS_POOLNAME = "pool-name";

    public static String   DIRECTIVE_REWRITE_BEFORE     = "matches";
    public static String   DIRECTIVE_REWRITE_AFTER      = "target-pattern";
    public static String   DIRECTIVE_REWRITE_BREAK      = "break";
    public static String   DIRECTIVE_REWRITE_LAST       = "last";

    public static String   DIRECTIVE_RETURN_CODE        = "response-code";

    public static String   DIRECTIVE_STATICRES_ROOTDOC  = "root-doc";
    public static String   DIRECTIVE_STATICRES_EXP      = "expires";

    static {
        try {
            Field[] fields = Constants.class.getDeclaredFields();
            List<String> locationTypes = new ArrayList<String>();
            List<String> directiveTypes = new ArrayList<String>();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().startsWith("LOCATION_MATCHTYPE_")) {
                    locationTypes.add((String) field.get(null));
                } else if (field.getName().startsWith("DIRECTIVE_TYPE_")) {
                    directiveTypes.add((String) field.get(null));
                }
            }
            LOCATION_MATCHTYPES = locationTypes.toArray(new String[locationTypes.size()]);
            DIRECTIVE_TYPES = directiveTypes.toArray(new String[locationTypes.size()]);

        } catch (Exception e) {
            throw new RuntimeException("Init Constants failed.", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.asList(Constants.LOCATION_MATCHTYPES));
        System.out.println(Arrays.asList(Constants.DIRECTIVE_TYPES));
    }
}
