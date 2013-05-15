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
package com.dianping.maven.plugin.tools.wms;

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
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shop/trunk/shop-web/", "peng.hu",
                    "qweasd", -1l);
        } else if ("user-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/user/trunk/user-web/", "peng.hu",
                    "qweasd", -1l);
        } else if ("shoplist-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shop/trunk/shoplist-web/",
                    "peng.hu", "qweasd", -1l);
        } else if ("dpindex-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/dpindex/trunk/dpindex-web/",
                    "peng.hu", "qweasd", -1l);
        } else if ("user-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/user/trunk/user-service/", "peng.hu",
                    "qweasd", -1l);
        } else if ("user-base-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/user/trunk/user-base-service/", "peng.hu",
                    "qweasd", -1l);
        } else {
            return null;
        }
    }

}
