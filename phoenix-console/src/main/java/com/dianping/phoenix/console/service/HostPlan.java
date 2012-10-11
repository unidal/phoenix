package com.dianping.phoenix.console.service;

import java.util.Arrays;

public class HostPlan {
	private int m_index;

	private String m_host;

	private DeployStep[] m_steps;

	private String[] m_statuses;

	private DeployStep m_currentStep;

	public HostPlan(int index, String host) {
		m_index = index;
		m_host = host;
		m_steps = DeployStep.values();
		m_statuses = new String[m_steps.length];
		m_currentStep = m_steps[0];

		Arrays.fill(m_statuses, "todo");
	}

	public String getHost() {
		return m_host;
	}

	public int getIndex() {
		return m_index;
	}

	public String[] getStatuses() {
		return m_statuses;
	}

	public DeployStep[] getSteps() {
		return m_steps;
	}

	public void setCurrentStep(DeployStep currentStep) {
		m_currentStep = currentStep;
	}

	public void setStatus(String status) {
		m_statuses[m_currentStep.ordinal()] = status;
	}
}