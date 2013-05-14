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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author Leo Liang
 * 
 */
public class ProjectMetaGenerator extends TemplateBasedFileGenerator<ProjectMetaContext> {
    private static final String TEMPLATE = "project-meta.vm";

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
    protected Object getArgs(ProjectMetaContext context) throws Exception {
        Map<String, Integer> projectPortMapping = new LinkedHashMap<String, Integer>();

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
                    .executeQuery("SELECT DISTINCT p.id AS id, p.name AS project, j.port1 AS port FROM jrobin_host j, project p WHERE j.projectId=p.id AND j.port1 IS NOT NULL ORDER BY port ASC");
            while (rs.next()) {
                String project = rs.getString("project");
                int port = rs.getInt("port");
                if (StringUtils.isNotBlank(project)) {
                    projectPortMapping.put(project, port);
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

        return projectPortMapping;

    }

}
