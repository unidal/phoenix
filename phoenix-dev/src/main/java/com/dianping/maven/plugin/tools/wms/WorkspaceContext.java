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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo Liang
 * 
 */
public class WorkspaceContext implements Serializable {
    private static final long serialVersionUID     = -7734656051647733810L;
    private File              baseDir;
    private List<String>      projects             = new ArrayList<String>();
    private String            phoenixRouterVersion = "0.1-SNAPSHOT";
    private boolean           cleanFolder;
    private String            gitConfigRepositoryUrl;
    private String            gitConfigRepositoryBranch;

    /**
     * @return the gitConfigRepositoryUrl
     */
    public String getGitConfigRepositoryUrl() {
        return gitConfigRepositoryUrl;
    }

    /**
     * @param gitConfigRepositoryUrl
     *            the gitConfigRepositoryUrl to set
     */
    public void setGitConfigRepositoryUrl(String gitConfigRepositoryUrl) {
        this.gitConfigRepositoryUrl = gitConfigRepositoryUrl;
    }

    /**
     * @return the gitConfigRepositoryBranch
     */
    public String getGitConfigRepositoryBranch() {
        return gitConfigRepositoryBranch;
    }

    /**
     * @param gitConfigRepositoryBranch
     *            the gitConfigRepositoryBranch to set
     */
    public void setGitConfigRepositoryBranch(String gitConfigRepositoryBranch) {
        this.gitConfigRepositoryBranch = gitConfigRepositoryBranch;
    }

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

    public void addProject(String project) {
        projects.add(project);
    }

}
