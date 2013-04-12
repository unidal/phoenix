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
package com.dianping.phoenix.misc.file;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * @author Leo Liang
 * 
 */
public class BytemanScriptGenerator {
    private static final VelocityEngine _ve;
    private static final String         TEMPLATE = "byteman.vm";
    static {
        _ve = new VelocityEngine();
        _ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        _ve.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
        _ve.setProperty("class.resource.loader.cache", true);
        _ve.setProperty("class.resource.loader.modificationCheckInterval", "-1");
        _ve.setProperty("input.encoding", "UTF-8");
        _ve.setProperty("runtime.log", "/tmp/velocity.log");
        _ve.init();
    }

    public void generate(File file, Map<String, String> args) throws IOException {
        String content = buildContent(args);
        FileUtils.writeStringToFile(file, content);
    }

    private String buildContent(Map<String, String> args) {
        Template t = _ve.getTemplate(TEMPLATE);
        VelocityContext context = new VelocityContext();
        context.put("lionConfig", args);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

}
