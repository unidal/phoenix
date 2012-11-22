package com.dianping.phoenix.service;

public enum DeploymentPolicy {
	ONE_BY_ONE(1, "1 -> 1 -> 1 -> 1 ( 每次一台 )"),

	TWO_BY_TWO(2, "1 -> 2 -> 2 -> 2 ( 每次两台 )"),

	THREE_BY_THREE(3, "1 -> 3 -> 3 -> 3 ( 每次三台 )");

	private int m_id;

	private String m_description;

	private DeploymentPolicy(int id, String description) {
		m_id = id;
		m_description = description;
	}

	public int getId() {
		return m_id;
	}

	public String getDescription() {
		return m_description;
	}
}
