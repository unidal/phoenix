package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public enum DeployStep {
	
	START(-1, 500, 0),

	INIT(0, 500, 1),

	CHECK_ARGUMENT(1, 500, 2) {

		@Override
		protected void doActivity(Context ctx) {
			moveTo(ctx, INJECT_PHOENIX_LOADER);
		}

	},

	INJECT_PHOENIX_LOADER(2, 80, 10) {

		@Override
		protected void doActivity(Context ctx) {
			try {
				ctx.injectPhoenixContextLoader();
				moveTo(ctx, GET_KERNEL_WAR);
			} catch (Exception e) {
				moveTo(ctx, ROLLBACK);
				logger.error(String.format("error in step %s", this), e);
			}
		}

	},

	GET_KERNEL_WAR(10, 80, 20),

	TURN_OFF_TRAFFIC(20, 80, 30),

	STOP_CONTAINER(30, 80, 40),

	UPGRADE_KERNEL(40, 80, 50),

	START_CONTAINER(50, 80, 60),

	CHECK_CONTAINER_STATUS(60, 80, 70),

	COMMIT(70, 500, 200),

	ROLLBACK(80, 500, 500),

	SUCCESS(200, 500) {

		@Override
		protected void doActivity(Context ctx) {
			try {
				ctx.setStatus(Status.SUCCESS);
				ctx.writeHeader(this, "successful");
				ctx.writeLogChunkSeparator();
				ctx.writeLogChunkTerminator();
			} catch (Exception e) {
				logger.error(String.format("error in step %s", this), e);
				moveTo(ctx, FAILED);
			}
		}

	},

	FAILED(500) {

		@Override
		protected void doActivity(Context ctx) {
			try {
				ctx.setStatus(Status.FAILED);
				ctx.writeHeader(this, "failed");
				ctx.writeLogChunkSeparator();
				ctx.writeLogChunkTerminator();
			} catch (Exception e) {
				logger.error(String.format("error in step %s", this), e);
			}
		}

	};

	private final static int FAILED_ID_IDX = 0;
	private final static int SUCCESS_ID_IDX = 1;

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

		nextStep.doActivity(ctx);
	}

	public static void execute(Context ctx) {
		START.moveTo(ctx, INIT);
	}

	protected void doActivity(Context ctx) {
		int exitCode = -1;
		try {
			exitCode = ctx.runShellCmd(this);
		} catch (Exception e) {
			logger.error(String.format("error in step %s", this), e);
		}
		if (exitCode != 0) {
			if (m_nextIds.length > FAILED_ID_IDX) {
				moveTo(ctx, get(m_nextIds[FAILED_ID_IDX]));
			}
		} else {
			if (m_nextIds.length > SUCCESS_ID_IDX) {
				moveTo(ctx, get(m_nextIds[SUCCESS_ID_IDX]));
			}
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
