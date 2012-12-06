package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public class DeployWorkflowTestConfigurator extends AbstractResourceConfigurator {

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(MockDeployStep.class));
		all.add(C(DeployStep.class, MockDeployStep.class).is(PER_LOOKUP));
		all.add(C(DeployWorkflow.class).is(PER_LOOKUP));
		
		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return DeployWorkflowTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new DeployWorkflowTestConfigurator());
	}

}
