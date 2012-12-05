package com.dianping.phoenix.agent.core.task.processor;

import java.util.concurrent.Semaphore;

public class SemaphoreWrapper {

	private Semaphore semaphore = new Semaphore(1);
	
	public Semaphore getSemaphore() {
		return semaphore;
	}
	
}
