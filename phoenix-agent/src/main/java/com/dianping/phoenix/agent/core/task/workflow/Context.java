package com.dianping.phoenix.agent.core.task.workflow;

import java.io.OutputStream;

public interface Context {

	boolean isKilled();

	OutputStream getLogOut();

}
