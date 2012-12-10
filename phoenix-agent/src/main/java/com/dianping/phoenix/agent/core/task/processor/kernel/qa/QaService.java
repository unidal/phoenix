package com.dianping.phoenix.agent.core.task.processor.kernel.qa;


public interface QaService {

	public enum CheckResult {
		SUBMIT_FAILED, // can't submit task to qa service
		PASS, // qa service tell the domain passed test 
		FAIL, // qa service tell the domain did not pass the test
		TIMEOUT, // timeout when calling qa service
		AGENT_LOCAL_EXCEPTION, // agent encounter local exception
		QA_LOCAL_EXCEPTION // qa service encounter local exception
	}

	CheckResult isDomainHealthy(DomainHealthCheckInfo deployInfo, int timeout, int queryInterval);

}
