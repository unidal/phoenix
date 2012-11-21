package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.DefaultAgent;
import com.dianping.phoenix.agent.core.log.FilePerTxLog;
import com.dianping.phoenix.agent.core.log.TransactionLog;
import com.dianping.phoenix.agent.core.shell.DefaultScriptExecutor;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTaskProcessor;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(TransactionLog.class, FilePerTxLog.class));
		all.add(C(ScriptExecutor.class, DefaultScriptExecutor.class));
		all.add(C(Agent.class, DefaultAgent.class).req(TransactionLog.class).req(TaskProcessorFactory.class));
		all.add(C(TaskProcessor.class, WarUpdateTaskProcessor.class).req(TransactionLog.class).req(ScriptExecutor.class));
		all.add(C(TaskProcessorFactory.class));

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}
