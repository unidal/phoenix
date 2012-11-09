package com.dianping.kernel.inspect.page.classpath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dianping.kernel.inspect.InspectPage;
import com.site.web.mvc.ViewModel;

public class Model extends ViewModel<InspectPage, Action, Context> {
	private List<Artifact> m_artifacts;

	private Map<String, Artifact> m_kernelArtifacts;

	private Map<String, Artifact> m_appArtifacts;
	
	private Map<String, Artifact> m_containerArtifacts;

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

	public Map<String, Artifact> getContainerArtifacts() {
		return m_containerArtifacts;
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

	public void setContainerArtifacts(List<Artifact> containerArtifacts) {
		m_containerArtifacts = new HashMap<String, Artifact>();

		for (Artifact artifact : containerArtifacts) {
			m_containerArtifacts.put(artifact.getKey(), artifact);
		}
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
