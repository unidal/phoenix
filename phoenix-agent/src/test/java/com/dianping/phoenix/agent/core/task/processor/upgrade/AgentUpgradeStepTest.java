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
	private List<AgentStep> preList = new ArrayList<AgentStep>();
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
	public void testInitFail() throws Exception {
		preList.clear();
		preList.add(AgentStep.INIT);
		preList.add(AgentStep.FAIL);

		stepProvider.setInitFail();
		Assert.assertTrue(doTest());
	}

	@Test
	public void testGitpullFail() throws Exception {
		preList.clear();
		preList.add(AgentStep.INIT);
		preList.add(AgentStep.GITPULL);
		preList.add(AgentStep.FAIL);

		stepProvider.setGitpullFail();
		Assert.assertTrue(doTest());
	}

	@Test
	public void testDryrunFail() throws Exception {
		preList.clear();
		preList.add(AgentStep.INIT);
		preList.add(AgentStep.GITPULL);
		preList.add(AgentStep.DRYRUN);
		preList.add(AgentStep.FAIL);

		stepProvider.setDryrunFail();
		Assert.assertTrue(doTest());
	}

	@Test
	public void testUpgradeFail() throws Exception {
		preList.clear();
		preList.add(AgentStep.INIT);
		preList.add(AgentStep.GITPULL);
		preList.add(AgentStep.DRYRUN);
		preList.add(AgentStep.UPGRADE);
		preList.add(AgentStep.FAIL);

		stepProvider.setUpgradeFail();
		Assert.assertTrue(doTest());
	}

	@Test
	public void testSuccess() throws Exception {
		preList.clear();
		preList.add(AgentStep.INIT);
		preList.add(AgentStep.GITPULL);
		preList.add(AgentStep.DRYRUN);
		preList.add(AgentStep.UPGRADE);
		preList.add(AgentStep.SUCCESS);

		Assert.assertTrue(doTest());
	}

	private boolean doTest() throws Exception {
		Engine engine = lookup(Engine.class);
		AgentUpgradeContext ctx = mock(AgentUpgradeContext.class);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		when(ctx.getLogOut()).thenReturn(out);
		when(ctx.getStepProvider()).thenReturn(stepProvider);
		branchList.clear();
		branchList.add(engine.start(AgentUpgradeStep.START, ctx) == Step.CODE_OK ? AgentStep.SUCCESS : AgentStep.FAIL);
		return isArrayEquals(branchList, preList);
	}

	private boolean isArrayEquals(List<AgentStep> lList, List<AgentStep> rList) {
		if (lList.size() != rList.size()) {
			return false;
		}
		for (int idx = 0; idx < rList.size(); idx++) {
			if (lList.get(idx) != rList.get(idx)) {
				return false;
			}
		}
		return true;
	}
}
