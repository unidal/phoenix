package com.dianping.maven.plugins;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.maven.plugins.web.MojoDataWebUI.DataTransmitter;
import com.dianping.maven.plugins.web.UICreator;
import com.dianping.phoenix.dev.core.WorkspaceFacade;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.model.workspace.transform.DefaultSaxParser;

public class TestServer extends ComponentTestCase {
	
	UICreator uiCreator;
	WorkspaceFacade facade;
	
	@Before
	public void before() throws Exception {
		uiCreator = lookup(UICreator.class);
		facade = lookup(WorkspaceFacade.class);
	}
	
	@Test
	public void testServer() throws Exception {
		String dir = "/Users/marsqing/Projects/tmp/phoenix-maven-tmp";
		File existingWorkspaceXml = new File(dir, "phoenix/meta/workspace.xml");
		Workspace model = new Workspace();
		if(existingWorkspaceXml.isFile()) {
			System.out.println("Found existing workspace.xml");
			model = DefaultSaxParser.parse(new FileInputStream(existingWorkspaceXml));
		}
		model.setDir(dir);
		
		facade.init(new File(model.getDir()));
		
		DataTransmitter<Workspace, Workspace> result = uiCreator.createUI(model , "/workspace.html");
		
		model = result.awaitResult();
		
		facade.create(model);
	}
	
}
