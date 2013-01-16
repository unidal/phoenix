package com.dianping.phoenix.agent.core.task.processor.kernel;

import com.dianping.phoenix.agent.core.task.AbstractTask;

public class DetachTask extends AbstractTask {

	private String domain;

	/**
	 * for serialization
	 */
	@SuppressWarnings("unused")
	private DetachTask() {
	}

	public DetachTask(String domain) {
		super();
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

}
