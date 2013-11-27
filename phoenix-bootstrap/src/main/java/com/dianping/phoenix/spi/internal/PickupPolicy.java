package com.dianping.phoenix.spi.internal;

public enum PickupPolicy {
	USE_APP("use-app"), USE_KERNEL("use-kernel"), DEFAULT("default"), USE_NEITHER("use-neither");

	private String m_policy;

	private PickupPolicy(String policy) {
		m_policy = policy;
	}

	public String getName() {
		return m_policy;
	}

	public static PickupPolicy getByName(String name) {
		for (PickupPolicy policy : PickupPolicy.values()) {
			if (policy.getName().equals(name)) {
				return policy;
			}
		}
		return null;
	}
}
