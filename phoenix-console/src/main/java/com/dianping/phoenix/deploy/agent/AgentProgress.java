package com.dianping.phoenix.deploy.agent;

public class AgentProgress {
	private int m_current;

	private int m_total;

	private String m_status;

	private String m_step;

	public int getCurrent() {
		return m_current;
	}

	public String getStatus() {
		return m_status;
	}

	public String getStep() {
		return m_step;
	}

	public int getTotal() {
		return m_total;
	}

	public void setCurrent(int current) {
		m_current = current;
	}

	public void setStatus(String status) {
		m_status = status;
	}

	public void setStep(String step) {
		m_step = step;
	}

	public void setTotal(int total) {
		m_total = total;
	}

	@Override
	public String toString() {
		return String.format("Progress[%s/%s, %s, %s]", m_current, m_total, m_status, m_step);
	}
}