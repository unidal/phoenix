package com.dianping.service.editor.page.home;

import com.dianping.service.editor.EditorPage;
import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<EditorPage, Action, Context> {
	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}
}
