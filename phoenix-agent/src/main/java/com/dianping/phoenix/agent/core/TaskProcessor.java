package com.dianping.phoenix.agent.core;

public interface TaskProcessor {

	void process(Task task, EventTracker eventTracker);
	
}
