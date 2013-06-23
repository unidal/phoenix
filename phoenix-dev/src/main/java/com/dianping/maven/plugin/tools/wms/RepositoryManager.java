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

import java.util.List;

/**
 * @author Leo Liang
 * 
 */
public interface RepositoryManager {

    Repository find(String project);

    List<String> getProjectListByPrefix(String prefix);

    List<String> getProjectList();

}
