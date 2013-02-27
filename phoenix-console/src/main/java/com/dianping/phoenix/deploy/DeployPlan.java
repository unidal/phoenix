package com.dianping.phoenix.deploy;

public class DeployPlan {
	private String m_warType = "phoenix-kernel";

	private String m_version;

	private String m_policy;

	private boolean m_abortOnError = true;

	private boolean m_skipTest;

	public String getPolicy() {
		return m_policy;
	}

	public String getVersion() {
		return m_version;
	}

	public String getWarType() {
		return m_warType;
	}

	public boolean isAbortOnError() {
		return m_abortOnError;
	}

	public boolean isSkipTest() {
		return m_skipTest;
	}

	public void setAbortOnError(boolean abortOnError) {
		m_abortOnError = abortOnError;
	}

	public void setPolicy(String policy) {
		m_policy = policy;
	}

	public void setSkipTest(boolean testService) {
		m_skipTest = testService;
	}

	public void setVersion(String version) {
		m_version = version;
	}

	public void setWarType(String warType) {
		m_warType = warType;
	}

	@Override
	public String toString() {
		return String.format("%s[version=%s, policy=%s, abortOnError=%s]", DeployPlan.class.getSimpleName(), m_version,
		      m_policy, m_abortOnError);
	}
}
