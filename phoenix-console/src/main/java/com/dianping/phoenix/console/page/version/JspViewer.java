package com.dianping.phoenix.console.page.version;

import org.unidal.web.mvc.view.BaseJspViewer;

import com.dianping.phoenix.console.ConsolePage;

public class JspViewer extends BaseJspViewer<ConsolePage, Action, Context, Model> {
	@Override
	protected String getJspFilePath(Context ctx, Model model) {
		Action action = model.getAction();

		switch (action) {
		case VIEW:
			return JspFile.VIEW.getPath();
		case STATUS:
			return JspFile.STATUS.getPath();
		}

		throw new RuntimeException("Unknown action: " + action);
	}
}
