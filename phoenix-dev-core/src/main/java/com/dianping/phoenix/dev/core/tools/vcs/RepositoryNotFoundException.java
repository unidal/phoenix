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
package com.dianping.phoenix.dev.core.tools.vcs;

/**
 * @author Leo Liang
 * 
 */
public class RepositoryNotFoundException extends Exception {
    private static final long serialVersionUID = 6883598741851828538L;

    public RepositoryNotFoundException() {
        super();
    }

    public RepositoryNotFoundException(String message) {
        super(message);
    }

    public RepositoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryNotFoundException(Throwable cause) {
        super(cause);
    }
}
