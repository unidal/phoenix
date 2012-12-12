package com.dianping.phoenix.agent.page.deploy;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpHeaders;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.AgentStatusReporter;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.SubmitResult;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;
import com.dianping.phoenix.agent.core.task.processor.kernel.DetachTask;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.transform.DefaultJsonBuilder;
import com.dianping.phoenix.agent.util.CharacterReplaceFilterWriter;
import com.dianping.phoenix.agent.util.ThreadUtil;

public class Handler implements PageHandler<Context> {

	private final static Logger logger = Logger.getLogger(Handler.class);

	@Inject
	private Agent agent;
	@Inject
	private TransactionManager txMgr;
	@Inject
	private AgentStatusReporter agentStatusReporter;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "deploy")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "deploy")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();

		Response res = new Response();
		if (ctx.hasErrors()) {
			res.setStatus("error");
			// at most one error
			res.setMessage(ctx.getErrors().get(0).getCode());
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			return;
		}

		String version = payload.getVersion();
		String domain = payload.getDomain();
		long deployId = payload.getDeployId();
		int offset = payload.getOffset();
		TransactionId txId = new TransactionId(deployId);
		Task task;
		SubmitResult submitResult;

		switch (payload.getAction()) {
		case DEFAULT:
			res.setStatus("ok");
			try {
				res = agentStatusReporter.report();
			} catch (Exception e) {
				logger.error("error get agent status", e);
				res.setStatus("error");
				res.setMessage("internal server error");
			}
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;

		case DEPLOY:
			task = new DeployTask(domain, version);
			submitResult = submitTask(task, txId);
			res.setStatus(submitResult.isAccepted() ? "ok" : "error");
			res.setMessage(submitResult.getMsg());
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;

		case STATUS:
			if (txMgr.transactionExists(txId)) {
				try {
					Transaction tx = txMgr.loadTransaction(txId);
					res.setStatus(tx.getStatus().toString().toLowerCase());
				} catch (Exception e) {
					logger.error("error get transaction status", e);
					res.setStatus("error");
					res.setMessage("internal server error");
				}
			} else {
				res.setStatus("error");
				res.setMessage("not found");
			}
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;

		case CANCEL:
			res.setStatus("ok");
			agent.cancel(txId);
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;

		case GETLOG:
			Reader logReader = agent.getLogReader(txId, offset);
			while (logReader == null && agent.isTransactionProcessing(txId)) {
				ThreadUtil.sleepQuiet(1000);
				logReader = agent.getLogReader(txId, offset);
			}

			if (logReader != null) {
				HttpServletResponse resp = ctx.getHttpServletResponse();
				resp.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
				Writer writer = resp.getWriter();
				if (payload.getBr() > 0) {
					writer = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
				}
				transferLog(txMgr, txId, logReader, writer);
			} else {
				res.setStatus("notfound");
				res.setMessage("error");
				ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			}
			break;

		case DETACH:
			task = new DetachTask(domain);
			submitResult = submitTask(task, txId);
			res.setStatus(submitResult.isAccepted() ? "ok" : "error");
			res.setMessage(submitResult.getMsg());
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;
		}
	}

	private SubmitResult submitTask(Task task, TransactionId txId) {
		Transaction tx = new Transaction(task, txId, EventTracker.DUMMY_TRACKER);
		SubmitResult submitResult = new SubmitResult(false, "");
		try {
			submitResult = agent.submit(tx);
		} catch (Exception e) {
			logger.error("error submit transaction " + tx, e);
		}
		return submitResult;
	}

	private void transferLog(TransactionManager txMgr, TransactionId txId, Reader logReader, Writer writer)
			throws IOException {
		int len;
		char[] cbuf = new char[4096];
		if (logReader != null) {
			while (true) {
				len = logReader.read(cbuf);
				if (len > 0) {
					writer.write(cbuf, 0, len);
					writer.flush();
				} else {
					Transaction tx = txMgr.loadTransaction(txId);
					if (tx.getStatus().isCompleted()) {
						break;
					} else {
						ThreadUtil.sleepQuiet(1000);
					}
				}
			}
		}
	}
}
