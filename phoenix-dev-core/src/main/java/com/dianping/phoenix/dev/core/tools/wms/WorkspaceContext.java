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
package com.dianping.phoenix.dev.core.tools.wms;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;

/**
 * @author Leo Liang
 * 
 */
public class WorkspaceContext implements Serializable {
    private static final long serialVersionUID     = -7734656051647733810L;
    private File              baseDir;
    private List<BizProject>  projects             = new ArrayList<BizProject>();
    private String            phoenixRouterVersion = "0.1-SNAPSHOT";
    private boolean           cleanFolder;

    /**
     * @return the cleanFolder
     */
    public boolean isCleanFolder() {
        return cleanFolder;
    }

    /**
     * @param cleanFolder
     *            the cleanFolder to set
     */
    public void setCleanFolder(boolean cleanFolder) {
        this.cleanFolder = cleanFolder;
    }

    /**
     * @return the phoenixRouterVersion
     */
    public String getPhoenixRouterVersion() {
        return phoenixRouterVersion;
    }

    /**
     * @param phoenixRouterVersion
     *            the phoenixRouterVersion to set
     */
    public void setPhoenixRouterVersion(String phoenixRouterVersion) {
        this.phoenixRouterVersion = phoenixRouterVersion;
    }

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
    public List<BizProject> getProjects() {
        return projects;
    }

    /**
     * @param projects
     *            the projects to set
     */
    public void setProjects(List<BizProject> projects) {
        this.projects = projects;
    }

    public void addProject(BizProject project) {
        projects.add(project);
    }

}
