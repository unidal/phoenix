package com.dianping.phoenix.console.page.home;

import org.unidal.web.mvc.view.BaseJspViewer;

import com.dianping.phoenix.console.ConsolePage;

public class JspViewer extends BaseJspViewer<ConsolePage, Action, Context, Model> {
	@Override
	protected String getJspFilePath(Context ctx, Model model) {
		Action action = model.getAction();

		switch (action) {
		case HOME:
			return JspFile.HOME.getPath();
		case PROJECT:
			return JspFile.PROJECT.getPath();
		case DEPLOY:
			return JspFile.DEPLOY.getPath();
		case SEARCHJAR:
			return JspFile.SEARCHJAR.getPath();
		case SEARCHAGENT:
			return JspFile.SEARCHAGENT.getPath();
		case OVERVIEW:
			return JspFile.OVERVIEW.getPath();
		case DOMAININFO:
			return JspFile.DOMAININFO.getPath();
		case ABOUT:
			return JspFile.ABOUT.getPath();
		}

		throw new RuntimeException("Unknown action: " + action);
	}
}
