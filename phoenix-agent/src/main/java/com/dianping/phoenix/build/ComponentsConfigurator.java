package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.initialization.DefaultModuleManager;
import org.unidal.initialization.ModuleManager;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.AgentStatusReporter;
import com.dianping.phoenix.agent.core.DefaultAgent;
import com.dianping.phoenix.agent.core.shell.DefaultScriptExecutor;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.SemaphoreWrapper;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;
import com.dianping.phoenix.agent.core.task.processor.kernel.DefaultDeployStep;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployStep;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployWorkflow;
import com.dianping.phoenix.agent.core.task.processor.kernel.DetachTaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.MockQaService;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService;
import com.dianping.phoenix.agent.core.tx.FileBasedTransactionManager;
import com.dianping.phoenix.agent.core.tx.LogFormatter;
import com.dianping.phoenix.agent.core.tx.TransactionManager;
import com.dianping.phoenix.agent.util.url.DefaultUrlContentFetcher;
import com.dianping.phoenix.agent.util.url.UrlContentFetcher;
import com.dianping.phoenix.configure.ConfigManager;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(UrlContentFetcher.class, DefaultUrlContentFetcher.class).req(ConfigManager.class));
		all.add(C(SemaphoreWrapper.class, "kernel", SemaphoreWrapper.class));
		all.add(C(LogFormatter.class));
		all.add(C(DeployWorkflow.class).is(PER_LOOKUP));
		all.add(C(QaService.class, MockQaService.class));
		all.add(C(DeployStep.class, DefaultDeployStep.class) //
		      .req(ConfigManager.class).req(ScriptExecutor.class) //
		      .req(QaService.class).is(PER_LOOKUP));
		all.add(C(TransactionManager.class, FileBasedTransactionManager.class));
		all.add(C(ScriptExecutor.class, DefaultScriptExecutor.class));
		all.add(C(ConfigManager.class));
		all.add(C(Agent.class, DefaultAgent.class).req(TransactionManager.class) //
		      .req(TaskProcessorFactory.class));
		all.add(C(TaskProcessor.class, "deploy", DeployTaskProcessor.class) //
		      .req(SemaphoreWrapper.class, "kernel").req(TransactionManager.class) //
		      .req(DeployWorkflow.class).req(LogFormatter.class));
		all.add(C(TaskProcessor.class, "detach", DetachTaskProcessor.class) //
		      .req(SemaphoreWrapper.class, "kernel").req(TransactionManager.class) //
		      .req(ConfigManager.class));
		all.add(C(TaskProcessorFactory.class));
		all.add(C(AgentStatusReporter.class).req(ConfigManager.class).req(ScriptExecutor.class));
		all.add(C(TransactionManager.class, FileBasedTransactionManager.class));

		all.add(C(ModuleManager.class, DefaultModuleManager.class));

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}
