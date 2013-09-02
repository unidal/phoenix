package com.dianping.phoenix.lb.domain.model.dispatch;

import com.dianping.phoenix.lb.domain.model.Visitor;

public class Redirect extends DispatchStep {

	private String url;
	
	public Redirect(String url) {
		this.url = url;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
