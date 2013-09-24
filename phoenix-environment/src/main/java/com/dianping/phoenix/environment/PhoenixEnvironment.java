package com.dianping.phoenix.environment;

import java.util.HashMap;
import java.util.Map;

public class PhoenixEnvironment {

    public static final String                      METAS            = "metas";
    public static final String                      REQUEST_ID       = "request_id";
    public static final String                      REFER_REQUEST_ID = "refer_request_id";
    public static final String                      GUID             = "guid";
    public static final String                      ENV              = "phoenixEnvironment";

    private static ThreadLocal<Map<String, Object>> map              = new ThreadLocal<Map<String, Object>>() {
                                                                         protected synchronized Map<String, Object> initialValue() {
                                                                             return new HashMap<String, Object>();
                                                                         }
                                                                     };

    private static ThreadLocal<Map<String, Object>> inheritableMap   = new InheritableThreadLocal<Map<String, Object>>() {
                                                                         protected synchronized Map<String, Object> initialValue() {
                                                                             return new HashMap<String, Object>();
                                                                         }
                                                                     };

    public static Object get(String key) {
        return get(key, false);
    }

    public static void set(String key, Object value) {
        set(key, value, false);
    }

    public static Object get(String key, boolean inheritable) {
        if (inheritable) {
            return inheritableMap.get().get(key);
        } else {
            return map.get().get(key);
        }
    }

    public static void set(String key, Object value, boolean inheritable) {
        if (inheritable) {
            inheritableMap.get().put(key, value);
        } else {
            map.get().put(key, value);
        }
    }

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
