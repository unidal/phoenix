package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.model.workspace.transform.BaseVisitor;

public class AbstractVisitor<T> extends BaseVisitor {
	
	protected T result;
	protected String from;

	public T getVisitResult() {
		return result;
	}
	
	public void visitWorkspace(Workspace workspace) {
	    from = workspace.getFrom();
	    super.visitWorkspace(workspace);
	}
	
}
