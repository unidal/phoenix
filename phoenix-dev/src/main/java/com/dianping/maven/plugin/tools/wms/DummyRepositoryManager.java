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

    @Override
    public Repository find(String project) {
        if ("shop-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shop/trunk/shop-web/", "-", "-", -1l);
        } else if ("user-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/user/trunk/user-web/", "-", "-", -1l);
        } else if ("shoplist-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shop/trunk/shoplist-web/", "-", "-",
                    -1l);
        } else if ("dpindex-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/dpindex/trunk/dpindex-web/", "-",
                    "-", -1l);
        } else if ("shoppic-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shoppic/trunk/shoppic-web/", "-",
                    "-", -1l);
        } else if ("account-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/account/trunk/account-web/", "-",
                    "-", -1l);
        } else if ("shopsearch-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shopsearch/trunk/shopsearch-web/",
                    "-", "-", -1l);
        } else if ("group-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/group/trunk/groupweb/", "-", "-",
                    -1l);
        } else if ("groupback-web".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/group/trunk/groupbackweb/", "-",
                    "-", -1l);
        } else if ("user-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/user/trunk/user-service/", "-", "-",
                    -1l);
        } else if ("user-base-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/user/trunk/user-base-service/", "-",
                    "-", -1l);
        } else if ("shoppic-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/shoppic/trunk/shoppic-service/",
                    "-", "-", -1l);
        } else if ("other-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/other-service/trunk/", "-", "-", -1l);
        } else if ("group-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/group/trunk/groupservice/", "-",
                    "-", -1l);
        } else if ("kangaroo-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/group/trunk/kangaroo/", "-", "-",
                    -1l);
        } else if ("groupback-service".equals(project)) {
            return new SvnRepository("http://192.168.8.45:81/svn/dianping/dianping/group/trunk/groupbackservice/", "-",
                    "-", -1l);
        } else {
            return null;
        }
    }

}
