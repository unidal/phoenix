package com.dianping.phoenix.deploy;

public enum DeployType {
	KERNEL("phoenix-kernel"), AGENT("phoenix-agent"), UNKNOW("unknown");
	private String m_name;

	private DeployType(String name) {
		m_name = name;
	}

	public static DeployType get(String name) {
		for (DeployType warType : DeployType.values()) {
			if (warType.m_name.equals(name.trim().toLowerCase())) {
				return warType;
			}
		}
		return UNKNOW;
	}

	public String getName() {
		return m_name;
	}

}
