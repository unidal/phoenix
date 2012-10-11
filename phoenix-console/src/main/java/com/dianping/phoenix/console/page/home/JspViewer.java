package com.dianping.phoenix.console.page.home;

import com.dianping.phoenix.console.ConsolePage;
import com.site.web.mvc.view.BaseJspViewer;

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
		case ABOUT:
			return JspFile.ABOUT.getPath();
		}

		throw new RuntimeException("Unknown action: " + action);
	}
}
