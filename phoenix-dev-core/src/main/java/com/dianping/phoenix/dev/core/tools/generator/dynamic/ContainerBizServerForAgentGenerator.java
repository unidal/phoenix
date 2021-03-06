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
package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.util.Map;

import com.dianping.phoenix.dev.core.tools.generator.TemplateBasedFileGenerator;

/**
 * @author Leo Liang
 * 
 */
public class ContainerBizServerForAgentGenerator extends TemplateBasedFileGenerator<Map<String, String>> {
    private static final String TEMPLATE = "container-bizserverforagentjava.vm";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.misc.file.TemplateBasedFileGenerator#getTemplate()
     */
    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.misc.file.TemplateBasedFileGenerator#getArgs(java
     * .lang.Object)
     */
    @Override
    protected Object getArgs(Map<String, String> context) {
        return context;
    }

}
