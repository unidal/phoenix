/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-5-15
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo Liang
 * 
 */
public class DummyRepositoryManager implements RepositoryManager {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.maven.plugin.tools.wms.RepositoryManager#find(java.lang.
     * String)
     */
    @Override
    public Repository find(String project) {
        if ("shop-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/shop-web/",
                    "-", "-", -1l);
        } else if ("user-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/user-web/",
                    "-", "-", -1l);
        } else if ("shoplist-web".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/shoplist-web/", "-", "-",
                    -1l);
        } else if ("dpindex-web".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/dpindex-web/", "-", "-", -1l);
        } else if ("shoppic-web".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/shoppic-web/", "-", "-", -1l);
        } else if ("account-web".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/account-web/", "-", "-", -1l);
        } else if ("shopsearch-web".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/shopsearch-web/", "-", "-",
                    -1l);
        } else if ("group-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/groupweb/",
                    "-", "-", -1l);
        } else if ("groupback-web".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/groupbackweb/", "-", "-",
                    -1l);
        } else if ("user-service".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/user-service/", "-", "-",
                    -1l);
        } else if ("user-base-service".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/user-base-service/", "-",
                    "-", -1l);
        } else if ("shoppic-service".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/shoppic-service/", "-", "-",
                    -1l);
        } else if ("group-service".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/groupservice/", "-", "-",
                    -1l);
        } else if ("groupback-service".equals(project)) {
            return new SvnRepository(
                    "http://192.168.8.45:81/svn/dianping/platform/middleware/trunk/phoenix/groupbackservice/", "-",
                    "-", -1l);
        } else if ("phoenix-maven-config".equals(project)) {
            return new GitRepository("http://code.dianpingoa.com/arch/phoenix-maven-config.git");
        } else {
            return null;
        }
    }

    @Override
    public List<String> getProjectListByPattern(String pattern) {
        ArrayList<String> projectList = new ArrayList<String>();
        projectList.add("user-web");
        return projectList;
    }

}
