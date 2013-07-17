/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-5-14
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
package com.dianping.phoenix.dev.core.tools.wms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerWebXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceStartSHGenerator;

/**
 * 
 * @author Leo Liang
 * 
 */
public class AgentWorkspaceServiceImpl extends AbstractWorkspaceService {

	private void checkoutSource(WorkspaceContext context, OutputStream out) throws Exception {
		for (BizProject project : context.getProjects()) {
			project.setName(getFromUrl(project.getName(), context.getBaseDir(), out));
		}
	}

	public String getFromUrl(String url, File baseDir, OutputStream out) throws Exception {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		String fileName = extractFileName(url);
		BufferedInputStream is = null;
		BufferedOutputStream os = null;
		try {
			is = new BufferedInputStream(new URL(url).openStream());
			os = new BufferedOutputStream(new FileOutputStream(new File(baseDir, fileName)));
			IOUtils.copyLarge(is, os);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}

		ZipUtil.explode(new File(baseDir, fileName));

		return fileName;
	}

	private String extractFileName(String url) {
		String name = url;
		int pos = name.lastIndexOf("/");
		if (pos >= 0) {
			name = name.substring(pos + 1);
		}

		pos = -1;

		String[] envPatterns = new String[] { "-qa-", "-dev-", "-alpha-", "-product-" };

		for (String pat : envPatterns) {
			pos = name.indexOf(pat);
			if (pos >= 0) {
				name = name.substring(0, pos);
				break;
			}
		}

		return name;
	}

	@Override
	protected void generateWorkspaceMisc(WorkspaceContext context) throws Exception {
		WorkspaceStartSHGenerator workspaceStartSHGenerator = new WorkspaceStartSHGenerator();
		File startSh = new File(context.getBaseDir(), "start.sh");
		workspaceStartSHGenerator.generate(startSh, new ArrayList<String>());
		startSh.setExecutable(true);
	}

	@Override
	protected void generateContainer(WorkspaceContext context, OutputStream out) throws Exception {
		
		checkoutSource(context, out);
		
		File warBase = new File(context.getBaseDir(), WorkspaceConstants.PHOENIX_CONTAINER_FOLDER);
		File webInfFolder = new File(warBase, "WEB-INF");
		File phoenixServerFolder = new File(webInfFolder, "classes/com/dianping/phoenix/container/");
		FileUtils.forceMkdir(webInfFolder);
		FileUtils.forceMkdir(phoenixServerFolder);

		String libZipName = "lib.zip";
		copyFile(libZipName, webInfFolder);
		ZipUtil.unpack(new File(webInfFolder, libZipName), webInfFolder);
		FileUtils.deleteQuietly(new File(webInfFolder, libZipName));

		ContainerWebXMLGenerator containerWebXMLGenerator = new ContainerWebXMLGenerator();
		containerWebXMLGenerator.generate(new File(webInfFolder, "web.xml"), null);

		String serverClassFileName = "AgentPhoenixServer.classfile";
		copyFile(serverClassFileName, phoenixServerFolder);
		FileUtils.moveFile(new File(phoenixServerFolder, serverClassFileName), new File(phoenixServerFolder,
				"PhoenixServer.class"));

	}

	@Override
	protected File resourceFileFor(File rootDir, String fileName) {
		return new File(rootDir, WorkspaceConstants.PHOENIX_CONTAINER_WAR_CLASSES_FOLDER + fileName);
	}

}
