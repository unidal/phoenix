package com.dianping.phoenix.dev.agent.page.home;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.dev.agent.AgentPage;

public class Model extends ViewModel<AgentPage, Action, Context> {
	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}
}
