package com.dianping.phoenix.lb.domain.model.dispatch;

import java.util.List;

import com.dianping.phoenix.lb.domain.model.Visitable;
import com.dianping.phoenix.lb.domain.model.Visitor;

public class DispatchAction implements Visitable {

	private UrlMatcher urlMatcher;
	private List<DispatchStep> steps;

	public DispatchAction(UrlMatcher urlMatcher, List<DispatchStep> steps) {
		this.urlMatcher = urlMatcher;
		this.steps = steps;
	}

	public UrlMatcher getUrlMatcher() {
		return urlMatcher;
	}

	public List<DispatchStep> getSteps() {
		return steps;
	}

	public void setSteps(List<DispatchStep> steps) {
		this.steps = steps;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		visitor.visit(urlMatcher);
		for (DispatchStep step : steps) {
			step.accept(visitor);
		}
	}

}
