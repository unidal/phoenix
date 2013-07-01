package com.dianping.maven.plugin.phoenix;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.phoenix.MojoDataWebUI.DataTransmitter;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;

public class UICreator {
	
	@Inject
	WorkspaceFacade facade;

	public DataTransmitter<Workspace, Workspace> createUI(Workspace model, String displayUri) throws Exception,
			MalformedURLException {
		DataTransmitter<Workspace, Workspace> dataTransmitter = new DataTransmitter<Workspace, Workspace>(model);
		Map<String, BaseMojoDataServlet<Workspace, Workspace>> servletMapping = new HashMap<String, BaseMojoDataServlet<Workspace, Workspace>>();
		servletMapping.put("/req/*", new DefaultMojoDataServlet(dataTransmitter, facade, "/req/"));

		MojoDataWebUI<Workspace, Workspace> webUI = new MojoDataWebUI<Workspace, Workspace>(servletMapping);
		webUI.start();
		webUI.display(displayUri);
		return dataTransmitter;
	}

}
