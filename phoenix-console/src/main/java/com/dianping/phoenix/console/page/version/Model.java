package com.dianping.phoenix.console.page.version;

import java.util.List;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deliverable;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Deliverable> m_deliverables;

	private String m_creatingVersion;

	private String m_log;

	private int m_index = -1;

	public Model(Context ctx) {
		super(ctx);
	}

	public String getCreatingVersion() {
		return m_creatingVersion;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public List<Deliverable> getDeliverables() {
		return m_deliverables;
	}

	public int getIndex() {
		return m_index;
	}

	public String getLog() {
		return m_log;
	}

	public void setCreatingVersion(String creatingVersion) {
		m_creatingVersion = creatingVersion;
	}

	public void setDeliverables(List<Deliverable> deliverables) {
		m_deliverables = deliverables;
	}

	public void setIndex(int index) {
		m_index = index;
	}

	public void setLog(String log) {
		m_log = log;
	}
}
