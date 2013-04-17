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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.dianping.phoenix.misc.scanner.ProjectMetaScanner;
import com.dianping.phoenix.misc.scanner.ProjectPortEntry;
import com.dianping.phoenix.misc.scanner.Scanner;
import com.dianping.phoenix.misc.scanner.ServiceScanner;

/**
 * @author Leo Liang
 * 
 */
public class ServiceLionPropertiesGenerator {
    private static final String SERVICE_META_FILE = "service-port.xml";
    private static final String OUTPUT            = "phoenix-lion.btm";

    @SuppressWarnings("unchecked")
    protected void generate(ServiceLionContext context) throws Exception {

        File projectMetaOutput = new File(context.getOutputBaseDir(), SERVICE_META_FILE);

        if (context.isRefreshProjectMeta()) {
            ProjectMetaGenerator projectMetaGenerator = new ProjectMetaGenerator();
            projectMetaGenerator.generate(projectMetaOutput, context.getProjectMetaContext());
        }

        Scanner<ProjectPortEntry> projectMetaScanner = new ProjectMetaScanner();
        List<ProjectPortEntry> projectPortsList = projectMetaScanner.scan(projectMetaOutput);
        Map<String, Integer> projectPortMapping = new HashMap<String, Integer>();
        for (ProjectPortEntry entry : projectPortsList) {
            projectPortMapping.put(entry.getProject(), entry.getPort());
        }

        Map<String, String> serviceLionContents = new HashMap<String, String>();

        Scanner<String> serviceScanner = new ServiceScanner();
        for (Map.Entry<String, File> entry : context.getProjectBaseDirMapping().entrySet()) {
            Collection<File> allXmlFiles = FileUtils.listFiles(entry.getValue(), new String[] { "xml" }, true);

            List<String> serviceKeys = new ArrayList<String>();

            for (File xml : allXmlFiles) {
                serviceKeys.addAll(serviceScanner.scan(xml));
            }

            for (String serviceKey : serviceKeys) {
                serviceLionContents.put(serviceKey,
                        context.getServiceHost() + ":" + projectPortMapping.get(entry.getKey()));
            }

        }

        BytemanScriptGenerator bytemanScriptGenerator = new BytemanScriptGenerator();
        bytemanScriptGenerator.generate(new File(context.getOutputBaseDir(), OUTPUT), serviceLionContents);

    }

    public static void main(String[] args) throws Exception {
        ServiceLionPropertiesGenerator serviceLionPropertiesGenerator = new ServiceLionPropertiesGenerator();

        Map<String, File> projectBaseDirMapping = new HashMap<String, File>();
        projectBaseDirMapping.put("alpaca", new File("/Volumes/HDD/dev_env_work/war/alpaca.war"));
        ProjectMetaContext projectMetaContext = new ProjectMetaContext("com.mysql.jdbc.Driver",
                "jdbc:mysql://192.168.7.105:3306/hawk", "dpcom_hawk", "123456");
        serviceLionPropertiesGenerator.generate(new ServiceLionContext(projectBaseDirMapping, projectMetaContext,
                "127.0.0.1", new File("/Users/leoleung/phoenix-test"), true));
    }

}
