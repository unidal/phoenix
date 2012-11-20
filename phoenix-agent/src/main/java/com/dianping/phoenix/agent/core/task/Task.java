package com.dianping.phoenix.agent.core.task;


public interface Task {
	
	public enum Status {
		INIT, REJECTED, PROCESSING, FAILED, SUCCESS
	}

	Status getStatus();
	
}
