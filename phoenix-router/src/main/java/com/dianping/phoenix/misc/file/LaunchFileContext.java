/**
 * Project: phoenix-router
 * 
 * File Created at 2013-4-15
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
package com.dianping.phoenix.misc.file;

import java.io.File;

public class LaunchFileContext {
    private String mainClass;
    private File   btmFile;

    public LaunchFileContext(String mainClass, File btmFile) {
        this.mainClass = mainClass;
        this.btmFile = btmFile;
    }

    public String getMainClass() {
        return mainClass;
    }

    public File getBtmFile() {
        return btmFile;
    }

}
