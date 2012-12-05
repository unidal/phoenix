package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

public class DeployWorkflow {
	
	private final static Logger logger = Logger.getLogger(DeployWorkflow.class);

	private Context ctx;
	private DeployStep steps;
	private AtomicBoolean started = new AtomicBoolean(false);

	@SuppressWarnings("unused")
	private class Context {
		private int exitcode;
		private Step endStep;
		private OutputStream logOut;
		private AtomicBoolean killed = new AtomicBoolean(false);

		public Context(OutputStream logOut) {
			this.logOut = logOut;
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
				return steps.start();
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

		GET_KERNEL_WAR(10, 80, 20) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.getKernelWar();
			}
		},

		TURN_OFF_TRAFFIC(20, 80, 30) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.turnOffTraffic();
			}
		},

		STOP_CONTAINER(30, 80, 40) {
			@Override
			protected int doStep(DeployStep steps, Context ctx) throws Exception {
				return steps.stopContainer();
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

			writeLogChunkHeader(logOut, nextStep);
			int exitCode = DeployStep.CODE_ERROR;
			logger.info(String.format("current step %s", nextStep));
			try {
				exitCode = nextStep.doStep(steps, ctx);
			} catch (Exception e) {
				logger.error(String.format("error doing step %s", nextStep), e);
			}
			writeLogChunkTerminator(logOut, nextStep);

			if (exitCode != DeployStep.CODE_OK) {
				logger.info("failed");
				if (nextStep.nextIdWhenFail != null) {
					nextStep.moveTo(steps, get(nextStep.nextIdWhenFail), logOut, ctx);
				}
			} else {
				logger.info("successful");
				if (nextStep.nextIdWhenSuccess != null) {
					nextStep.moveTo(steps, get(nextStep.nextIdWhenSuccess), logOut, ctx);
				}
			}
		}

		private boolean canMoveTo(Step nextStep) {
			return nextIdWhenFail == nextStep.getId() || nextIdWhenSuccess == nextStep.getId();
		}

		private static void writeLogChunkTerminator(OutputStream logOut, Step step) {
			try {
				logOut.write("--9ed2b78c112fbd17a8511812c554da62941629a8--\r\n".getBytes("ascii"));
				logOut.flush();
			} catch (Exception e) {
				logger.error(String.format("error write log chunk terminator for %s", step), e);
			}
		}

		private static void writeLogChunkHeader(OutputStream logOut, Step step) {
			StringBuilder sb = new StringBuilder();
			
			sb.append("Progress: ");
			sb.append(step.getProgressInfo());
			
			sb.append("\r\n");
			sb.append("Step:");
			sb.append(step);

			if (step == SUCCESS || step == FAILED) {
				sb.append("\r\n");
				sb.append("Status: ");
				sb.append(step == SUCCESS ? "successful" : "failed");
			}

			sb.append("\r\n\r\n");

			try {
				logOut.write(sb.toString().getBytes());
			} catch (Exception e) {
				logger.error(String.format("error write log chunk header for %s", step), e);
			}
		}
	}
	
	private void writeLogTerminator(OutputStream logOut) {
		try {
			logOut.write("--255220d51dc7fb4aacddadedfe252a346da267d4--\r\n".getBytes("ascii"));
		} catch (Exception e) {
			logger.error(String.format("error write log terminator for %s", this), e);
		}
	}

	public int start(String domain, String kernelVersion, DeployStep steps, OutputStream logOut) {
		boolean notStarted = started.compareAndSet(false, true);
		if (notStarted) {
			this.steps = steps;
			steps.prepare(domain, kernelVersion, logOut);
			ctx = new Context(logOut);
			Step.BEFORE_START.moveTo(steps, Step.INIT, logOut, ctx);
			writeLogTerminator(logOut);
			return ctx.getExitcode();
		} else {
			throw new IllegalStateException("can not start workflow more than once");
		}
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
