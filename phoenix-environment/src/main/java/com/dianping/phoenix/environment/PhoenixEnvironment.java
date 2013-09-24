package com.dianping.phoenix.environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于ThreadLocal的通用内存容器，用于在各个平台级别的组件中传递变量
 * 
 * @author kezhu.wu
 * 
 */
public class PhoenixEnvironment {

    public static final String                      MOBILE_REQUEST_ID       = "pragma-page-id";
    public static final String                      MOBILE_REFER_REQUEST_ID = "pragma-prev-page-id";
    public static final String                      METAS                   = "metas";
    public static final String                      REQUEST_ID              = "request_id";
    public static final String                      REFER_REQUEST_ID        = "refer_request_id";
    public static final String                      GUID                    = "guid";
    public static final String                      ENV                     = "phoenixEnvironment";

    private static ThreadLocal<Map<String, Object>> map                     = new ThreadLocal<Map<String, Object>>() {
                                                                                protected synchronized Map<String, Object> initialValue() {
                                                                                    return new HashMap<String, Object>();
                                                                                }
                                                                            };

    private static ThreadLocal<Map<String, Object>> inheritableMap          = new InheritableThreadLocal<Map<String, Object>>() {
                                                                                protected synchronized Map<String, Object> initialValue() {
                                                                                    return new HashMap<String, Object>();
                                                                                }
                                                                            };

    /**
     * 根据key获取存在ThreadLocal的map中的对象
     * 
     * @param key
     * @return
     */
    public static Object get(String key) {
        return get(key, false);
    }

    /**
     * 以key将value存在ThreadLocal的map中
     * 
     * @param key
     * @return
     */
    public static void set(String key, Object value) {
        set(key, value, false);
    }

    /**
     * 以key将value存在ThreadLocal的map中
     * 
     * @param key
     * @param inheritable 如果为true，则从InheritableThreadLocal的map获取，否则从ThreadLocal的map获取
     * @return
     */
    public static Object get(String key, boolean inheritable) {
        if (inheritable) {
            return inheritableMap.get().get(key);
        } else {
            return map.get().get(key);
        }
    }

    /**
     * 以key将value存在ThreadLocal中
     * 
     * @param key
     * @param inheritable 如果在true，则使用InheritableThreadLocal的map来存储，否则使用ThreadLocal的map存储
     * @return
     */
    public static void set(String key, Object value, boolean inheritable) {
        if (inheritable) {
            inheritableMap.get().put(key, value);
        } else {
            map.get().put(key, value);
        }
    }

    /**
     * 获取metas，用于页头插入到html中
     */
    public static String getMetas() {
        return (String) get(METAS);
    }

    public static void setMetas(String metas) {
        set(METAS, metas);
    }

    public static String getRequestId() {
        return (String) get(REQUEST_ID);
    }

    public static void setRequestId(String requestId) {
        set(REQUEST_ID, requestId);
    }

    public static String getReferRequestId() {
        return (String) get(REFER_REQUEST_ID);
    }

    public static void setReferRequestId(String referRequestId) {
        set(REFER_REQUEST_ID, referRequestId);
    }

    public static String getGuid() {
        return (String) get(GUID);
    }

    public static void setGuid(String guid) {
        set(GUID, guid);
    }

}
