package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.util.Map;

import com.dianping.phoenix.agent.core.task.workflow.AbstractStep;
import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Step;

public class AgentUpgradeStep extends AbstractStep {

	private static AgentUpgradeStep FAIL = new AgentUpgradeStep(null, null, 4) {

		@Override
		public Map<String, String> getLogChunkHeader() {
			Map<String, String> header = super.getLogChunkHeader();
			header.put(HEADER_STATUS, STATUS_FAIL);
			return header;
		}

	};

	private static AgentUpgradeStep SUCCESS = new AgentUpgradeStep(null, null, 4) {

		@Override
		public Map<String, String> getLogChunkHeader() {
			Map<String, String> header = super.getLogChunkHeader();
			header.put(HEADER_STATUS, STATUS_SUCCESS);
			return header;
		}

	};

	private static AgentUpgradeStep UPGRADE_AGENT = new AgentUpgradeStep(SUCCESS, FAIL, 3) {

		@Override
		public int doStep(Context ctx) throws Exception {
			AgentUpgradeContext myCtx = (AgentUpgradeContext) ctx;
			return myCtx.getStepProvider().upgradeAgent(myCtx);
		}

	};

	private static AgentUpgradeStep DRYRUN_AGENT = new AgentUpgradeStep(UPGRADE_AGENT, FAIL, 2) {

		@Override
		public int doStep(Context ctx) throws Exception {
			AgentUpgradeContext myCtx = (AgentUpgradeContext) ctx;
			return myCtx.getStepProvider().dryrunAgent(myCtx);
		}

	};

	private static AgentUpgradeStep GIT_PULL = new AgentUpgradeStep(DRYRUN_AGENT, FAIL, 1) {

		@Override
		public int doStep(Context ctx) throws Exception {
			AgentUpgradeContext myCtx = (AgentUpgradeContext) ctx;
			return myCtx.getStepProvider().gitPull(myCtx);
		}

	};

	public static AgentUpgradeStep START = new AgentUpgradeStep(GIT_PULL, GIT_PULL, 0);

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
