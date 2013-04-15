package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.util.Map;

import com.dianping.phoenix.agent.core.task.workflow.AbstractStep;
import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Step;

public class AgentUpgradeStep extends AbstractStep {

	private static AgentUpgradeStep FAIL = new AgentUpgradeStep(null, null, 5) {

		@Override
		public Map<String, String> getLogChunkHeader() {
			Map<String, String> header = super.getLogChunkHeader();
			header.put(HEADER_STATUS, STATUS_FAIL);
			return header;
		}

		@Override
		public int doStep(Context ctx) throws Exception {
			return Step.CODE_ERROR;
		}

		@Override
		public String toString() {
			return "FAILED";
		}

	};

	private static AgentUpgradeStep SUCCESS = new AgentUpgradeStep(null, null, 5) {

		@Override
		public Map<String, String> getLogChunkHeader() {
			Map<String, String> header = super.getLogChunkHeader();
			header.put(HEADER_STATUS, STATUS_SUCCESS);
			return header;
		}

		@Override
		public int doStep(Context ctx) throws Exception {
			return Step.CODE_OK;
		}

		@Override
		public String toString() {
			return "SUCCESS";
		}

	};

	private static AgentUpgradeStep UPGRADE_AGENT = new AgentUpgradeStep(SUCCESS, FAIL, 4) {

		@Override
		public int doStep(Context ctx) throws Exception {
			return getProvider(ctx).upgradeAgent(ctx);
		}

		@Override
		public String toString() {
			return "UPGRADE_AGENT";
		}

	};

	private static AgentUpgradeStep DRYRUN_AGENT = new AgentUpgradeStep(UPGRADE_AGENT, FAIL, 3) {

		@Override
		public int doStep(Context ctx) throws Exception {
			return getProvider(ctx).dryrunAgent(ctx);
		}

		@Override
		public String toString() {
			return "DRYRUN_AGENT";
		}

	};

	private static AgentUpgradeStep GIT_PULL = new AgentUpgradeStep(DRYRUN_AGENT, FAIL, 2) {

		@Override
		public int doStep(Context ctx) throws Exception {
			return getProvider(ctx).gitPull(ctx);
		}

		@Override
		public String toString() {
			return "GIT_PULL";
		}
	};

	public static AgentUpgradeStep INIT = new AgentUpgradeStep(GIT_PULL, FAIL, 1) {

		@Override
		public int doStep(Context ctx) throws Exception {
			return getProvider(ctx).init(ctx);
		}

		@Override
		public String toString() {
			return "INIT";
		}

	};

	public static AgentUpgradeStep START = new AgentUpgradeStep(INIT, FAIL, 0) {

		@Override
		public String toString() {
			return "START";
		}
	};

	private static AgentUpgradeStepProvider getProvider(Context ctx) {
		return ((AgentUpgradeContext) ctx).getStepProvider();
	}

	private AgentUpgradeStep(AgentUpgradeStep nextStepWhenSuccess, AgentUpgradeStep nextStepWhenFail, int stepSeq) {
		super(nextStepWhenSuccess, nextStepWhenFail, stepSeq);
	}

	@Override
	protected int getTotalStep() {
		return 4;
	}

	@Override
	public int doStep(Context ctx) throws Exception {
		return Step.CODE_OK;
	}

}
