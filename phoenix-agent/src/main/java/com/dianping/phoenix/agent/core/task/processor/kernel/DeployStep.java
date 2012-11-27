package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public enum DeployStep {

	INIT(-1, 0) {
		@Override
		protected void doActivity(Context ctx) {
			// do nothing
		}
	},

	CHECK_ARGUMENT(0, 1, 80) {

		@Override
		protected void doActivity(Context ctx) {
			moveTo(ctx, INJECT_PHOENIX_LOADER);
		}

	},

	INJECT_PHOENIX_LOADER(1, 10, 80) {

		@Override
		protected void doActivity(Context ctx) throws Exception {
			ctx.injectPhoenixContextLoader();
			moveTo(ctx, GET_KERNEL_WAR);
		}

	},

	GET_KERNEL_WAR(10, 20, 80),

	TURN_OFF_TRAFFIC(20, 30, 80),

	STOP_CONTAINER(30, 40, 80),

	UPGRADE_KERNEL(40, 50, 80),

	START_CONTAINER(50, 60, 80),

	CHECK_CONTAINER_STATUS(60, 70, 80),

	COMMIT(70, 200, 500),

	ROLLBACK(80, 500),

	SUCCESS(200) {

		@Override
		protected void doActivity(Context ctx) throws Exception {
			ctx.setStatus(Status.SUCCESS);
			ctx.writeHeader(this, "successful");
			ctx.writeLogChunkSeparator();
			ctx.writeLogChunkTerminator();
		}

	},

	FAILED(500) {

		@Override
		protected void doActivity(Context ctx) throws Exception {
			ctx.setStatus(Status.FAILED);
			ctx.writeHeader(this, "failed");
			ctx.writeLogChunkSeparator();
			ctx.writeLogChunkTerminator();
		}

	};

	private final static Logger logger = Logger.getLogger(DeployStep.class);
	
	private int m_id;

	private int[] m_nextIds;

	private DeployStep(int id, int... nextIds) {
		m_id = id;
		m_nextIds = nextIds;
	}

	public int getId() {
		return m_id;
	}

	public static DeployStep get(int id) {
		for (DeployStep deployStep : DeployStep.values()) {
			if (deployStep.getId() == id) {
				return deployStep;
			}
		}
		return null;
	}
	
	public String getProgressInfo() {
		return String.format("%s/%s", m_id > 100 ? 100 : m_id + 1, 100);
	}

	void moveTo(Context ctx, DeployStep nextStep) {

		int nextId = nextStep.getId();
		boolean found = false;
		for (int id : m_nextIds) {
			if (id == nextId) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new IllegalStateException(String.format("Can't move deploy step from %s to %s", this, nextStep));
		} else {
			ctx.setStep(nextStep);
		}

		try {
			nextStep.doActivity(ctx);
		} catch (Exception e) {
			logger.error(String.format("error in step %s", nextStep), e);
			this.activityFailed(ctx);
		}
	}
	
	private void activityFailed(Context ctx) {
		try {
			ctx.writeHeader(this, "failed");
		} catch (Exception e) {
			logger.error("error write header", e);
		}
		if(this == FAILED || this == SUCCESS) {
			logger.error("error in terminator steps, please check");
		} else if (this == ROLLBACK || this == COMMIT) {
			this.moveTo(ctx, FAILED);
		} else {
			this.moveTo(ctx, ROLLBACK);
		}
	}

	public static void execute(Context ctx) {
		INIT.moveTo(ctx, CHECK_ARGUMENT);
	}

	protected void doActivity(Context ctx) throws Exception {
		int exitCode = -1;
		try {
			exitCode = ctx.runShellCmd(this);
		} catch (Exception e) {
			logger.error(String.format("error in step %s", this), e);
		}
		if (exitCode != 0) {
			activityFailed(ctx);
		} else {
			moveTo(ctx, get(this.m_nextIds[0]));
		}
	}

	public static interface Context {

		public abstract int runShellCmd(DeployStep deployStep) throws Exception;

		public abstract void setStep(DeployStep step);

		public abstract DeployStep getStep();

		public abstract void setStatus(Status status);

		public abstract Status getStatus();

		public abstract void injectPhoenixContextLoader() throws Exception;

		public void kill(TransactionId txId);

		void writeLogChunkTerminator() throws IOException;

		void writeHeader(DeployStep curStep, String status) throws IOException;

		void writeLogChunkSeparator() throws IOException;

	}

}
