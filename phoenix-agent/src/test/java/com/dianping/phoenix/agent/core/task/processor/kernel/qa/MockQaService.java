package com.dianping.phoenix.agent.core.task.processor.kernel.qa;


public class MockQaService implements QaService {

	@Override
	public CheckResult isDomainHealthy(DomainHealthCheckInfo deployInfo, int timeout, int queryInterval) {
		return CheckResult.PASS;
	}

}
