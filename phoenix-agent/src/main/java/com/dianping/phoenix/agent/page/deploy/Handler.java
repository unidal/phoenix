package com.dianping.phoenix.agent.page.deploy;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.Event;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.war.Artifact;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;
import com.dianping.phoenix.agent.response.entity.Container;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;
import com.dianping.phoenix.agent.response.transform.DefaultJsonBuilder;

public class Handler implements PageHandler<Context> {
	@Inject
	private Agent agent;

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
			res.setIp("127.0.0.1");
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
			res.setStatus("ok");
			EventTracker eventTracker = new EventTracker() {

				@Override
				public void onEvent(Event event) {
					// TODO Auto-generated method stub

				}
			};
			Artifact artifactToUpdate = new Artifact(domain, "");
			Task task = new WarUpdateTask(artifactToUpdate, version);
			Transaction tx = new Transaction(task, txId, eventTracker);
			try {
				agent.submit(tx);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;
		case STATUS:
			res.setStatus("done");
			List<Transaction> txes = agent.currentTransactions();
			for (Transaction atx : txes) {
				if (atx.getTxId().equals(txId)) {
					res.setStatus("processing");
				}
			}
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;
		case CANCEL:
			res.setStatus("ok");
			agent.cancel(txId);
			ctx.getHttpServletResponse().getWriter().write(new DefaultJsonBuilder().buildJson(res));
			break;
		case GETLOG:
			final AtomicBoolean txCompleted = new AtomicBoolean(false);
			boolean attachSuccess = agent.attachEventTracker(txId, new AbstractEventTracker() {

				@Override
				protected void onLifecycleEvent(LifecycleEvent event) {
					if (event.getStatus().isCompleted()) {
						txCompleted.set(true);
					}
				}

			});
			if (!attachSuccess) {
				txCompleted.set(true);
			}
			char[] cbuf = new char[4096];
			Reader logReader = agent.getLog(txId, offset);
			HttpServletResponse resp = ctx.getHttpServletResponse();
			int len;
			if (logReader != null) {
				while (true) {
					
					if(resp.getWriter().checkError()) {
						break;
					}
					
					len = logReader.read(cbuf);
					if (len > 0) {
						resp.getWriter().write(cbuf, 0, len);
						resp.getWriter().flush();
					} else {
						if (txCompleted.get()) {
							break;
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			break;
		}
	}
}
