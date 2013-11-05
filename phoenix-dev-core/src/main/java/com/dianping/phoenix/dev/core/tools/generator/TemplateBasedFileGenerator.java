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
package com.dianping.phoenix.dev.core.tools.generator;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.dianping.phoenix.dev.core.tools.velocity.PhoenixResourceLoader;

/**
 * @author Leo Liang
 * 
 */
public abstract class TemplateBasedFileGenerator<T> implements FileGenerator<T> {

    private static final VelocityEngine _ve;
    static {
        _ve = new VelocityEngine();
        _ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        _ve.setProperty("class.resource.loader.class", PhoenixResourceLoader.class.getName());
        _ve.setProperty("class.resource.loader.cache", true);
        _ve.setProperty("class.resource.loader.modificationCheckInterval", "-1");
        _ve.setProperty("input.encoding", "UTF-8");
        _ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        _ve.init();
    }

    public void generate(File file, T context) throws Exception {
        FileUtils.writeStringToFile(file, buildContent(context));
    }

    protected abstract String getTemplate();

    protected abstract Object getArgs(T context) throws Exception;

    protected String buildContent(T context) throws Exception {
        Template t = _ve.getTemplate(getTemplate());
        VelocityContext vcontext = new VelocityContext();
        vcontext.put("args", getArgs(context));
        StringWriter writer = new StringWriter();
        t.merge(vcontext, writer);
        return writer.toString();
    }
}
