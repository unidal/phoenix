package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import com.dianping.phoenix.dev.core.model.workspace.transform.BaseVisitor;

public class AbstractVisitor<T> extends BaseVisitor {
	
	protected T result;

	public T getVisitResult() {
		return result;
	}
	
}
