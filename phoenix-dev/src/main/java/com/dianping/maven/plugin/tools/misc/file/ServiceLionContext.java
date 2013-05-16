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
package com.dianping.maven.plugin.tools.misc.file;

import java.io.File;
import java.util.Map;

public class ServiceLionContext {
    private Map<String, File>  projectBaseDirMapping;
    private ServiceMetaContext serviceMetaContext;
    private String             serviceHost;
    private boolean            refreshServiceMeta;

    public ServiceLionContext(Map<String, File> projectBaseDirMapping, ServiceMetaContext serviceMetaContext,
            String serviceHost, boolean refreshServiceMeta) {
        this.projectBaseDirMapping = projectBaseDirMapping;
        this.serviceMetaContext = serviceMetaContext;
        this.serviceHost = serviceHost;
        this.refreshServiceMeta = refreshServiceMeta;
    }

    public Map<String, File> getProjectBaseDirMapping() {
        return projectBaseDirMapping;
    }

    public ServiceMetaContext getServiceMetaContext() {
        return serviceMetaContext;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public boolean isRefreshServiceMeta() {
        return refreshServiceMeta;
    }

}
