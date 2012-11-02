package com.dianping.kernel.inspect.page.descriptor;

import com.dianping.kernel.inspect.InspectPage;
import com.site.web.mvc.ActionContext;
import com.site.web.mvc.ActionPayload;
import com.site.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<InspectPage, Action> {
	private InspectPage m_page;

	@FieldMeta("op")
	private Action m_action;

	public void setAction(Action action) {
		m_action = action;
	}

	@Override
	public Action getAction() {
		return m_action;
	}

	@Override
	public InspectPage getPage() {
		return m_page;
	}

	@Override
	public void setPage(String page) {
		m_page = InspectPage.getByName(page, InspectPage.DESCRIPTOR);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
	}
}
