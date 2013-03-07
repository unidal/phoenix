package com.dianping.phoenix.agent.core.shell;

import org.apache.commons.lang.StringUtils;

public class CmdBuilder {

	private StringBuilder sb = new StringBuilder();

	private CmdBuilder(String scriptPath) {
		if (StringUtils.isBlank(scriptPath)) {
			throw new RuntimeException(String.format("scriptPath can not be blank '%s'", scriptPath));
		} else {
			sb.append(scriptPath);
		}
	}

	public static CmdBuilder start(String scriptPath) {
		return new CmdBuilder(scriptPath);
	}

	/**
	 * Add parameter to script, double quotation mark(") in paramValue should be
	 * escaped to \"
	 * 
	 * @param paramName
	 * @param paramValue
	 * @return
	 */
	public CmdBuilder add(String paramName, String paramValue) {
		if (StringUtils.isBlank(paramName)) {
			throw new RuntimeException(String.format("paramName can not be blank '%s'", paramName));
		} else if (paramValue == null) {
			throw new RuntimeException("paramValue can not be null");
		} else {
			sb.append(String.format(" --%s=\"%s\"", paramName.trim(), paramValue));
		}
		return this;
	}

	public String get() {
		return sb.toString();
	}

}
