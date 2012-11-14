package com.dianping.phoenix.agent.core;

public class WarUpdateTaskProcessor implements TaskProcessor {

	@Override
	public void process(Task task, EventTracker eventTracker) {
		eventTracker.onEvent(new MessageEvent("processing " + task));
	}

}
