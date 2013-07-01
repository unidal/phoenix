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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.xml.sax.SAXException;

import com.dianping.maven.plugin.phoenix.MojoDataWebUI.DataTransmitter;
import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.transform.DefaultSaxParser;

/**
 * @author Leo Liang
 * 
 */
public class DefaultMojoDataServlet extends BaseMojoDataServlet<Workspace, Workspace> {

	private static Logger log = Logger.getLogger(DefaultMojoDataServlet.class);

	private static final long serialVersionUID = -4489369742174754185L;

	private static final String PARAM_PROJECTS = "projects";
	private static final String PARAM_PROJECTNAME_PATTERN = "projectNamePattern";

	private static final String URI_SUBMIT_WORKSPACE = "submitWorkspace";
	private static final String URI_LIST_PROJECTS = "listProjects";
	private static final String URI_WORKSPACE_META = "workspaceMeta";

	private static final String SPLITOR = ",";
	private WorkspaceFacade workspaceFacade;
	private String baseUri;

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
					String pattern = req.getParameter(PARAM_PROJECTNAME_PATTERN);
					if (StringUtils.isNotBlank(pattern)) {
						resData.put("projects", workspaceFacade.getProjectListByPattern(pattern.trim()));
					} else {
						resData.put("projects", Collections.EMPTY_LIST);
					}
					respSuccess(resp, resData);
				} else if (URI_SUBMIT_WORKSPACE.equals(newUri)) {
					submitWorkspace(req, resp);
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
			log.error("", e);
			respError(resp, String.format("Unknow error(%s)", e.getMessage()));
		}
	}

	private void submitWorkspace(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String projects = req.getParameter(PARAM_PROJECTS);

		if (StringUtils.isNotBlank(projects)) {
			dataTransmitter.returnResult(buildWorkSpace(Arrays.asList(projects.split(SPLITOR))));
		} else {
			respError(resp, "Projects must not be null/empty.");
		}
	}

	private Workspace buildWorkSpace(List<String> projects) throws Exception {
		Workspace rawWorkspace = dataTransmitter.getInitData();
		Workspace newWorkspace = cloneWorkspace(rawWorkspace);

		newWorkspace.getBizProjects().clear();

		if (projects == null || projects.isEmpty()) {
			return newWorkspace;
		}

		for (String name : projects) {
			BizProject bizProject = new BizProject();
			bizProject.setName(name);
			newWorkspace.addBizProject(bizProject);
		}

		return newWorkspace;
	}

	private Workspace cloneWorkspace(Workspace rawWorkspace) throws SAXException, IOException {
		Workspace newWorkspace = DefaultSaxParser.parse(rawWorkspace.toString());
		return newWorkspace;
	}
}
