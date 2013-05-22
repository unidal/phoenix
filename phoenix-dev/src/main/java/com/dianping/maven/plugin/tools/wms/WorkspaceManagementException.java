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

/**
 * @author Leo Liang
 * 
 */
public class WorkspaceManagementException extends Exception {

    private static final long serialVersionUID = -652464443921355874L;

    public WorkspaceManagementException() {
        super();
    }

    public WorkspaceManagementException(String message) {
        super(message);
    }

    public WorkspaceManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkspaceManagementException(Throwable cause) {
        super(cause);
    }

}
