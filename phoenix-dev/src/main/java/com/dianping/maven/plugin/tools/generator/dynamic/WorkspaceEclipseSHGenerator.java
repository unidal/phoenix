/**
 * Project: phoenix-router
 * 
 * File Created at 2013-4-12
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
package com.dianping.maven.plugin.tools.generator.dynamic;

import java.util.List;

import com.dianping.maven.plugin.tools.generator.TemplateBasedFileGenerator;

/**
 * @author Leo Liang
 * 
 */
public class WorkspaceEclipseSHGenerator extends TemplateBasedFileGenerator<List<String>> {
    private static final String TEMPLATE = "workspace-eclipsesh.vm";

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    @Override
    protected Object getArgs(List<String> context) {
        return context;
    }

}
