package com.dianping.phoenix.agent.core.task.processor;

import java.io.IOException;

public class ProcessTest {
	public static void main(String[] args) throws IOException {
		Process p = Runtime.getRuntime().exec("/bin/bash /Users/marsqing/Projects/tmp/tmp/test.sh");
		System.in.read();
		p.destroy();
		System.in.read();
	}
}
