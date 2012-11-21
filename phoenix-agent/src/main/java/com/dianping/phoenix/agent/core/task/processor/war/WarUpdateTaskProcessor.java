package com.dianping.phoenix.agent.core.task.processor.war;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.log.TransactionLog;
import com.dianping.phoenix.agent.core.shell.ExecuteResultCallback;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;

public class WarUpdateTaskProcessor extends AbstractSerialTaskProcessor<WarUpdateTask> {

	@Inject
	private TransactionLog txLog;
	@Inject
	private ScriptExecutor scriptExecutor;

	@Override
	protected void doProcess(final Transaction tx) throws IOException {
		try {
			innerProcess(tx);
		} catch (Exception e) {
			eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), e.getMessage(), Status.FAILED));
		}
	}

	private void innerProcess(final Transaction tx) throws IOException {
		WarUpdateTask task = (WarUpdateTask) tx.getTask();
		Artifact artifactToUpdate = task.getArtifactToUpdate();
		eventTrackerChain.onEvent(new MessageEvent(tx.getTxId(), String.format("updating %s to version %s",
				artifactToUpdate, task.getNewVersion())));
		final OutputStream stdOut = txLog.getOutputStream(tx.getTxId());

		ExecuteResultCallback execCallback = new ExecuteResultCallback() {

			private void cleanUp() {
				try {
					stdOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onProcessFailed(Exception e) {
				cleanUp();
				eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), e.getMessage(), Status.FAILED));
			}

			@Override
			public void onProcessCompleted(int exitCode) {
				cleanUp();
				Status exitStatus = exitCode == 0 ? Status.SUCCESS : Status.FAILED;
				eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), Integer.toString(exitCode), exitStatus));
			}
		};

		runShellCmd(stdOut, execCallback);
	}

	private String getScriptPath() {
		URL scriptUrl = this.getClass().getClassLoader().getResource("agent.sh");
		if (scriptUrl == null) {
			throw new RuntimeException("agent.sh not found");
		}
		return scriptUrl.getPath();
	}

	private void runShellCmd(OutputStream outputCollector, ExecuteResultCallback execCallback) throws IOException {
		scriptExecutor.exec(getScriptPath(), outputCollector, outputCollector, execCallback);
	}

	@Override
	public boolean cancel(TransactionId txId) {
		scriptExecutor.kill();
		return true;
	}

	@Override
	public Class<WarUpdateTask> handle() {
		return WarUpdateTask.class;
	}

}
