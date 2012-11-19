package com.dianping.phoenix.agent.core.task.processor.war;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;

public class WarUpdateTaskProcessor extends AbstractSerialTaskProcessor {

	@Override
	protected void doProcess(Transaction tx) {
		EventTracker eventTracker = tx.getEventTracker();
		WarUpdateTask task = (WarUpdateTask) tx.getTask();
		Artifact artifactToUpdate = task.getArtifactToUpdate();
		eventTracker.onEvent(new MessageEvent(String.format("updating %s to version %s", artifactToUpdate,
				task.getNewVersion())));
		eventTracker.onEvent(new LifecycleEvent("Done", Status.SUCCESS));
	}

}
