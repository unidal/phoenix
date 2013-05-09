package com.dianping.phoenix.agent.core.task.processor.upgrade;

import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.workflow.Context;

public class AgentUpgradeContext extends Context {
	@Inject
	private AgentUpgradeStepProvider stepProvider;
	@Inject
	private ScriptExecutor scriptExecutor;

	private com.dianping.cat.message.Transaction c_agentUpgrade;

	private String underLyingFile;
	private String tempScriptFile;

	public void setTempScriptFile(String tempScriptFile) {
		this.tempScriptFile = tempScriptFile;
	}

	public String getUnderLyingFile() {
		return underLyingFile;
	}

	public void setUnderLyingFile(String underLyingFile) {
		this.underLyingFile = underLyingFile;
	}

	public AgentUpgradeStepProvider getStepProvider() {
		return stepProvider;
	}

	public ScriptExecutor getScriptExecutor() {
		return scriptExecutor;
	}

	public String getTempScriptFile() {
		return tempScriptFile;
	}

	public com.dianping.cat.message.Transaction getCatTransaction() {
		return c_agentUpgrade;
	}

	@Override
	public void setTask(Task task) {
		super.setTask(task);
		AgentUpgradeTask tsk = (AgentUpgradeTask) task;
		c_agentUpgrade = Cat.getProducer().newTransaction("Agent", tsk.getAgentVersion());
		try {
			setMsgId(Cat.getProducer().createMessageId());
		} catch (Exception e) {
			setMsgId("no-cat-id");
		}
	}
}
