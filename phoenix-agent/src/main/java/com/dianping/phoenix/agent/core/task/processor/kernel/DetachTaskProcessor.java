package com.dianping.phoenix.agent.core.task.processor.kernel;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.ContainerManager;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

/**
 * Remove &lt;Loader$gt; from server.xml
 * 
 * @author marsqing
 * 
 */
public class DetachTaskProcessor extends AbstractSerialTaskProcessor<DetachTask> {
	@Inject
	private ContainerManager m_containerManager;

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
		try {
			m_containerManager.detachContainerLoader(task.getDomain());
		} catch (Exception e) {
			return Status.FAILED;
		}
		return Status.SUCCESS;
	}

}
