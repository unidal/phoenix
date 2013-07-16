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
import java.io.OutputStream;
import java.util.List;

import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;

/**
 * @author Leo Liang
 * 
 */
public interface WorkspaceService {

    public void create(Workspace model, OutputStream out) throws Exception;

    public void modify(Workspace model, OutputStream out) throws Exception;

	public void pullConfig(File wsDir);

	public List<String> getProjectListByPattern(String pattern);
}
