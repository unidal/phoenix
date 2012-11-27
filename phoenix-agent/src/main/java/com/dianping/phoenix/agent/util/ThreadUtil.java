package com.dianping.phoenix.agent.util;

public class ThreadUtil {

	public static void sleepQuiet(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
}
