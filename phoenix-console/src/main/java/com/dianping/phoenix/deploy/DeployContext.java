package com.dianping.phoenix.deploy;

public class DeployContext {
	private int m_deployId;

	private int m_offset;

	public DeployContext(int deployId) {
		m_deployId = deployId;
	}

	public int getDeployId() {
		return m_deployId;
	}

	public int getOffset() {
		return m_offset;
	}

	public void setOffset(int offset) {
		m_offset = offset;
	}
}
