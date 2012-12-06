package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.unidal.helper.Files;
import org.unidal.helper.Formats;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.agent.Context;
import com.dianping.phoenix.deploy.agent.Progress;
import com.dianping.phoenix.deploy.agent.SegmentReader;
import com.dianping.phoenix.deploy.agent.State;
import com.dianping.phoenix.deploy.event.AgentListener;
import com.dianping.phoenix.deploy.event.DeployListener;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;

public class DefaultDeployExecutor implements DeployExecutor {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private DeployListener m_deployListener;

	@Inject
	private AgentListener m_agentListener;

	@Inject
	private DeployPolicy m_policy;

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public synchronized void submit(int deployId, List<String> hosts) throws Exception {
		DeployModel model = m_deployListener.getModel(deployId);
		ControllerTask task = new ControllerTask(m_agentListener, model, hosts);

		Threads.forGroup("Phoenix").start(task);
		m_deployListener.onDeployStart(deployId);
	}

	class ControllerTask implements Task {
		private static final String SUMMARY = "summary";

		private List<String> m_hosts;

		private int m_hostIndex;

		private boolean m_active;

		private DeployModel m_model;

		private AgentListener m_listener;

		public ControllerTask(AgentListener listener, DeployModel model, List<String> hosts) {
			m_listener = listener;
			m_model = model;
			m_hosts = hosts;
			m_model.addHost(new HostModel(SUMMARY));
		}

		private void cancelResetTasks() {
			int len = m_hosts.size();

			while (m_hostIndex < len) {
				String host = m_hosts.get(m_hostIndex++);
				RolloutContext ctx = new RolloutContext(this, m_agentListener, m_model, host);

				try {
					m_agentListener.onCancel(ctx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public ConfigManager getConfigManager() {
			return m_configManager;
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		public ControllerTask log(String message) {
			String timestamp = Formats.forObject().format(new Date(), "yyyy-MM-dd HH:mm:ss");
			HostModel host = m_model.findHost(SUMMARY);

			host.addSegment(new SegmentModel().setText("[" + timestamp + "] " + message));
			return this;
		}

		@Override
		public void run() {
			m_active = true;

			CountDownLatch latch = submitNextRolloutTask(1);

			try {
				while (m_active) {
					if (latch == null) { // no more hosts
						break;
					}

					boolean done = latch.await(10, TimeUnit.MILLISECONDS);

					if (done) {
						if (shouldStop()) {
							cancelResetTasks();
							latch = null;
						} else {
							int batchSize = m_policy.getBatchSize();

							latch = submitNextRolloutTask(batchSize);
						}
					}
				}
			} catch (InterruptedException e) {
				// ignore it
			} finally {
				if (latch == null) {
					try {
						m_deployListener.onDeployEnd(m_model.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		private boolean shouldStop() {
			boolean abortOnError = m_model.isAbortOnError();

			if (abortOnError) {
				for (HostModel host : m_model.getHosts().values()) {
					for (SegmentModel segment : host.getSegments()) {
						String status = segment.getStatus();

						if (status != null && "failed".equals(status)) {
							return true;
						}
					}
				}
			}

			return false;
		}

		@Override
		public void shutdown() {
			m_active = false;
		}

		private CountDownLatch submitNextRolloutTask(int maxCount) {
			CountDownLatch latch = new CountDownLatch(maxCount);
			int len = m_hosts.size();
			int count = 0;

			while (m_hostIndex < len && count < maxCount) {
				String host = m_hosts.get(m_hostIndex++);

				Threads.forGroup("Phoenix").start(new RolloutTask(this, m_listener, m_model, host, latch));
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
	}

	static class RolloutContext implements Context {
		private ControllerTask m_controller;

		private AgentListener m_listener;

		private DeployModel m_model;

		private State m_state;

		private int m_id;

		private String m_host;

		private int m_retryCount;

		private StringBuilder m_log = new StringBuilder(256);

		public RolloutContext(ControllerTask controller, AgentListener listener, DeployModel model, String host) {
			m_controller = controller;
			m_listener = listener;
			m_model = model;
			m_host = host;

			HostModel m = model.findHost(host);

			m_id = m.getId();
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
		public int getId() {
			return m_id;
		}

		@Override
		public String getRawLog() {
			HostModel host = m_model.findHost(m_host);

			return new DeployModel().addHost(host).toString();
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

					try {
						m_listener.onProgress(this, progress, segment);
					} catch (Exception e) {
						e.printStackTrace(); // TODO
					}
				}

				return "";
			} else {
				throw new IllegalStateException(String.format("Not implemented yet(%s)!", url));
			}
		}

		@Override
		public Context print(String pattern, Object... args) {
			String message = String.format(pattern, args);

			m_log.append(message);
			return this;
		}

		@Override
		public Context println() {
			m_controller.log(m_log.toString());
			m_log.setLength(0);
			return this;
		}

		@Override
		public Context println(String pattern, Object... args) {
			String message = String.format(pattern, args);

			m_log.append(message);
			m_controller.log(m_log.toString());
			m_log.setLength(0);
			return this;
		}

		@Override
		public void setRetryCount(int retryCount) {
			m_retryCount = retryCount;
		}

		@Override
		public void setState(State state) {
			m_state = state;

			try {
				switch (state) {
				case CREATED:
					m_listener.onStart(this);
					break;
				case SUCCESSFUL:
					m_listener.onEnd(this, "successful");
					break;
				case FAILED:
					m_listener.onEnd(this, "failed");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace(); // TODO
			}
		}

		@Override
		public void updateStatus(String status, String message) {
			HostModel host = m_model.findHost(m_host);

			host.addSegment(new SegmentModel().setStatus(status) //
			      .setCurrentTicks(100).setTotalTicks(100).setText(message));
		}
	}

	static class RolloutTask implements Task {
		private RolloutContext m_ctx;

		private CountDownLatch m_latch;

		public RolloutTask(ControllerTask controller, AgentListener listener, DeployModel model, String host,
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
