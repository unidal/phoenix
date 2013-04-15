package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Engine;
import com.dianping.phoenix.agent.core.task.workflow.Step;
import com.dianping.phoenix.agent.core.tx.LogFormatter;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class AgentUpgradeTaskProcessor extends AbstractSerialTaskProcessor<AgentUpgradeTask> {

	private final static Logger logger = Logger.getLogger(AgentUpgradeTaskProcessor.class);

	private ScriptExecutor scriptExecutor;
	@Inject
	private LogFormatter logFormatter;
	@Inject
	private Engine engine;

	private AtomicReference<TransactionId> currentTxIdRef = new AtomicReference<TransactionId>();

	private AtomicReference<Transaction> currentTxRef = new AtomicReference<Transaction>();
	private AtomicReference<Context> currentCtxRef = new AtomicReference<Context>();

	@Override
	public boolean cancel(TransactionId txId) {
		if (txId != null && txId.equals(currentTxIdRef.get())) {
			scriptExecutor.kill();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Class<AgentUpgradeTask> handle() {
		return AgentUpgradeTask.class;
	}

	@Override
	protected Status doTransaction(Transaction tx) throws Exception {
		currentTxRef.set(tx);
		AgentUpgradeTask task = (AgentUpgradeTask) tx.getTask();
		eventTrackerChain.onEvent(new MessageEvent(tx.getTxId(), String.format("updating phoenix-agent to version %s",
				task.getAgentVersion())));
		OutputStream stdOut = txMgr.getLogOutputStream(tx.getTxId());
		AgentUpgradeContext ctx = (AgentUpgradeContext) lookup(Context.class, "agent_ctx");
		ctx.setLogOut(stdOut);
		ctx.setLogFormatter(logFormatter);
		ctx.setTask(task);
		ctx.setUnderLyingFile(txMgr.getUnderlyingFile(tx.getTxId()).getAbsolutePath());
		currentCtxRef.set(ctx);

		Status exitStatus = Status.SUCCESS;
		try {
			exitStatus = upgradeAgent(ctx);
		} catch (Exception e) {
			logger.error("error update kernel", e);
			exitStatus = Status.FAILED;
		} finally {
			IOUtil.close(stdOut);
		}
		return exitStatus;
	}

	private Status upgradeAgent(Context ctx) {
		int exitCode = engine.start(AgentUpgradeStep.START, ctx);
		if (exitCode == Step.CODE_OK) {
			return Status.SUCCESS;
		} else {
			return Status.FAILED;
		}
	}
}
