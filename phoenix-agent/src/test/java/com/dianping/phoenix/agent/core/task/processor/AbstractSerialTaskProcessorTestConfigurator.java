package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.agent.core.tx.MockTransactionManager;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public class AbstractSerialTaskProcessorTestConfigurator extends AbstractResourceConfigurator {

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(TransactionManager.class, MockTransactionManager.class).is(PER_LOOKUP));
		all.add(C(SemaphoreWrapper.class, "A", SemaphoreWrapper.class));
		all.add(C(SemaphoreWrapper.class, "B", SemaphoreWrapper.class));
		all.add(C(MockTaskProcessorA1.class).req(SemaphoreWrapper.class, "A") //
				.req(TransactionManager.class).is(PER_LOOKUP));
		all.add(C(MockTaskProcessorA2.class).req(SemaphoreWrapper.class, "A") //
				.req(TransactionManager.class).is(PER_LOOKUP));
		all.add(C(MockTaskProcessorB.class).req(SemaphoreWrapper.class, "B") //
				.req(TransactionManager.class).is(PER_LOOKUP));

		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return AbstractSerialTaskProcessorTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new AbstractSerialTaskProcessorTestConfigurator());
	}

}
