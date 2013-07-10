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
package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.io.File;
import java.util.Map;

public class ServiceLionContext {
    private Map<String, File> projectBaseDirMapping;
    private String            serviceHost;
    private File              serviceMetaConfig;

    public ServiceLionContext(Map<String, File> projectBaseDirMapping, String serviceHost, File serviceMetaConfig) {
        this.projectBaseDirMapping = projectBaseDirMapping;
        this.serviceHost = serviceHost;
        this.serviceMetaConfig = serviceMetaConfig;
    }

    public ServiceLionContext() {
    }

    public void setProjectBaseDirMapping(Map<String, File> projectBaseDirMapping) {
        this.projectBaseDirMapping = projectBaseDirMapping;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    public Map<String, File> getProjectBaseDirMapping() {
        return projectBaseDirMapping;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public File getServiceMetaConfig() {
        return serviceMetaConfig;
    }

    public void setServiceMetaConfig(File serviceMetaConfig) {
        this.serviceMetaConfig = serviceMetaConfig;
    }

}
