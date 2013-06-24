/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-6-23
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
package com.dianping.maven.plugin.phoenix;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.maven.plugin.phoenix.MojoDataWebUI.DataTransmitter;
import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;

/**
 * @author Leo Liang
 * 
 */
public class DefaultMojoDataServlet extends BaseMojoDataServlet<Workspace, Workspace> {
    private static final long   serialVersionUID      = -4489369742174754185L;

    private static final String PARAM_PROJECTS_REMOVE = "projectsRemove";
    private static final String PARAM_PROJECTS_ADD    = "projectsAdd";

    private static final String URI_CREATE_WORKSPACE  = "createWorkspace";
    private static final String URI_MODIFY_WORKSPACE  = "modifyWorkspace";
    private static final String URI_LIST_PROJECTS     = "listProjects";
    private static final String URI_WORKSPACE_META    = "workspaceMeta";

    private static final String SPLITOR               = ",";
    private WorkspaceFacade     workspaceFacade;
    private String              baseUri;

    public DefaultMojoDataServlet(DataTransmitter<Workspace, Workspace> dataTransmitter,
            WorkspaceFacade workspaceFacade, String baseUri) {
        super(dataTransmitter);
        this.workspaceFacade = workspaceFacade;
        this.baseUri = baseUri;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uri = req.getRequestURI();
            if (uri.startsWith(baseUri)) {
                String newUri = uri.substring(baseUri.length());
                Map<String, Object> resData = new HashMap<String, Object>();
                if (URI_LIST_PROJECTS.equals(newUri)) {
                    resData.put("projects", workspaceFacade.listProjects());
                    respSuccess(resp, resData);
                } else if (URI_CREATE_WORKSPACE.equals(newUri)) {
                    createWorkspace(req, resp);
                } else if (URI_MODIFY_WORKSPACE.equals(newUri)) {
                    modifyWorkspace(req, resp);
                } else if (URI_WORKSPACE_META.equals(newUri)) {
                    resData.put("workspace", dataTransmitter.getInitData());
                    respSuccess(resp, resData);
                } else {
                    respError(resp, String.format("Can not handle uri(%s)", uri));
                }
            } else {
                respError(resp, String.format("Req uri not start with %s(req uri = %s)", baseUri, uri));
            }
        } catch (Exception e) {
            respError(resp, String.format("Unknow error(%s)", e.getMessage()));
        }
    }

    private void createWorkspace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String projectsAddStr = req.getParameter(PARAM_PROJECTS_ADD);

        if (StringUtils.isNotBlank(projectsAddStr)) {
            dataTransmitter.returnResult(buildWorkSpace(Arrays.asList(projectsAddStr.split(SPLITOR)), null));
        } else {
            respError(resp, "ProjectsAdd must not be null/empty.");
        }
    }

    private void modifyWorkspace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String projectsAddStr = req.getParameter(PARAM_PROJECTS_ADD);
        String projectsRemoveStr = req.getParameter(PARAM_PROJECTS_REMOVE);

        dataTransmitter.returnResult(buildWorkSpace(
                StringUtils.isNotBlank(projectsAddStr) ? Arrays.asList(projectsAddStr.split(SPLITOR)) : null,
                StringUtils.isNotBlank(projectsRemoveStr) ? Arrays.asList(projectsRemoveStr.split(SPLITOR)) : null));
    }

    private Workspace buildWorkSpace(List<String> projectsAdd, List<String> projectsRemove) {
        Workspace rawWorkspace = dataTransmitter.getInitData();
        Workspace newWorkspace = new Workspace();
        newWorkspace.setPhoenixProject(rawWorkspace.getPhoenixProject());
        newWorkspace.setDir(rawWorkspace.getDir());

        Set<String> projectNames = new HashSet<String>();

        if (rawWorkspace.getBizProjects() != null && !rawWorkspace.getBizProjects().isEmpty()) {
            for (BizProject project : rawWorkspace.getBizProjects()) {
                projectNames.add(project.getName());
            }
        }

        if (projectsAdd != null && !projectsAdd.isEmpty()) {
            for (String project : projectsAdd) {
                projectNames.add(project);
            }
        }

        if (projectsRemove != null && !projectsRemove.isEmpty()) {
            for (String project : projectsRemove) {
                projectNames.remove(project);
            }
        }

        for (String name : projectNames) {
            BizProject bizProject = new BizProject();
            bizProject.setName(name);
            newWorkspace.addBizProject(bizProject);
        }

        return newWorkspace;
    }
}
