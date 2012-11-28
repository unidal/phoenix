package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.unidal.dal.jdbc.DalException;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.deploy.agent.Context;
import com.dianping.phoenix.deploy.agent.Listener;
import com.dianping.phoenix.deploy.agent.Progress;
import com.dianping.phoenix.deploy.agent.SegmentReader;
import com.dianping.phoenix.deploy.agent.State;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.site.helper.Files;
import com.site.helper.Threads;

public class DefaultDeployExecutor implements DeployExecutor, Listener {
	private static ExecutorService s_threadPool = Threads.forPool().getFixedThreadPool("Phoenix-Deploy", 50);

	@Inject
	private ConfigManager m_configManager;

	@Inject
	private DeploymentDetailsDao m_detailsDao;

	@Inject
	private DeployPolicy m_policy;

	private Map<Integer, DeployModel> m_models = new HashMap<Integer, DeployModel>();

	@Override
	public DeployModel getModel(int deployId) {
		return m_models.get(deployId);
	}

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}

	@Override
	public void onEnd(Context ctx, String status) {
		DeploymentDetails details = m_detailsDao.createLocal();

		try {
			if ("successful".equals(status)) {
				details.setStatus(3); // 3 - successful
			} else if ("failed".equals(status)) {
				details.setStatus(5); // 5 - failed
			} else {
				throw new RuntimeException(String.format("Internal error: unknown status(%s)!", status));
			}

			details.setEndDate(new Date());
			m_detailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
		} catch (DalException e) {
			throw new RuntimeException("Error when updating deployment details table! " + e, e);
		}
	}

	@Override
	public void onProgress(Context ctx, Progress progress, String log) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart(Context ctx) {
		DeploymentDetails details = m_detailsDao.createLocal();

		try {
			details.setStatus(2); // 2 - deploying
			details.setBeginDate(new Date());
			m_detailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
		} catch (DalException e) {
			throw new RuntimeException("Error when updating deployment details table! " + e, e);
		}
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public synchronized void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError) {
		DeployModel model = new DeployModel();

		model.setId(deployId).setDomain(name).setVersion(version).setAbortOnError(abortOnError);

		for (String host : hosts) {
			model.addHost(new HostModel().setIp(host));
		}

		ControllerTask task = new ControllerTask(m_configManager, m_policy, this, model, hosts);

		m_models.put(deployId, model);
		Threads.forGroup("Phoenix").start(task);
	}

	static class ControllerTask implements Task {
		private ConfigManager m_configManager;

		private DeployPolicy m_policy;

		private StringBuilder m_content = new StringBuilder(8192);

		private List<String> m_hosts;

		private int m_hostIndex;

		private boolean m_active;

		private Listener m_listener;

		private DeployModel m_model;

		public ControllerTask(ConfigManager configManager, DeployPolicy policy, Listener listener, DeployModel model,
		      List<String> hosts) {
			m_configManager = configManager;
			m_policy = policy;
			m_listener = listener;
			m_model = model;
			m_hosts = hosts;
		}

		public ConfigManager getConfigManager() {
			return m_configManager;
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		public ControllerTask log(String pattern, Object... args) {
			m_content.append(String.format(pattern, args));
			return this;
		}

		@Override
		public void run() {
			m_active = true;

			CountDownLatch latch = submitNextRolloutTask(1);

			try {
				while (m_active) {
					if (latch == null) {
						break;
					}

					boolean done = latch.await(5, TimeUnit.MILLISECONDS);

					if (done) {
						int batchSize = m_policy.getBatchSize();

						latch = submitNextRolloutTask(batchSize);
					}
				}
			} catch (InterruptedException e) {
				// ignore it
			}
		}

		@Override
		public void shutdown() {
			m_active = false;
		}

		private CountDownLatch submitNextRolloutTask(int maxCount) {
			CountDownLatch latch = new CountDownLatch(maxCount);
			int len = m_hosts.size();
			int count = 0;

			while (m_hostIndex < len) {
				String host = m_hosts.get(m_hostIndex++);

				s_threadPool.submit(new RolloutTask(this, m_listener, m_model, host, latch));
				count++;
			}

			for (int i = count; i < maxCount; i++) {
				latch.countDown();
			}

			if (count == 0) { // no more hosts
				return null;
			} else {
				return latch;
			}
		}

		public void update(Progress progress, String segment) {
			// TODO Auto-generated method stub
			System.out.println(progress + "\r\n" + segment);
		}
	}

	static class RolloutContext implements Context {
		private ControllerTask m_controller;

		private Listener m_listener;

		private DeployModel m_model;

		private State m_state;

		private String m_host;

		private int m_retryCount;

		public RolloutContext(ControllerTask controller, Listener listener, DeployModel model, String host) {
			m_controller = controller;
			m_listener = listener;
			m_model = model;
			m_host = host;
		}

		@Override
		public ConfigManager getConfigManager() {
			return m_controller.getConfigManager();
		}

		@Override
		public int getDeployId() {
			return m_model.getId();
		}

		@Override
		public String getDomain() {
			return m_model.getDomain();
		}

		@Override
		public String getHost() {
			return m_host;
		}

		@Override
		public int getRetryCount() {
			return m_retryCount;
		}

		@Override
		public State getState() {
			return m_state;
		}

		@Override
		public String getVersion() {
			return m_model.getVersion();
		}

		@Override
		public String openUrl(String url) throws IOException {
			if (url.contains("?op=deploy&")) {
				String content = Files.forIO().readFrom(new URL(url).openStream(), "utf-8");

				return content;
			} else if (url.contains("?op=log&")) {
				SegmentReader sr = new SegmentReader(new InputStreamReader(new URL(url).openStream(), "utf-8"));
				Progress progress = new Progress();

				while (sr.hasNext()) {
					String segment = sr.next(progress);

					m_listener.onProgress(this, progress, segment);
				}

				return "";
			} else {
				throw new IllegalStateException(String.format("Not implemented yet(%s)!", url));
			}
		}

		@Override
		public Context print(String pattern, Object... args) {
			m_controller.log(pattern, args);
			return this;
		}

		@Override
		public Context println() {
			m_controller.log("\r\n");
			return this;
		}

		@Override
		public Context println(String pattern, Object... args) {
			m_controller.log(pattern, args);
			m_controller.log("\r\n");
			return this;
		}

		@Override
		public void setRetryCount(int retryCount) {
			m_retryCount = retryCount;
		}

		@Override
		public void setState(State state) {
			m_state = state;

			switch (state) {
			case CREATED:
				m_listener.onStart(this);
				break;
			case SUCCESSFUL:
				m_listener.onEnd(this, "success");
				break;
			case FAILED:
				m_listener.onEnd(this, "failed");
				break;
			}
		}
	}

	static class RolloutTask implements Task {
		private RolloutContext m_ctx;

		private CountDownLatch m_latch;

		public RolloutTask(ControllerTask controller, Listener listener, DeployModel model, String host,
		      CountDownLatch latch) {
			m_ctx = new RolloutContext(controller, listener, model, host);
			m_latch = latch;
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public void run() {
			try {
				State.execute(m_ctx);
			} catch (Throwable e) {
				m_ctx.print("Deployment aborted due to: %s.\r\n", e);

				StringWriter sw = new StringWriter();
				PrintWriter writer = new PrintWriter(sw);
				e.printStackTrace(writer);

				m_ctx.println(sw.toString());
			} finally {
				m_latch.countDown();
			}
		}

		@Override
		public void shutdown() {
		}
	}
}
