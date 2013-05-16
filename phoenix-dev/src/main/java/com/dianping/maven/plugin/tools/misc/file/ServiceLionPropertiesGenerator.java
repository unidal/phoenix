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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.dianping.maven.plugin.tools.misc.scanner.ServiceMetaScanner;
import com.dianping.maven.plugin.tools.misc.scanner.ServicePortEntry;
import com.dianping.maven.plugin.tools.misc.scanner.Scanner;
import com.dianping.maven.plugin.tools.misc.scanner.ServiceScanner;

/**
 * @author Leo Liang
 * 
 */
public class ServiceLionPropertiesGenerator {
    private static final String SERVICE_META_FILE = "service-port.xml";

    public void generate(File file, ServiceLionContext context) throws Exception {

        File serviceMetaOutput = new File(file.getParentFile(), SERVICE_META_FILE);

        if (context.isRefreshServiceMeta()) {
            ServiceMetaGenerator serviceMetaGenerator = new ServiceMetaGenerator();
            serviceMetaGenerator.generate(serviceMetaOutput, context.getServiceMetaContext());
        }

        Scanner<ServicePortEntry> serviceMetaScanner = new ServiceMetaScanner();
        List<ServicePortEntry> servicePortList = serviceMetaScanner.scan(serviceMetaOutput);
        Map<String, Integer> servicePortMapping = new HashMap<String, Integer>();
        for (ServicePortEntry entry : servicePortList) {
            servicePortMapping.put(entry.getService(), entry.getPort());
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
                        context.getServiceHost() + ":" + servicePortMapping.get(serviceKey));
            }

        }

        BytemanScriptGenerator bytemanScriptGenerator = new BytemanScriptGenerator();
        bytemanScriptGenerator.generate(file, serviceLionContents);

    }

    public static void main(String[] args) throws Exception {
        ServiceLionPropertiesGenerator serviceLionPropertiesGenerator = new ServiceLionPropertiesGenerator();

        Map<String, File> projectBaseDirMapping = new HashMap<String, File>();
        projectBaseDirMapping.put("ssss", new File("/Volumes/HDD/dev_env_work/war/alpaca.war"));
        ServiceMetaContext projectMetaContext = new ServiceMetaContext("com.mysql.jdbc.Driver",
                "jdbc:mysql://192.168.7.105:3306/hawk", "dpcom_hawk", "123456");
        serviceLionPropertiesGenerator.generate(new File("/Users/leoleung/phoenix-test/phoenix-lion.btm"),
                new ServiceLionContext(projectBaseDirMapping, projectMetaContext, "127.0.0.1", true));
    }

}
