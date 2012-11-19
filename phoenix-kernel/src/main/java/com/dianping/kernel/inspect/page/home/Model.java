package com.dianping.kernel.inspect.page.home;

import com.dianping.kernel.GlobalModel;
import com.dianping.kernel.inspect.InspectPage;
import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<InspectPage, Action, Context> {
	private GlobalModel m_applicationModel;

	public Model(Context ctx) {
		super(ctx);
	}

	public GlobalModel getApplicationModel() {
		return m_applicationModel;
	}
	
	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public void setApplicationModel(GlobalModel applicationModel) {
		m_applicationModel = applicationModel;
	}
}
