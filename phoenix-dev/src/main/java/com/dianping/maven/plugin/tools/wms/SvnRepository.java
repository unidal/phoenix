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
 * 
 * @author Leo Liang
 * 
 */
public class SvnRepository extends Repository {
    private long revision = -1L;


    public SvnRepository() {
    }

    public SvnRepository(String repoUrl, String user, String pwd, long revision) {
        super(repoUrl, user, pwd);
        this.revision = revision;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

}
