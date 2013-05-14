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
    private File               outputBaseDir;
    private ProjectMetaContext projectMetaContext;
    private String             serviceHost;
    private boolean            refreshProjectMeta;

    public ServiceLionContext(Map<String, File> projectBaseDirMapping, ProjectMetaContext projectMetaContext,
            String serviceHost, File outputBaseDir, boolean refreshProjectMeta) {
        this.projectBaseDirMapping = projectBaseDirMapping;
        this.outputBaseDir = outputBaseDir;
        this.projectMetaContext = projectMetaContext;
        this.serviceHost = serviceHost;
        this.refreshProjectMeta = refreshProjectMeta;
    }

    public Map<String, File> getProjectBaseDirMapping() {
        return projectBaseDirMapping;
    }

    public File getOutputBaseDir() {
        return outputBaseDir;
    }

    public ProjectMetaContext getProjectMetaContext() {
        return projectMetaContext;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public boolean isRefreshProjectMeta() {
        return refreshProjectMeta;
    }

}
