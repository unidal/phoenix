package com.dianping.phoenix.agent.core.shell;

import java.io.IOException;

import org.junit.Test;

public class DefaultScriptExecutorTest {

	@Test
	public void testKillProcess() throws IOException {
		final DefaultScriptExecutor exec = new DefaultScriptExecutor();
		String scriptPath = "/Users/marsqing/Projects/tmp/tmp/test.sh";
		new Thread() {

			@Override
			public void run() {
				try {
					System.in.read();
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				exec.kill();
			}
			
		}.start();
		exec.exec(scriptPath , System.out, System.out);
	}
	
}
