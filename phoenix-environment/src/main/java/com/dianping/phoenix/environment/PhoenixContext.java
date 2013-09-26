package com.dianping.phoenix.environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于ThreadLocal的通用内存容器，用于在各个平台级别的组件中传递变量
 * 
 * @author kezhu.wu
 * 
 */
public class PhoenixContext {

    public static final String               MOBILE_REQUEST_ID       = "pragma-page-id";
    public static final String               MOBILE_REFER_REQUEST_ID = "pragma-prev-page-id";
    public static final String               METAS                   = "metas";
    public static final String               REQUEST_ID              = "request_id";
    public static final String               REFER_REQUEST_ID        = "refer_request_id";
    public static final String               GUID                    = "guid";
    public static final String               ENV                     = "phoenixEnvironment";

    private static PhoenixContext            instance                = new PhoenixContext();

    private ThreadLocal<Map<String, Object>> map                     = new ThreadLocal<Map<String, Object>>() {
                                                                         protected synchronized Map<String, Object> initialValue() {
                                                                             return new HashMap<String, Object>();
                                                                         }
                                                                     };

    private PhoenixContext() {
    }

    public static PhoenixContext getInstance() {
        return instance;
    }

    /**
     * 根据key获取存在ThreadLocal的map中的对象
     * 
     * @param key
     * @return
     */
    public Object get(String key) {
        return map.get().get(key);
    }

    /**
     * 以key将value存在ThreadLocal的map中
     * 
     * @param key
     * @return
     */
    public void set(String key, Object value) {
        map.get().put(key, value);
    }

    /**
     * 清除ThreadLocal
     */
    public void clear() {
        map.remove();
    }

    /**
     * 获取metas，用于页头插入到html中
     */
    public String getMetas() {
        return (String) get(METAS);
    }

    public void setMetas(String metas) {
        set(METAS, metas);
    }

    public String getRequestId() {
        return (String) get(REQUEST_ID);
    }

    public void setRequestId(String requestId) {
        set(REQUEST_ID, requestId);
    }

    public String getReferRequestId() {
        return (String) get(REFER_REQUEST_ID);
    }

    public void setReferRequestId(String referRequestId) {
        set(REFER_REQUEST_ID, referRequestId);
    }

    public String getGuid() {
        return (String) get(GUID);
    }

    public void setGuid(String guid) {
        set(GUID, guid);
    }

}
