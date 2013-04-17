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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Leo Liang
 * 
 */
public class LaunchFileGenerator extends TemplateBasedFileGenerator<LaunchFileContext> {

    @Override
    protected String getTemplate() {
        return "launch.vm";
    }

    @Override
    protected Object getArgs(LaunchFileContext context) throws Exception {
        Map<String, String> res = new HashMap<String, String>();
        res.put("btmFile", context.getBtmFile().getAbsolutePath());
        res.put("mainClass", context.getMainClass());
        return res;
    }

    public static void main(String[] args) throws Exception {
        LaunchFileGenerator lfg = new LaunchFileGenerator();
        
        LaunchFileContext context = new LaunchFileContext("com.dianping.phoenix.BizServer", new File(
                "/Users/marsqing/Projects/tmp/phoenix-lion.btm"));
        
        lfg.generate(new File("/Users/marsqing/Projects/phoenix/phoenix-router", "phoenix.launch"), context);
    }
}
