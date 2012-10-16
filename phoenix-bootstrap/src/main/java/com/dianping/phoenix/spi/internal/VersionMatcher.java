package com.dianping.phoenix.spi.internal;

public class VersionMatcher {
	private String m_pattern;

	public VersionMatcher(String pattern) {
		m_pattern = pattern;
	}

	public boolean matches(String version) {
		if (m_pattern == null) {
			return true;
		}

		// TODO
		return false;
	}
}
