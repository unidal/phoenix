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
package com.dianping.phoenix.dev.core.tools.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dianping.phoenix.dev.core.tools.generator.BytemanScriptGenerator;

/**
 * @author Leo Liang
 * 
 */
public class BytemanScriptGeneratorTest {
    private File file = new File(System.getProperty("java.io.tmpdir", "."), "lion.btm");

    @Before
    public void before() {
        FileUtils.deleteQuietly(file);
    }

    @After
    public void after() {
        FileUtils.deleteQuietly(file);
    }

    @Test
    public void test() throws Exception {
        String expected = "# usage:\n"
+"# -javaagent:{BYTEMAN_HOME}/lib/byteman.jar=script:{BTM_HOME}/{BTM_NAME},boot:{BYTEMAN_HOME}/lib/byteman.jar -Dorg.jboss.byteman.transform.all\n"
+"#\n"
+"RULE lion change\n"
+"CLASS com.dianping.lion.client.InitializeConfig\n"
+"METHOD postProcessBeanFactory\n"
+"HELPER com.dianping.phoenix.router.byteman.ServiceLionBytemanDataLoader\n"
+"AT INVOKE setPts\n"
+"IF true\n"
+"DO    \n"
+"    $this.pts.putAll(loadServices(\"^router-.*\\.properties$\"));\n"
+"    System.out.println(\"Phoenix runtime config modification complete...\")\n"
+"ENDRULE\n"
+"\n"
+"RULE TemplateUtils\n"
+"CLASS com.dianping.w3c.pagelet.template.freemarker.TemplateUtils\n"
+"METHOD freemarker(Template ,String, Map)\n"
+"HELPER com.dianping.phoenix.router.remedy.TemplateUtilsRemedy\n"
+"AT ENTRY\n"
+"IF true\n"
+"DO\n"
+"    $2=getWebappDir($1);\n"
+"ENDRULE";
        Map<String, String> args = new HashMap<String, String>();
        BytemanScriptGenerator bsg = new BytemanScriptGenerator();
        bsg.generate(file, args);
        Assert.assertEquals(expected, FileUtils.readFileToString(file));
    }
}
