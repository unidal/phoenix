package com.dianping.phoenix.agent.page.deploy;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;

import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.DefaultAgent;
import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.log.InMemoryTransactionLog;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.war.Artifact;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;
import com.dianping.phoenix.agent.page.deploy.shell.Shell;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private Shell m_shell;

	Agent agent = new DefaultAgent();

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

		final OutputStream resOut = ctx.getHttpServletResponse().getOutputStream();

		EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				try {
					resOut.write(event.getStatus().toString().getBytes());
					resOut.write("<br/>".getBytes());
					resOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onMessageEvent(MessageEvent event) {
				try {
					resOut.write(event.getMsg().getBytes());
					resOut.write("<br/>".getBytes());
					resOut.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		};
		TransactionId txId = new TransactionId(11L);
		Artifact artifactToUpdate = new Artifact("user-web", "1.0");
		Task task = new WarUpdateTask(artifactToUpdate, "1.1");
		Transaction tx = new Transaction(task, txId, eventTracker, new InMemoryTransactionLog());
		agent.submit(tx);
	}
}
