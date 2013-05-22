package com.dianping.maven.plugin.phoenix.model.visitor;

import com.dianping.maven.plugin.phoenix.model.transform.BaseVisitor;

public class AbstractVisitor<T> extends BaseVisitor {
	
	protected T result;

	public T getVisitResult() {
		return result;
	}
	
}
