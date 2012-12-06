package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class DeployTaskProcessor extends AbstractSerialTaskProcessor<DeployTask> {

	private final static Logger logger = Logger.getLogger(DeployTaskProcessor.class);

	@Inject
	DeployWorkflow workflow;
	AtomicReference<Transaction> currentTxRef = new AtomicReference<Transaction>();

	public DeployTaskProcessor() {
	}

	@Override
	protected Status doTransaction(final Transaction tx) throws IOException {

		currentTxRef.set(tx);
		DeployTask task = (DeployTask) tx.getTask();
		String domain = task.getDomain();

		eventTrackerChain.onEvent(new MessageEvent(tx.getTxId(), String.format("updating %s to version %s", domain,
				task.getKernelVersion())));
		OutputStream stdOut = txMgr.getLogOutputStream(tx.getTxId());
		Status exitStatus = Status.SUCCESS;
		try {
			exitStatus = updateKernel(domain, task.getKernelVersion(), stdOut);
		} catch (Exception e) {
			logger.error("error update kernel", e);
			exitStatus = Status.FAILED;
		} finally {
			IOUtil.close(stdOut);
		}
		return exitStatus;
	}

	private Status updateKernel(String domain, String kernelVersion, OutputStream stdOut) throws Exception {
		DeployStep steps = lookup(DeployStep.class);
		int exitCode = workflow.start(domain, kernelVersion, steps, stdOut);
		if (exitCode == DeployStep.CODE_OK) {
			return Status.SUCCESS;
		} else {
			return Status.FAILED;
		}
	}

	@Override
	public boolean cancel(TransactionId txId) {
		Transaction currentTx = currentTxRef.get();
		if (currentTx != null && currentTx.getTxId().equals(txId)) {
			if (workflow.kill()) {
				currentTx.setStatus(Status.KILLED);
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<DeployTask> handle() {
		return DeployTask.class;
	}

}
