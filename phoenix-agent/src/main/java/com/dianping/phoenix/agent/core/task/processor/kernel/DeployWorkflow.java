package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.dianping.phoenix.agent.core.tx.LogFormatter;

public class DeployWorkflow {

	private final static Logger logger = Logger.getLogger(DeployWorkflow.class);

	private Context ctx;
	private DeployStep steps;

	@SuppressWarnings("unused")
	private class Context {
		private int exitcode;
		private Step endStep;
		private OutputStream logOut;
		private AtomicBoolean killed = new AtomicBoolean(false);
		private LogFormatter logFormatter;

		public Context(OutputStream logOut, LogFormatter logFormatter) {
			this.logOut = logOut;
			this.logFormatter = logFormatter;
		}
		
		public LogFormatter getLogFormatter() {
			return logFormatter;
		}

		public OutputStream getLogOut() {
			return logOut;
		}

		public int getExitcode() {
			return exitcode;
		}

		public void setExitcode(int exitcode) {
			this.exitcode = exitcode;
		}

		public void setEndStep(Step endStep) {
			this.endStep = endStep;
		}

		public boolean isKilled() {
			return killed.get();
		}

		public void kill() {
			killed.set(true);
		}

	}

	private enum Step {

		BEFORE_START(-1, 500, 0) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				// will never be called
				return DeployStep.CODE_OK;
			}
		},

		INIT(0, 500, 1) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.init();
			}
		},

		CHECK_ARGUMENT(1, 500, 2) {

			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.checkArgument();
			}

		},

		INJECT_PHOENIX_LOADER(2, 80, 10) {

			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.injectPhoenixLoader();
			}

		},

		GET_KERNEL_WAR(10, 80, 30) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.getKernelWar();
			}
		},

		STOP_ALL(30, 80, 40) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.stopAll();
			}
		},

		UPGRADE_KERNEL(40, 80, 50) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.upgradeKernel();
			}
		},

		START_CONTAINER(50, 80, 60) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.startContainer();
			}
		},

		CHECK_CONTAINER_STATUS(60, 80, 70) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.checkContainerStatus();
			}
		},

		COMMIT(70, 500, 200) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.commit();
			}
		},

		ROLLBACK(80, 500, 500) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.rollback();
			}
		},

		SUCCESS(200, 500, null) {

			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				ctx.setExitcode(DeployStep.CODE_OK);
				ctx.setEndStep(SUCCESS);

				return DeployStep.CODE_OK;
			}

		},

		FAILED(500, null, null) {

			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				ctx.setExitcode(DeployStep.CODE_ERROR);
				ctx.setEndStep(FAILED);

				return DeployStep.CODE_ERROR;
			}

		};

		private final static Logger logger = Logger.getLogger(Step.class);

		private int id;
		private Integer nextIdWhenFail;
		private Integer nextIdWhenSuccess;

		private Step(int id, Integer nextIdWhenFail, Integer nextIdWhenSuccess) {
			this.id = id;
			this.nextIdWhenFail = nextIdWhenFail;
			this.nextIdWhenSuccess = nextIdWhenSuccess;
		}

		public int getId() {
			return id;
		}

		public static Step get(int id) {
			for (Step deployStep : Step.values()) {
				if (deployStep.getId() == id) {
					return deployStep;
				}
			}
			return null;
		}

		protected abstract int doStep(DeployStep steps, Context ctx) throws Exception;

		public String getProgressInfo() {
			return String.format("%s/%s", id > 100 ? 100 : id + 1, 100);
		}

		void moveTo(DeployStep steps, Step nextStep, OutputStream logOut, Context ctx) {

			if (ctx.isKilled()) {
				return;
			}

			if (!canMoveTo(nextStep)) {
				throw new IllegalStateException(String.format("Can't move deploy step from %s to %s", this, nextStep));
			}

			writeLogChunkHeader(ctx, nextStep);
			int exitCode = DeployStep.CODE_ERROR;
			logger.info(String.format("current step %s", nextStep));
			try {
				exitCode = nextStep.doStep(steps, ctx);
			} catch (Exception e) {
				logger.error(String.format("error doing step %s", nextStep), e);
			}

			String stepResultMsg = "";
			Step stepToGo = null;
			if (exitCode != DeployStep.CODE_OK) {
				stepResultMsg = "STEP FAIL";
				if (nextStep.nextIdWhenFail != null) {
					stepToGo = get(nextStep.nextIdWhenFail);
				}
			} else {
				stepResultMsg = "STEP SUCCESS";
				if (nextStep.nextIdWhenSuccess != null) {
					stepToGo = get(nextStep.nextIdWhenSuccess);
				}
			}
			
			logger.info(stepResultMsg);
			writeLogChunkTerminator(ctx, nextStep);
			if(stepToGo != null) {
				nextStep.moveTo(steps, stepToGo, logOut, ctx);
			}
		}

		private boolean canMoveTo(Step nextStep) {
			return nextIdWhenFail == nextStep.getId() || nextIdWhenSuccess == nextStep.getId();
		}

		private void writeLogChunkTerminator(Context ctx, Step step) {
			try {
				ctx.getLogFormatter().writeChunkTerminator(ctx.getLogOut());
			} catch (Exception e) {
				logger.error(String.format("error write log chunk terminator for %s", step), e);
			}
		}

		private static void writeLogChunkHeader(Context ctx, Step step) {

			Map<String, String> headers = new HashMap<String, String>();
			
			headers.put("Progress", step.getProgressInfo());
			headers.put("Step", step.toString());

			if (step == SUCCESS || step == FAILED) {
				headers.put("Status", step == SUCCESS ? "successful" : "failed");
			}

			try {
				ctx.getLogFormatter().writeHeader(ctx.getLogOut(), headers);
			} catch (Exception e) {
				logger.error(String.format("error write log chunk header for %s", step), e);
			}
		}
	}

	private void writeLogTerminator(Context ctx) {
		try {
			ctx.getLogFormatter().writeTerminator(ctx.getLogOut());
		} catch (Exception e) {
			logger.error(String.format("error write log terminator for %s", this), e);
		}
	}

	public int start(DeployTask task, DeployStep steps, OutputStream logOut, LogFormatter logFormatter) {
		this.steps = steps;
		steps.prepare(task, logOut);
		ctx = new Context(logOut, logFormatter);
		Step.BEFORE_START.moveTo(steps, Step.INIT, logOut, ctx);
		writeLogTerminator(ctx);
		return ctx.getExitcode();
	}

	public boolean kill() {
		boolean killed = true;
		try {
			ctx.kill();
			steps.kill();
		} catch (Exception e) {
			killed = false;
		}
		return killed;
	}

}
