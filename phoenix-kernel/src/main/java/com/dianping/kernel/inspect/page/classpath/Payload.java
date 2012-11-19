package com.dianping.kernel.inspect.page.classpath;

import com.dianping.kernel.inspect.InspectPage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

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
		m_page = InspectPage.getByName(page, InspectPage.CLASSPATH);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
	}
}
