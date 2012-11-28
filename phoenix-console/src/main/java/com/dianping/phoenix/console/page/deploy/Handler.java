package com.dianping.phoenix.console.page.deploy;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;

import com.dianping.phoenix.deploy.DeployLog;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;

public class Handler implements PageHandler<Context> {
	@Inject
	private DeployManager m_deployManager;

	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private KeepAliveViewer m_statusViewer;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "deploy")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "deploy")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		model.setAction(action);
		model.setPage(ConsolePage.DEPLOY);

		switch (action) {
		case VIEW:
			try {
				showView(model, payload.getId());
			} catch (Exception e) {
				ctx.addError("deploy.query", e);
			}

			m_jspViewer.view(ctx, model);
			break;
		case STATUS:
//			try {
//				showStatus(model, payload.getId());
//			} catch (Exception e) {
//				ctx.addError("deploy.status", e);
//			}
//
//			m_statusViewer.view(ctx, model);
            m_jspViewer.view(ctx, model);
			break;
		}
	}

	private void showStatus(Model model, int id) {

	}

	private void showView(Model model, int id) throws Exception {
		Deployment deployment = m_deployManager.query(id);
		DeployPlan plan = new DeployPlan();

		plan.setVersion(deployment.getWarVersion());
		plan.setPolicy(deployment.getStrategy());
		plan.setAbortOnError("abortOnError".equals(deployment.getErrorPolicy()));
		model.setName(deployment.getDomain());
		model.setPlan(plan);

		List<String> hosts = new ArrayList<String>();

		for (DeploymentDetails details : deployment.getDetailsList()) {
			hosts.add(details.getIpAddress());
		}

//		model.setHosts(hosts);
        model.setPlanStatus("doing");
        model.setHostStatus(mockHostStatus());
        model.setLogs(mockDeployLogs());
	}

    private Map<String, DeployLog> mockDeployLogs() {
        Map<String, DeployLog> deployLogs = new TreeMap<String, DeployLog>();
        deployLogs.put("192.168.8.40", new MockDeployLog(mockDeployLog(), 5));
        deployLogs.put("192.168.8.41", new MockDeployLog("line1<br />line2<br />line3<br />line4<br />line5<br />", 5));
        deployLogs.put("192.168.8.42", new MockDeployLog("", 0));
        deployLogs.put("192.168.8.43", new MockDeployLog("", 0));
        return deployLogs;
    }

    private String mockDeployLog() {
        return "16:10:50,393 INFO  [Server] Starting JBoss (MX MicroKernel)...<br />" +
                "16:10:50,394 INFO  [Server] Release ID: JBoss [Trinity] 4.2.2.GA (build: SVNTag=JBoss_4_2_2_GA date=200710221139)<br />" +
                "16:10:50,394 DEBUG [Server] Using config: org.jboss.system.server.ServerConfigImpl@51f6f27b<br />" +
                "16:10:50,394 DEBUG [Server] Server type: class org.jboss.system.server.ServerImpl<br />" +
                "16:10:50,394 DEBUG [Server] Server loaded through: org.jboss.system.server.NoAnnotationURLClassLoader<br />" +
                "16:10:50,395 DEBUG [Server] Boot URLs:<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/endorsed/serializer.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/endorsed/xalan.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/endorsed/xercesImpl.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/jboss-jmx.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/concurrent.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/log4j-boot.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/jboss-common.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/jboss-system.jar<br />" +
                "16:10:50,395 DEBUG [Server]   file:/usr/local/jboss/lib/jboss-xml-binding.jar<br />" +
                "16:10:50,395 INFO  [Server] Home Dir: /usr/local/jboss<br />" +
                "16:10:50,395 INFO  [Server] Home URL: file:/usr/local/jboss/<br />" +
                "16:10:50,395 DEBUG [Server] Library URL: file:/usr/local/jboss/lib/\n";
    }

    private List<HostDeployStatus> mockHostStatus() {
        List<HostDeployStatus> statusList = new ArrayList<HostDeployStatus>();
        statusList.add(createHostDeployStatus("192.168.8.40", "shutdown jboss", 10, "doing"));
        statusList.add(createHostDeployStatus("192.168.8.41", "fetch kernel", 30, "doing"));
        statusList.add(createHostDeployStatus("192.168.8.42", "", 0, "pending"));
        statusList.add(createHostDeployStatus("192.168.8.43", "", 0, "pending"));
        return statusList;
    }

    private HostDeployStatus createHostDeployStatus(String host, String action, int progress, String status) {
        HostDeployStatus deployStatus = new HostDeployStatus();
        deployStatus.setHost(host);
        deployStatus.setAction(action);
        deployStatus.setProgress(progress);
        deployStatus.setStatus(status);
        return deployStatus;
    }

    public static class MockDeployLog implements DeployLog {

        private String m_content;

        private int m_offset;

        MockDeployLog(String content, int offset) {
            this.m_content = content;
            this.m_offset = offset;
        }

        public String getContent() {
            return m_content;
        }

        public void setContent(String content) {
            this.m_content = content;
        }

        public int getOffset() {
            return m_offset;
        }

        public void setOffset(int offset) {
            this.m_offset = offset;
        }
    }
}
