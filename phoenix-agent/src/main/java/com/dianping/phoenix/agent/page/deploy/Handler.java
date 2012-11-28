package com.dianping.phoenix.agent.page.deploy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.HttpHeaders;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.StringUtils;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.SubmitResult;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;
import com.dianping.phoenix.agent.response.entity.Container;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;
import com.dianping.phoenix.agent.response.transform.DefaultJsonBuilder;
import com.dianping.phoenix.agent.util.CharacterReplaceFilterWriter;
import com.dianping.phoenix.agent.util.ThreadUtil;

public class Handler implements PageHandler<Context> {
	
	private final static Logger logger = Logger.getLogger(Handler.class);
	
	@Inject
	private Agent agent;
	@Inject
	private TransactionManager txMgr;

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

		String version = payload.getVersion();
		String domain = payload.getDomain();
		long deployId = payload.getDeployId();
		int offset = payload.getOffset();

		Response res = new Response();

		TransactionId txId = new TransactionId(deployId);
		switch (payload.getAction()) {
		case DEFAULT:
			res.setStatus("ok");
			Container container = new Container();
			container.setInstallPath("/usr/local/tomcat");
			container.setName("tomcat");
			container.setStatus("up");
			container.setVersion("6.0.35");
			res.setContainer(container);
			res.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
			War war = new War();
			war.setName("kernel");
			war.setVersion("0.9.0");
			Lib lib = new Lib();
			lib.setGroupId("g");
			lib.setArtifactId("a");
			lib.setVersion("v");
			war.addLib(lib);
			res.addWar(war);
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;
		case DEPLOY:
			if (deployArgumentValid(version, domain)) {
				res.setStatus("error");
				res.setMessage("More argument required");
			} else {
				Task task = new DeployTask(domain, version);
				Transaction tx = new Transaction(task, txId, EventTracker.DUMMY_TRACKER);
				SubmitResult submitResult = new SubmitResult(false, "");
				try {
					submitResult = agent.submit(tx);
				} catch (Exception e) {
					logger.error("error submit transaction " + tx, e);
				}
				res.setStatus(submitResult.isAccepted() ? "ok" : "error");
				res.setMessage(submitResult.getMsg());
			}
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;
		case STATUS:
			res.setStatus("done");
			if (txMgr.transactionExists(txId)) {
				try {
					Transaction tx = txMgr.loadTransaction(txId);
					res.setStatus(tx.getStatus().toString().toLowerCase());
				} catch (Exception e) {
					res.setStatus("error");
				}

			} else {
				res.setStatus("notfound");
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
			while(logReader == null && agent.isTransactionProcessing(txId)) {
				ThreadUtil.sleepQuiet(1000);
				logReader = agent.getLogReader(txId, offset);
			}
			HttpServletResponse resp = ctx.getHttpServletResponse();
			resp.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
			Writer writer = resp.getWriter();
			if(payload.getBr() > 0) {
				writer = new CharacterReplaceFilterWriter(writer, '\n', "<br/>");
			}
			transferLog(txMgr, txId, logReader, writer);
			break;
		}
	}

	private boolean deployArgumentValid(String version, String domain) {
		return StringUtils.isEmpty(StringUtils.trimAll(domain)) || StringUtils.isEmpty(StringUtils.trimAll(version));
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
