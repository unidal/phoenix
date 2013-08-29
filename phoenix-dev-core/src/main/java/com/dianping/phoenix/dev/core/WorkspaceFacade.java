package com.dianping.phoenix.dev.core;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;

import com.dianping.phoenix.dev.core.configure.Whiteboard;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.model.workspace.transform.DefaultSaxParser;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceConstants;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceService;

public class WorkspaceFacade extends ContainerHolder implements Initializable {

	private WorkspaceService pluginWms;
	private WorkspaceService agentWms;
	
	private WorkspaceService chooseWms(String from) {
		if(WorkspaceConstants.FROM_AGENT.equalsIgnoreCase(from)) {
			return agentWms;
		}
		return pluginWms;
	}

	public void init(File wsDir) {
		chooseWms("default").pullConfig(wsDir);
		Whiteboard.INSTANCE.workspaceInitialized(wsDir);
	}

	public List<String> getProjectListByPattern(String pattern) {
		return chooseWms("default").getProjectListByPattern(pattern);
	}

	public void create(Workspace model) throws Exception {
		workspaceChange(model, false);
	}

	public Workspace current(File dir) throws Exception {
		File metaFile = new File(dir, WorkspaceConstants.WORKSPACE_META_FILENAME);
		if (metaFile.exists() && metaFile.isFile()) {
			return DefaultSaxParser.parse(FileUtils.readFileToString(metaFile));
		}
		return null;
	}

	public void modify(Workspace model) throws Exception {
		workspaceChange(model, true);
	}

	private void workspaceChange(Workspace model, boolean modify) throws Exception {
		FileUtils.forceMkdir(new File(model.getDir(), WorkspaceConstants.PHOENIX_ROOT_FOLDER));
		WorkspaceService wms = chooseWms(model.getFrom());
		
		if (modify) {
			wms.modify(model, System.out);
		} else {
			wms.create(model, System.out);
		}
		saveMeta(model);
		FileUtils.touch(new File(model.getDir(), WorkspaceConstants.REINIT_SIG_FILENAME));
	}

	private void saveMeta(Workspace model) throws Exception {
		FileUtils.writeStringToFile(new File(model.getDir(), WorkspaceConstants.WORKSPACE_META_FILENAME),
				model.toString(), "utf-8");
	}

	public Workspace buildDefaultSkeletoModel() {
		InputStream defaultWorkspaceXml = this.getClass().getResourceAsStream("/workspace-default.xml");
		Workspace model;
		try {
			model = DefaultSaxParser.parse(defaultWorkspaceXml);
		} catch (Exception e) {
			throw new RuntimeException("error read workspace-default.xml", e);
		}
		return model;
	}

	@Override
	public void initialize() throws InitializationException {
		agentWms = lookup(WorkspaceService.class, "Agent");
		pluginWms = lookup(WorkspaceService.class, "Plugin");
	}
}
