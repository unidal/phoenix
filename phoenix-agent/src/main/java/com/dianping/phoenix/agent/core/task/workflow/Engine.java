package com.dianping.phoenix.agent.core.task.workflow;

import java.io.OutputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.tx.LogFormatter;

public class Engine {

	private static Logger logger = Logger.getLogger(Engine.class);

	@Inject
	LogFormatter logFormatter;

	public int start(Step startStep, Context ctx) {
		Step curStep = startStep;
		int exitCode = 0;
		OutputStream logOut = ctx.getLogOut();
		while (curStep != null) {
			if (ctx.isKilled()) {
				break;
			}
			writeLogChunkHeader(curStep.getLogChunkHeader(), logOut);
			logger.info(String.format("current step %s", curStep));
			try {
				exitCode = curStep.doStep(ctx);
			} catch (Exception e) {
				logger.error(String.format("exception when doing step %s", curStep), e);
				exitCode = Step.CODE_ERROR;
			}
			logger.info(String.format("step %s", exitCode == Step.CODE_OK ? "success" : "fail"));
			writeLogChunkTerminator(logOut);
			curStep = curStep.getNextStep(exitCode);
		}
		writeLogTerminator(logOut);
		return exitCode;
	}

	private void writeLogChunkHeader(Map<String, String> headers, OutputStream logOut) {
		try {
			logFormatter.writeHeader(logOut, headers);
		} catch (Exception e) {
			logger.error("error write log chunk header", e);
		}
	}

	private void writeLogChunkTerminator(OutputStream logOut) {
		try {
			logFormatter.writeChunkTerminator(logOut);
		} catch (Exception e) {
			logger.error("error write log chunk terminator", e);
		}
	}

	private void writeLogTerminator(OutputStream logOut) {
		try {
			logFormatter.writeTerminator(logOut);
		} catch (Exception e) {
			logger.error("error write log terminator", e);
		}
	}

	public boolean kill(Context ctx) {
		return ctx.kill();
	}
}
