package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.configure.ConfigManager;

public class AgentUpgradeTaskProcessor extends AbstractSerialTaskProcessor<AgentUpgradeTask> {
	
	private final static Logger logger = Logger.getLogger(AgentUpgradeTaskProcessor.class);

	private ScriptExecutor scriptExecutor;
	@Inject
	private ConfigManager config;
	private AtomicReference<TransactionId> currentTxRef = new AtomicReference<TransactionId>();
	
	@Override
	public boolean cancel(TransactionId txId) {
		if(txId != null && txId.equals(currentTxRef.get())) {
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
		TransactionId txId = tx.getTxId();
		scriptExecutor = lookup(ScriptExecutor.class);
		currentTxRef.set(txId);
		OutputStream logOut = txMgr.getLogOutputStream(txId);
		upgradeAgent(logOut, tx);
		return Status.SUCCESS;
	}

	private void upgradeAgent(OutputStream logOut, Transaction tx) throws IOException {
		StringBuilder sb = new StringBuilder();
		AgentUpgradeTask task = (AgentUpgradeTask) tx.getTask();
		logger.info(String.format("start upgrading agent to version %s", task.getAgentVersion()));
		
		sb .append(config.getAgentSelfUpgradeScriptFile().getAbsolutePath());
		sb.append(String.format(" -g \"%s\" ", task.getAgentGitUrl()));
		sb.append(String.format(" -v \"%s\" ", task.getAgentVersion()));
		sb.append(String.format(" -l \"%s\" ", txMgr.getUnderlyingFile(tx.getTxId()).getAbsolutePath()));
		
		scriptExecutor.exec(sb.toString(), logOut, logOut);
	}

}
