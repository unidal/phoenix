/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-6-6
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
package com.dianping.maven.plugin.tools.vcs;

import java.io.File;
import java.io.OutputStream;

/**
 * @author Leo Liang
 * 
 */
public interface RepositoryService {
    public void checkout(String project, File outputFolder, OutputStream logOutput)
            throws RepositoryNotFoundException;
}
