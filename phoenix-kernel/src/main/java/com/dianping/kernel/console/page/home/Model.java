package com.dianping.kernel.console.page.home;

import com.dianping.kernel.console.ConsolePage;
import com.dianping.kernel.state.ApplicationModel;
import com.site.web.mvc.ViewModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private ApplicationModel m_applicationModel;

	public Model(Context ctx) {
		super(ctx);
	}

	public ApplicationModel getApplicationModel() {
		return m_applicationModel;
	}
	
	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public void setApplicationModel(ApplicationModel applicationModel) {
		m_applicationModel = applicationModel;
	}
}
