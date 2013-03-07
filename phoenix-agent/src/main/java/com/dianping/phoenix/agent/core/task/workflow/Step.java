package com.dianping.phoenix.agent.core.task.workflow;

import java.util.Map;

public interface Step {

	int CODE_OK = 0;
	int CODE_ERROR = -1;
	
	String HEADER_STEP = "Step";
	String HEADER_PROGRESS = "Progress";
	String HEADER_STATUS = "Status";
	
	String STATUS_SUCCESS = "successful";
	String STATUS_FAIL = "failed";

	int doStep(Context ctx) throws Exception;
	
	Step getNextStep(int exitCode);

	Map<String, String> getLogChunkHeader();

}
