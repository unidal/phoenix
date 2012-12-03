package com.dianping.phoenix.deploy.agent;

public class Progress {
	private int m_current;

	private int m_total;

	private String m_status;

	public int getCurrent() {
		return m_current;
	}

	public String getStatus() {
		return m_status;
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

	public void setTotal(int total) {
		m_total = total;
	}

	@Override
	public String toString() {
		return String.format("Progress[%s/%s, %s]", m_current, m_total, m_status);
	}
}