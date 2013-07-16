package com.dianping.phoenix.dev.agent.page.home;

import org.unidal.web.mvc.view.BaseJspViewer;

import com.dianping.phoenix.dev.agent.AgentPage;

public class JspViewer extends BaseJspViewer<AgentPage, Action, Context, Model> {
	@Override
	protected String getJspFilePath(Context ctx, Model model) {
		Action action = model.getAction();

		switch (action) {
		case VIEW:
			return JspFile.VIEW.getPath();
		}

		throw new RuntimeException("Unknown action: " + action);
	}
}
