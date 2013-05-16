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
package com.dianping.maven.plugin.tools.misc.file;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * 
 * @author Leo Liang
 * 
 */
public class ProjectMetaGeneratorTest extends H2DBBasedTestCase {
    private File file = new File(System.getProperty("java.io.tmpdir", "."), "service-meta.xml");

    @Test
    public void test() throws Exception {
        String expected = "<services>\n"
                        + "    <service name=\"http://service.dianping.com/shoppicService/shoppicService_1.0.0\">\n"
                        + "        <port>2000</port>\n" 
                        + "    </service>\n" 
                        + "    <service name=\"http://service.dianping.com/groupService/groupService_1.0.0\">\n" 
                        + "        <port>2001</port>\n" 
                        + "    </service>\n" 
                        + "    <service name=\"http://service.dianping.com/accountService/accountService_1.0.0\">\n" 
                        + "        <port>2002</port>\n" 
                        + "    </service>\n" 
                        + "    <service name=\"http://service.dianping.com/userService/userService_1.0.0\">\n" 
                        + "        <port>2003</port>\n" 
                        + "    </service>\n"
                        + "</services>";
        ServiceMetaContext context = new ServiceMetaContext("org.h2.Driver",
                "jdbc:h2:mem:hawk;DB_CLOSE_DELAY=-1", "", "");
        ServiceMetaGenerator smg = new ServiceMetaGenerator();
        smg.generate(file, context);
        Assert.assertEquals(expected, FileUtils.readFileToString(file));
    }

    protected String getDBBaseUrl() {
        return "jdbc:h2:mem:";
    }

    protected String getCreateScriptConfigFile() {
        return "service-meta-generator-test-create.xml";
    }

    protected String getDataFile() {
        return "service-meta-generator-test-data.xml";
    }

    public static void main(String[] args) throws Exception {
        ServiceMetaContext context = new ServiceMetaContext("com.mysql.jdbc.Driver",
                "jdbc:mysql://192.168.7.105:3306/hawk", "dpcom_hawk", "123456");
        ServiceMetaGenerator smg = new ServiceMetaGenerator();
        smg.generate(new File("/Users/leoleung/project-port.xml"), context);
    }
}
