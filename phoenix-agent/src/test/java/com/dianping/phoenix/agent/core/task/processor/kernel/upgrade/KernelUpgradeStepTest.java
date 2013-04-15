package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

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

public class KernelUpgradeStepTest extends ComponentTestCase {
	private List<KernelStep> presetList = new ArrayList<KernelStep>();
	private List<KernelStep> branchList = new ArrayList<KernelStep>();
	private MockStepProvider stepProvider = new MockStepProvider();

	private enum KernelStep {
		INIT, CHECK_ARGUMENT, INJECT_PHOENIX_LOADER, GET_KERNEL_WAR, STOP_ALL, UPGRADE_KERNEL, START_CONTAINER, CHECK_CONTAINER_STATUS, COMMIT, ROLLBACK, SUCCESS, FAIL
	}

	private class MockStepProvider implements KernelUpgradeStepProvider {
		private boolean initSataus = true;
		private boolean checkArgumentStatus = true;
		private boolean injectPhoenixLoaderStatus = true;
		private boolean getKernelWarStatus = true;
		private boolean stopAllStatus = true;
		private boolean upgradeKernelStatus = true;
		private boolean startContainerStatus = true;
		private boolean checkContainerStatusStatus = true;
		private boolean commitStatus = true;
		private boolean rollbackStatus = true;

		public void setInitFail() {
			this.initSataus = false;
		}

		public void setCheckArgumentFail() {
			this.checkArgumentStatus = false;
		}

		public void setInjectPhoenixLoaderFail() {
			this.injectPhoenixLoaderStatus = false;
		}

		public void setGetKernelWarFail() {
			this.getKernelWarStatus = false;
		}

		public void setStopAllFail() {
			this.stopAllStatus = false;
		}

		public void setUpgradeKernelFail() {
			this.upgradeKernelStatus = false;
		}

		public void setStartContainerFail() {
			this.startContainerStatus = false;
		}

		public void setCheckContainerStatusFail() {
			this.checkContainerStatusStatus = false;
		}

		public void setCommitFail() {
			this.commitStatus = false;
		}

		public void setRollbackFail() {
			this.rollbackStatus = false;
		}

		@Override
		public int init(Context ctx) throws Exception {
			branchList.add(KernelStep.INIT);
			return initSataus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int checkArgument(Context ctx) throws Exception {
			branchList.add(KernelStep.CHECK_ARGUMENT);
			return checkArgumentStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int injectPhoenixLoader(Context ctx) throws Exception {
			branchList.add(KernelStep.INJECT_PHOENIX_LOADER);
			return injectPhoenixLoaderStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int getKernelWar(Context ctx) throws Exception {
			branchList.add(KernelStep.GET_KERNEL_WAR);
			return getKernelWarStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int stopAll(Context ctx) throws Exception {
			branchList.add(KernelStep.STOP_ALL);
			return stopAllStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int upgradeKernel(Context ctx) throws Exception {
			branchList.add(KernelStep.UPGRADE_KERNEL);
			return upgradeKernelStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int startContainer(Context ctx) throws Exception {
			branchList.add(KernelStep.START_CONTAINER);
			return startContainerStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int checkContainerStatus(Context ctx) throws Exception {
			branchList.add(KernelStep.CHECK_CONTAINER_STATUS);
			return checkContainerStatusStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int commit(Context ctx) throws Exception {
			branchList.add(KernelStep.COMMIT);
			return commitStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}

		@Override
		public int rollback(Context ctx) throws Exception {
			branchList.add(KernelStep.ROLLBACK);
			return rollbackStatus ? Step.CODE_OK : Step.CODE_ERROR;
		}
	}

	@Test
	public void testInitFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.FAIL);

		stepProvider.setInitFail();
		doTest();
	}

	@Test
	public void testCheckArgumentFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.FAIL);

		stepProvider.setCheckArgumentFail();
		doTest();
	}

	@Test
	public void testInjectPhoenixLoaderFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.ROLLBACK);
		presetList.add(KernelStep.FAIL);

		stepProvider.setInjectPhoenixLoaderFail();
		doTest();

		stepProvider.setRollbackFail();
		doTest();
	}

	@Test
	public void testGetKernelWarFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.ROLLBACK);
		presetList.add(KernelStep.FAIL);

		stepProvider.setGetKernelWarFail();
		doTest();

		stepProvider.setRollbackFail();
		doTest();
	}

	@Test
	public void testStopAllFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.STOP_ALL);
		presetList.add(KernelStep.ROLLBACK);
		presetList.add(KernelStep.FAIL);

		stepProvider.setStopAllFail();
		doTest();

		stepProvider.setRollbackFail();
		doTest();
	}

	@Test
	public void testUpgradeKernelFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.STOP_ALL);
		presetList.add(KernelStep.UPGRADE_KERNEL);
		presetList.add(KernelStep.ROLLBACK);
		presetList.add(KernelStep.FAIL);

		stepProvider.setUpgradeKernelFail();
		doTest();

		stepProvider.setRollbackFail();
		doTest();
	}

	@Test
	public void testStartContainerFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.STOP_ALL);
		presetList.add(KernelStep.UPGRADE_KERNEL);
		presetList.add(KernelStep.START_CONTAINER);
		presetList.add(KernelStep.ROLLBACK);
		presetList.add(KernelStep.FAIL);

		stepProvider.setStartContainerFail();
		doTest();

		stepProvider.setRollbackFail();
		doTest();
	}

	@Test
	public void testCheckContainerStatusFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.STOP_ALL);
		presetList.add(KernelStep.UPGRADE_KERNEL);
		presetList.add(KernelStep.START_CONTAINER);
		presetList.add(KernelStep.CHECK_CONTAINER_STATUS);
		presetList.add(KernelStep.ROLLBACK);
		presetList.add(KernelStep.FAIL);

		stepProvider.setCheckContainerStatusFail();
		doTest();

		stepProvider.setRollbackFail();
		doTest();
	}

	@Test
	public void testCommitFail() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.STOP_ALL);
		presetList.add(KernelStep.UPGRADE_KERNEL);
		presetList.add(KernelStep.START_CONTAINER);
		presetList.add(KernelStep.CHECK_CONTAINER_STATUS);
		presetList.add(KernelStep.COMMIT);
		presetList.add(KernelStep.FAIL);

		stepProvider.setCommitFail();
		doTest();
	}

	@Test
	public void testSuccess() {
		presetList.clear();
		presetList.add(KernelStep.INIT);
		presetList.add(KernelStep.CHECK_ARGUMENT);
		presetList.add(KernelStep.INJECT_PHOENIX_LOADER);
		presetList.add(KernelStep.GET_KERNEL_WAR);
		presetList.add(KernelStep.STOP_ALL);
		presetList.add(KernelStep.UPGRADE_KERNEL);
		presetList.add(KernelStep.START_CONTAINER);
		presetList.add(KernelStep.CHECK_CONTAINER_STATUS);
		presetList.add(KernelStep.COMMIT);
		presetList.add(KernelStep.SUCCESS);

		doTest();
	}

	private void doTest() {
		Engine engine = null;
		try {
			engine = lookup(Engine.class);
		} catch (Exception e) {
			// ignore it
		}
		KernelUpgradeContext ctx = mock(KernelUpgradeContext.class);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		when(ctx.getLogOut()).thenReturn(out);
		when(ctx.getStepProvider()).thenReturn(stepProvider);
		branchList.clear();
		branchList.add(engine.start(KernelUpgradeStep.START, ctx) == Step.CODE_OK ? KernelStep.SUCCESS
				: KernelStep.FAIL);
		Assert.assertArrayEquals(branchList.toArray(), presetList.toArray());
	}
}
