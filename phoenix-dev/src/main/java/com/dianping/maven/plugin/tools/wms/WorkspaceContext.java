/**
 * Project: phoenix-dev
 * 
 * File Created at 2013-5-14
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
package com.dianping.maven.plugin.tools.wms;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @author Leo Liang
 * 
 */
public class WorkspaceContext implements Serializable {
    private static final long serialVersionUID = -7734656051647733810L;
    private File              baseDir;
    private List<String>      projects;
    private File              bootstrapProjectDir;

    /**
     * @return the baseDir
     */
    public File getBaseDir() {
        return baseDir;
    }

    /**
     * @param baseDir
     *            the baseDir to set
     */
    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * @return the projects
     */
    public List<String> getProjects() {
        return projects;
    }

    /**
     * @param projects
     *            the projects to set
     */
    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    /**
     * @return the bootstrapProjectDir
     */
    public File getBootstrapProjectDir() {
        return bootstrapProjectDir;
    }

    /**
     * @param bootstrapProjectDir
     *            the bootstrapProjectDir to set
     */
    public void setBootstrapProjectDir(File bootstrapProjectDir) {
        this.bootstrapProjectDir = bootstrapProjectDir;
    }

}
