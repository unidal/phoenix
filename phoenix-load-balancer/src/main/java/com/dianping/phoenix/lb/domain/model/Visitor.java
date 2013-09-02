package com.dianping.phoenix.lb.domain.model;

import com.dianping.phoenix.lb.domain.model.dispatch.DispatchAction;
import com.dianping.phoenix.lb.domain.model.dispatch.Forward;
import com.dianping.phoenix.lb.domain.model.dispatch.Redirect;
import com.dianping.phoenix.lb.domain.model.dispatch.UrlMatcher;
import com.dianping.phoenix.lb.domain.model.pool.Pool;

public interface Visitor {

	public void visit(VirtualServer vs);
	public void visit(Pool pool);
	public void visit(DispatchAction action);
	public void visit(Forward forward);
	public void visit(Redirect redirect);
	public void visit(UrlMatcher urlMatcher);
	
}
