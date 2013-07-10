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

/**
 * @author Leo Liang
 * 
 */
public interface WorkspaceManagementService {

    public File create(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException;

    public File modify(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException;
}
