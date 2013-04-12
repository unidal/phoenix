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
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        String expected = "RULE lion change\n"
                + "CLASS com.dianping.lion.client.InitializeConfig\n"
                + "METHOD postProcessBeanFactory\n"
                + "AT INVOKE setPts\n"
                + "IF true\n"
                + "DO    $this.pts.put(\"http://service.dianping.com/hawk/alarm/commonAlarmSerivce\", \"192.168.8.2:1234\");\n"
                + "      $this.pts.put(\"alpaca.url\", \"http://www.dianping.com\");\n"
                + "      System.out.println(\"Phoenix runtime config modification complete...\")\n" + "ENDRULE";
        Map<String, String> args = new HashMap<String, String>();
        args.put("http://service.dianping.com/hawk/alarm/commonAlarmSerivce", "192.168.8.2:1234");
        args.put("alpaca.url", "http://www.dianping.com");
        BytemanScriptGenerator bsg = new BytemanScriptGenerator();
        bsg.generate(file, args);
        Assert.assertEquals(expected, FileUtils.readFileToString(file));
    }
}
