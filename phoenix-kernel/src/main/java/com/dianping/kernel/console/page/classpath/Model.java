package com.dianping.kernel.console.page.classpath;

import java.util.List;

import com.dianping.kernel.console.ConsolePage;
import com.site.web.mvc.ViewModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Artifact> m_artifacts;

	public Model(Context ctx) {
		super(ctx);
	}

	public List<Artifact> getArtifacts() {
		return m_artifacts;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public void setArtifacts(List<Artifact> artifacts) {
		m_artifacts = artifacts;
	}
}
