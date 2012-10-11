package com.dianping.phoenix.agent.page.deploy.shell;

import java.io.IOException;
import java.io.OutputStream;

public class FakeShell implements Shell {

	@Override
	public void prepare(String libVersion, OutputStream outputCollector) throws IOException {
		System.out.println("agent prepare");
	}

	@Override
	public void activate(OutputStream outputCollector) throws IOException {
		System.out.println("agent activate");
	}

	@Override
	public void commit(OutputStream outputCollector) throws IOException {
		System.out.println("agent commit");
	}

	@Override
	public void rollback(String appVersion, OutputStream outputCollector) throws IOException {
		System.out.println("agent rollback");
	}

}
