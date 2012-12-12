package com.dianping.phoenix.agent.page.deploy;

import com.dianping.phoenix.agent.AgentPage;
import org.unidal.web.mvc.view.BaseJspViewer;

public class JspViewer extends BaseJspViewer<AgentPage, Action, Context, Model> {
	@Override
	protected String getJspFilePath(Context ctx, Model model) {
		return JspFile.VIEW.getPath();
	}
}
