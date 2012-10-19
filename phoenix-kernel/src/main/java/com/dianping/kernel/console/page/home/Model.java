package com.dianping.kernel.console.page.home;

import com.dianping.kernel.console.ConsolePage;
import com.site.web.mvc.ViewModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}
}
