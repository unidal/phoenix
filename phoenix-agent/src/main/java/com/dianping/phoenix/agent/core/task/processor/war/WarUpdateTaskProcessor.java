package com.dianping.phoenix.agent.core.task.processor.war;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.exec.ExecuteException;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
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
		
		boolean cmdSuccess = false;
		try {
			cmdSuccess = runShellCmd(tx.getTxLog().getOutputStream(tx.getTxId()));
		} catch (Exception e) {
			e.printStackTrace();
			//TODO
		}
		eventTracker.onEvent(new LifecycleEvent("Done", cmdSuccess ? Status.SUCCESS : Status.FAILED));
	}
	
	private String getScriptPath() {
		URL scriptUrl = this.getClass().getClassLoader().getResource("agent.sh");
		if(scriptUrl == null) {
			throw new RuntimeException("agent.sh not found");
		}
		return scriptUrl.getPath();
	}

	private boolean runShellCmd(OutputStream outputCollector) throws ExecuteException, IOException {
		return false;
	}

	@Override
	public boolean cancel(TransactionId txId) {
		// TODO Auto-generated method stub
		return false;
	}

}
