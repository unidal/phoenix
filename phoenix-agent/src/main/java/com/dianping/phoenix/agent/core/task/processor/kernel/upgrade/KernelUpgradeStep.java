package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

import java.util.Map;

import com.dianping.phoenix.agent.core.task.workflow.AbstractStep;
import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Step;

public class KernelUpgradeStep extends AbstractStep {

	protected KernelUpgradeStep(AbstractStep nextStepWhenSuccess, AbstractStep nextStepWhenFail, int stepSeq) {
		super(nextStepWhenSuccess, nextStepWhenFail, stepSeq);
	}

	private static KernelUpgradeStep FAILED = new KernelUpgradeStep(null, null, 11) {
		@Override
		public int doStep(Context ctx) throws Exception {
			KernelUpgradeContext myCtx = (KernelUpgradeContext) ctx;
			myCtx.setEndStep(FAILED);
			myCtx.setExitCode(Step.CODE_ERROR);
			return Step.CODE_ERROR;
		}

		@Override
		public Map<String, String> getLogChunkHeader() {
			Map<String, String> header = super.getLogChunkHeader();
			header.put(HEADER_STATUS, STATUS_FAIL);
			return header;
		}

		@Override
		public String toString() {
			return "FAILED";
		}

	};

	private static KernelUpgradeStep SUCCESS = new KernelUpgradeStep(null, null, 11) {
		@Override
		public int doStep(Context ctx) throws Exception {
			KernelUpgradeContext myCtx = (KernelUpgradeContext) ctx;
			myCtx.setEndStep(SUCCESS);
			myCtx.setExitCode(Step.CODE_OK);
			return Step.CODE_OK;
		}

		@Override
		public Map<String, String> getLogChunkHeader() {
			Map<String, String> header = super.getLogChunkHeader();
			header.put(HEADER_STATUS, STATUS_SUCCESS);
			return header;
		}

		@Override
		public String toString() {
			return "SUCCESS";
		}
	};

	private static KernelUpgradeStep ROLLBACK = new KernelUpgradeStep(FAILED, FAILED, 10) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).rollback(ctx);
		}

		@Override
		public String toString() {
			return "ROLLBACK";
		}
	};

	private static KernelUpgradeStep COMMIT = new KernelUpgradeStep(SUCCESS, FAILED, 9) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).commit(ctx);
		}

		@Override
		public String toString() {
			return "COMMIT";
		}
	};

	private static KernelUpgradeStep CHECK_CONTAINER_STATUS = new KernelUpgradeStep(COMMIT, ROLLBACK, 8) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).checkContainerStatus(ctx);
		}

		@Override
		public String toString() {
			return "CHECK_CONTAINER_STATUS";
		}
	};

	private static KernelUpgradeStep START_CONTAINER = new KernelUpgradeStep(CHECK_CONTAINER_STATUS, ROLLBACK, 7) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).startContainer(ctx);
		}

		@Override
		public String toString() {
			return "START_CONTAINER";
		}
	};

	private static KernelUpgradeStep UPGRADE_KERNEL = new KernelUpgradeStep(START_CONTAINER, ROLLBACK, 6) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).upgradeKernel(ctx);
		}

		@Override
		public String toString() {
			return "UPGRADE_KERNEL";
		}
	};

	private static KernelUpgradeStep STOP_ALL = new KernelUpgradeStep(UPGRADE_KERNEL, ROLLBACK, 5) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).stopAll(ctx);
		}

		@Override
		public String toString() {
			return "STOP_ALL";
		}
	};

	private static KernelUpgradeStep GET_KERNEL_WAR = new KernelUpgradeStep(STOP_ALL, ROLLBACK, 4) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).getKernelWar(ctx);
		}

		@Override
		public String toString() {
			return "GET_KERNEL_WAR";
		}
	};

	private static KernelUpgradeStep INJECT_PHOENIX_LOADER = new KernelUpgradeStep(GET_KERNEL_WAR, ROLLBACK, 3) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).injectPhoenixLoader(ctx);
		}

		@Override
		public String toString() {
			return "INJECT_PHOENIX_LOADER";
		}
	};

	private static KernelUpgradeStep CHECK_ARGUMENT = new KernelUpgradeStep(INJECT_PHOENIX_LOADER, FAILED, 2) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).checkArgument(ctx);
		}

		@Override
		public String toString() {
			return "CHECK_ARGUMENT";
		}
	};

	private static KernelUpgradeStep INIT = new KernelUpgradeStep(CHECK_ARGUMENT, FAILED, 1) {
		@Override
		public int doStep(Context ctx) throws Exception {
			return getStepProvider(ctx).init(ctx);
		}

		@Override
		public String toString() {
			return "INIT";
		}
	};

	public static KernelUpgradeStep START = new KernelUpgradeStep(INIT, FAILED, 0) {
		@Override
		public String toString() {
			return "START";
		}
	};

	private static KernelUpgradeStepProvider getStepProvider(Context ctx) {
		return ((KernelUpgradeContext) ctx).getStepProvider();
	}

	@Override
	public int doStep(Context ctx) throws Exception {
		return Step.CODE_OK;
	}

	@Override
	protected int getTotalStep() {
		return 11;
	}
}
