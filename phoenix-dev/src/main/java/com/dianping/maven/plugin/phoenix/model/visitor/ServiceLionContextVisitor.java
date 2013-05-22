package com.dianping.maven.plugin.phoenix.model.visitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.misc.file.ServiceLionContext;

public class ServiceLionContextVisitor extends AbstractVisitor<ServiceLionContext> {

	public ServiceLionContextVisitor() {
		result = new ServiceLionContext();
		// use defautl service-port.xml
		result.setRefreshServiceMeta(false);
		result.setServiceHost("127.0.0.1");
	}

	@Override
	public void visitWorkspace(Workspace workspace) {
		Map<String, File> projectBaseDirMapping = new HashMap<String, File>();
		projectBaseDirMapping.put("itdoesntmatter", new File(workspace.getDir()));
		result.setProjectBaseDirMapping(projectBaseDirMapping);
		super.visitWorkspace(workspace);
	}

}
