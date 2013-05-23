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
package com.dianping.maven.plugin.tools.generator.stable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dianping.maven.plugin.tools.generator.TemplateBasedFileGenerator;

/**
 * @author Leo Liang
 * 
 */
public class ServiceMetaGenerator extends TemplateBasedFileGenerator<ServiceMetaContext> {
    private static final String TEMPLATE = "service-meta.vm";

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
    protected Object getArgs(ServiceMetaContext context) throws Exception {
        Map<String, Integer> servicePortMapping = new LinkedHashMap<String, Integer>();

        Class.forName(context.getDriverClass());

        Connection conn = null;
        Statement stmt = null;
        try {
            if (StringUtils.isNotBlank(context.getUser())) {
                conn = DriverManager.getConnection(context.getUrl(), context.getUser(), context.getPwd());
            } else {
                conn = DriverManager.getConnection(context.getUrl());
            }
            stmt = conn.createStatement();
            ResultSet rs = stmt
                    .executeQuery("SELECT s.serviceName AS serviceName, h.port1 AS port FROM jrobin_host h, service s WHERE s.projectId = h.projectId AND h.port1 IS NOT NULL ORDER BY PORT ASC;");
            while (rs.next()) {
                String serviceName = rs.getString("serviceName");
                int port = rs.getInt("port");
                if (StringUtils.isNotBlank(serviceName)) {
                    servicePortMapping.put(serviceName, port);
                }
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {

                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }

        return servicePortMapping;

    }

    public static void main(String[] args) throws Exception {
        ServiceMetaGenerator serviceMetaGenerator = new ServiceMetaGenerator();
        serviceMetaGenerator.generate(new File("/Users/leoleung/service-meta.xml"), new ServiceMetaContext(
                "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.7.105:3306/hawk", "dpcom_hawk", "123456"));
    }
}
