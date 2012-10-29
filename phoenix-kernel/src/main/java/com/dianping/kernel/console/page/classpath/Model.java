package com.dianping.kernel.console.page.classpath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dianping.kernel.console.ConsolePage;
import com.site.web.mvc.ViewModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Artifact> m_artifacts;

	private Map<String, Artifact> m_kernelArtifacts;

	private Map<String, Artifact> m_appArtifacts;

	private boolean m_mixedMode;

	public Model(Context ctx) {
		super(ctx);
	}

	public Map<String, Artifact> getAppArtifacts() {
		return m_appArtifacts;
	}

	public List<Artifact> getArtifacts() {
		return m_artifacts;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public Map<String, Artifact> getKernelArtifacts() {
		return m_kernelArtifacts;
	}

	public boolean isMixedMode() {
		return m_mixedMode;
	}

	public void setAppArtifacts(List<Artifact> appArtifacts) {
		m_appArtifacts = new HashMap<String, Artifact>();

		for (Artifact artifact : appArtifacts) {
			m_appArtifacts.put(artifact.getKey(), artifact);
		}
	}

	public void setArtifacts(List<Artifact> artifacts) {
		m_artifacts = artifacts;
	}

	public void setKernelArtifacts(List<Artifact> kernelArtifacts) {
		m_kernelArtifacts = new HashMap<String, Artifact>();

		for (Artifact artifact : kernelArtifacts) {
			m_kernelArtifacts.put(artifact.getKey(), artifact);
		}
	}

	public void setMixedMode(boolean mixedMode) {
		m_mixedMode = mixedMode;
	}
}
