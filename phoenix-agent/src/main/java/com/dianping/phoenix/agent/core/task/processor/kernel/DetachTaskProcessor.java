package com.dianping.phoenix.agent.core.task.processor.kernel;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.configure.ConfigManager;

/**
 * Remove &lt;Loader$gt; from server.xml
 * 
 * @author marsqing
 * 
 */
public class DetachTaskProcessor extends AbstractSerialTaskProcessor<DetachTask> {
	@Inject
	private ConfigManager config;

	@Inject
	private ServerXmlManager m_serverXmlManager;

	public DetachTaskProcessor() {
	}

	@Override
	public boolean cancel(TransactionId txId) {
		return true;
	}

	@Override
	public Class<DetachTask> handle() {
		return DetachTask.class;
	}

	@Override
	protected Status doTransaction(Transaction tx) throws Exception {
		DetachTask task = (DetachTask) tx.getTask();
		m_serverXmlManager.detachPhoenixContextLoader(config.getServerXml(),
				String.format(config.getDomainDocBaseFeaturePattern(), task.getDomain()));
		return Status.SUCCESS;
	}

}
