/**
 * Project: phoenix-router
 * 
 * File Created at 2013-4-15
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.maven.plugin.tools.generator.stable;

public class ServiceMetaContext {
    private String driverClass;
    private String url;
    private String user;
    private String pwd;

    public ServiceMetaContext(String driverClass, String url, String user, String pwd) {
        this.driverClass = driverClass;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getDriverClass() {
        return driverClass;
    }

}
