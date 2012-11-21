package com.dianping.phoenix.service;

public class DeploymentPlan {
	private String m_version;

	private String m_policy;

	private boolean m_abortOnError;

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String version) {
		m_version = version;
	}

	public String getPolicy() {
		return m_policy;
	}

	public void setPolicy(String policy) {
		m_policy = policy;
	}

	public boolean isAbortOnError() {
		return m_abortOnError;
	}

	public void setAbortOnError(boolean abortOnError) {
		m_abortOnError = abortOnError;
	}

	@Override
	public String toString() {
		return String.format("%s[version=%s, policy=%s, abortOnError=%s]", DeploymentPlan.class.getSimpleName(),
		      m_version, m_policy, m_abortOnError);
	}
}
