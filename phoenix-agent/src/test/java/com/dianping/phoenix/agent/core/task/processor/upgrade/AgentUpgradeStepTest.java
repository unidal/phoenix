package com.dianping.phoenix.agent.core.task.processor.upgrade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Engine;
import com.dianping.phoenix.agent.core.task.workflow.Step;

public class AgentUpgradeStepTest extends ComponentTestCase {
	private List<AgentStep> presetList = new ArrayList<AgentStep>();
	private List<AgentStep> branchList = new ArrayList<AgentStep>();

	private MockStepProvider stepProvider = new MockStepProvider();

	private enum AgentStep {
		INIT, GITPULL, DRYRUN, UPGRADE, SUCCESS, FAIL
	}

	private class MockStepProvider implements AgentUpgradeStepProvider {
		private boolean initStatus = true;
		private boolean gitpullStatus = true;
		private boolean dryrunStatus = true;
		private boolean upgradeStatus = true;

		public void setInitFail() {
			initStatus = false;
		}

		public void setGitpullFail() {
			gitpullStatus = false;
		}

		public void setDryrunFail() {
			dryrunStatus = false;
		}

		public void setUpgradeFail() {
			upgradeStatus = false;
		}

		@Override
		public int upgradeAgent(Context ctx) throws Exception {
			branchList.add(AgentStep.UPGRADE);
			return upgradeStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int init(Context ctx) throws Exception {
			branchList.add(AgentStep.INIT);
			return initStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int gitPull(Context ctx) throws Exception {
			branchList.add(AgentStep.GITPULL);
			return gitpullStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int dryrunAgent(Context ctx) throws Exception {
			branchList.add(AgentStep.DRYRUN);
			return dryrunStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int checkArgument(Context ctx) throws Exception {
			return Step.CODE_OK;
		}
	};

	@Test
	public void testInitFail() {
		presetList.clear();
		presetList.add(AgentStep.INIT);
		presetList.add(AgentStep.FAIL);

		stepProvider.setInitFail();
		doTest();
	}

	@Test
	public void testGitpullFail() {
		presetList.clear();
		presetList.add(AgentStep.INIT);
		presetList.add(AgentStep.GITPULL);
		presetList.add(AgentStep.FAIL);

		stepProvider.setGitpullFail();
		doTest();
	}

	@Test
	public void testDryrunFail() {
		presetList.clear();
		presetList.add(AgentStep.INIT);
		presetList.add(AgentStep.GITPULL);
		presetList.add(AgentStep.DRYRUN);
		presetList.add(AgentStep.FAIL);

		stepProvider.setDryrunFail();
		doTest();
	}

	@Test
	public void testUpgradeFail() {
		presetList.clear();
		presetList.add(AgentStep.INIT);
		presetList.add(AgentStep.GITPULL);
		presetList.add(AgentStep.DRYRUN);
		presetList.add(AgentStep.UPGRADE);
		presetList.add(AgentStep.FAIL);

		stepProvider.setUpgradeFail();
		doTest();
	}

	@Test
	public void testSuccess() {
		presetList.clear();
		presetList.add(AgentStep.INIT);
		presetList.add(AgentStep.GITPULL);
		presetList.add(AgentStep.DRYRUN);
		presetList.add(AgentStep.UPGRADE);
		presetList.add(AgentStep.SUCCESS);

		doTest();
	}

	private void doTest() {
		Engine engine = null;
		try {
			engine = lookup(Engine.class);
		} catch (Exception e) {
			// ignore it
		}
		AgentUpgradeContext ctx = mock(AgentUpgradeContext.class);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		when(ctx.getLogOut()).thenReturn(out);
		when(ctx.getStepProvider()).thenReturn(stepProvider);
		branchList.clear();
		branchList.add(engine.start(AgentUpgradeStep.START, ctx) == Step.CODE_OK ? AgentStep.SUCCESS : AgentStep.FAIL);
		Assert.assertArrayEquals(presetList.toArray(), branchList.toArray());
	}
}
