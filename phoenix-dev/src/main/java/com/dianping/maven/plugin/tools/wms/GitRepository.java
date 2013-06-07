/**
 * Project: phoenix-maven-plugin
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

/**
 * @author Leo Liang
 * 
 */
public class GitRepository extends Repository {
    private String branch;

    public GitRepository() {
    }

    public GitRepository(String repoUrl) {
    	this(repoUrl, "master");
    }
    
    public GitRepository(String repoUrl, String branch) {
        super(repoUrl, "-", "-");
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

}
