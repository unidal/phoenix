package com.dianping.phoenix.console.page.version;

import java.util.List;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Version;

import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Version> m_versions;

	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public List<Version> getVersions() {
		return m_versions;
	}

	public void setVersions(List<Version> versions) {
		m_versions = versions;
	}
}
