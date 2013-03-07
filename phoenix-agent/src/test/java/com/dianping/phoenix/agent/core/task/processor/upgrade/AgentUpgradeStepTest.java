package com.dianping.phoenix.agent.core.task.processor.upgrade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.task.workflow.Engine;
import com.dianping.phoenix.agent.core.task.workflow.Step;

public class AgentUpgradeStepTest extends ComponentTestCase {

	@Test
	public void testSuccess() throws Exception {
		Engine engine = lookup(Engine.class);
		AgentUpgradeContext ctx = mock(AgentUpgradeContext.class);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		when(ctx.getLogOut()).thenReturn(out);
		StepProvider stepProvider = mock(StepProvider.class);
		when(ctx.getStepProvider()).thenReturn(stepProvider);
		
		engine.start(AgentUpgradeStep.START.getNextStep(Step.CODE_OK), ctx);
	}

}
