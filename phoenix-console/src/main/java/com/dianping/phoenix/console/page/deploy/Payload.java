package com.dianping.phoenix.console.page.deploy;

import com.dianping.phoenix.console.ConsolePage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("id")
	private int m_id;

    @FieldMeta("hosts")
    private String m_hosts;

    private int count;   //TODO remove me!

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.VIEW);
	}

	@Override
	public Action getAction() {
		return m_action;
	}

	public int getId() {
		return m_id;
	}

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.DEPLOY);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.VIEW;
		}
	}

    public String getHosts() {
        return m_hosts;
    }

    public void setHosts(String hosts) {
        this.m_hosts = hosts;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
