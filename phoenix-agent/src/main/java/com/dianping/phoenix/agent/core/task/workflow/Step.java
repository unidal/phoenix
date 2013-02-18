package com.dianping.phoenix.agent.core.task.workflow;

import java.util.Map;

public interface Step {

	int CODE_OK = 0;
	int CODE_ERROR = -1;

	int doStep() throws Exception;
	
	Step getNextStep(int exitCode);

	Map<String, String> getLogChunkHeader();

}
