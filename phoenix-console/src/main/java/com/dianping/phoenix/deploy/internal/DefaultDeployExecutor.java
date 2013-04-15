package com.dianping.phoenix.deploy.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.helper.Files;
import org.unidal.helper.Formats;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.helper.Urls;
import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Pair;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.DeployConstant;
import com.dianping.phoenix.deploy.DeployExecutor;
import com.dianping.phoenix.deploy.DeployListener;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.deploy.agent.AgentContext;
import com.dianping.phoenix.deploy.agent.AgentListener;
import com.dianping.phoenix.deploy.agent.AgentProgress;
import com.dianping.phoenix.deploy.agent.AgentReader;
import com.dianping.phoenix.deploy.agent.AgentState;
import com.dianping.phoenix.deploy.agent.AgentStatus;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;

public class DefaultDeployExecutor implements DeployExecutor, LogEnabled {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private DeployListener m_deployListener;

	@Inject
	private AgentListener m_agentListener;

	@Inject
	private DeployPolicy m_policy;

	private Logger m_logger;

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public synchronized void submit(DeployModel model, List<String> hosts, String warType) throws Exception {
		ControllerTask task = new ControllerTask(m_agentListener, model, hosts, warType);

		m_deployListener.onDeployStart(model.getId());
		Threads.forGroup("Phoenix").start(task);
	}

	class ControllerTask implements Task {
		private List<String> m_hosts;

		private int m_hostIndex;

		private boolean m_active;

		private DeployModel m_model;

		private AgentListener m_listener;

		private String m_warType;

		public ControllerTask(AgentListener listener, DeployModel model, List<String> hosts, String warType) {
			m_listener = listener;
			m_model = model;
			m_hosts = hosts;
			m_warType = warType;
		}

		private void cancelResetTasks() {
			int len = m_hosts.size();

			while (m_hostIndex < len) {
				String host = m_hosts.get(m_hostIndex++);

				try {
					String message;

					if (m_configManager.isShowLogTimestamp()) {
						String timestamp = Formats.forObject().format(new Date(), "yyyy-MM-dd HH:mm:ss");

						message = String.format("[%s] Rollout to host(%s) cancelled due to error happened.", timestamp,
								host);
					} else {
						message = String.format("Rollout to host(%s) cancelled due to error happened.", host);
					}

					RolloutContext ctx = new RolloutContext(this, m_agentListener, m_model, m_warType, host);

					ctx.updateStatus(AgentStatus.CANCELLED, message);
					m_deployListener.onHostCancel(m_model.getId(), host);

					log("Rolling out to host(%s) ... CANCELLED.", host);
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
			return String.format("%s-%s", getClass().getSimpleName(), m_model.getDomain());
		}

		private ControllerTask log(String pattern, Object... args) {
			HostModel host = m_model.findHost(DeployConstant.SUMMARY);
			String text;

			if (m_configManager.isShowLogTimestamp()) {
				String timestamp = Formats.forObject().format(new Date(), "yyyy-MM-dd HH:mm:ss");

				text = "[" + timestamp + "] " + String.format(pattern, args);
			} else {
				text = String.format(pattern, args);
			}

			host.addSegment(new SegmentModel().setText(text));
			return this;
		}

		@Override
		public void run() {
			m_active = true;

			Pair<CountDownLatch, List<String>> pair = submitNextRolloutTask(1);

			try {
				while (m_active) {
					if (pair == null) { // no more hosts
						break;
					}

					boolean done = pair.getKey().await(10, TimeUnit.MILLISECONDS);

					if (done) {
						for (String ip : pair.getValue()) {
							HostModel host = m_model.findHost(ip);
							String status = host.getStatus();

							try {
								m_deployListener.onHostEnd(m_model.getId(), ip);

								log("Rolling out to host(%s) ... %s", ip, status.toUpperCase());
							} catch (Exception e) {
								m_logger.warn(
										String.format("Error when processing onHostEnd(%s) of deploy(%s)!", ip,
												m_model.getId()), e);
							}
						}

						if (shouldStop()) {
							cancelResetTasks();
							break;
						} else {
							int batchSize = m_policy.getBatchSize();

							pair = submitNextRolloutTask(batchSize);
						}
					}
				}
			} catch (InterruptedException e) {
				// ignore it
			} finally {
				try {
					m_deployListener.onDeployEnd(m_model.getId());
				} catch (Exception e) {
					m_logger.warn(String.format("Error when processing onEnd of deploy(%s)!", m_model.getId()), e);
				}
			}
		}

		private boolean shouldStop() {
			boolean abortOnError = m_model.getPlan().isAbortOnError();

			if (abortOnError) {
				for (HostModel host : m_model.getHosts().values()) {
					for (SegmentModel segment : host.getSegments()) {
						String status = segment.getStatus();

						if (status != null && AgentStatus.FAILED.getName().equals(status)) {
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

		private Pair<CountDownLatch, List<String>> submitNextRolloutTask(int maxCount) {
			CountDownLatch latch = new CountDownLatch(maxCount);
			int len = m_hosts.size();
			int count = 0;
			List<String> hosts = new ArrayList<String>();

			while (m_hostIndex < len && count < maxCount) {
				String host = m_hosts.get(m_hostIndex++);

				Threads.forGroup("Phoenix").start(new RolloutTask(this, m_listener, m_model, m_warType, host, latch));
				hosts.add(host);
				count++;

				log("Rolling out to host(%s) ...", host);
			}

			for (int i = count; i < maxCount; i++) {
				latch.countDown();
			}

			if (count == 0) { // no more hosts
				return null;
			} else {
				return new Pair<CountDownLatch, List<String>>(latch, hosts);
			}
		}
	}

	static class RolloutContext implements AgentContext {
		private ControllerTask m_controller;

		private AgentListener m_listener;

		private DeployModel m_model;

		private AgentState m_state;

		private AgentStatus m_status;

		private HostModel m_host;

		private int m_retriedCount;

		private StringBuilder m_log = new StringBuilder(256);

		private String m_warType;

		public RolloutContext(ControllerTask controller, AgentListener listener, DeployModel model, String warType,
				String host) {
			m_controller = controller;
			m_listener = listener;
			m_model = model;
			m_warType = warType;
			m_host = model.findHost(host);
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
		public DeployModel getDeployModel() {
			return m_model;
		}

		@Override
		public String getDomain() {
			return m_model.getDomain();
		}

		@Override
		public String getHost() {
			return m_host.getIp();
		}

		@Override
		public int getId() {
			return m_host.getId();
		}

		@Override
		public String getRawLog() {
			return new DeployModel().addHost(m_host).toString();
		}

		@Override
		public int getRetriedCount() {
			return m_retriedCount;
		}

		@Override
		public AgentState getState() {
			return m_state;
		}

		@Override
		public AgentStatus getStatus() {
			return m_status;
		}

		@Override
		public String getVersion() {
			return m_model.getPlan().getVersion();
		}

		@Override
		public boolean isSkipTest() {
			return m_model.getPlan().isSkipTest();
		}

		@Override
		public String openUrl(String url) throws IOException {
			ConfigManager configManager = m_controller.getConfigManager();
			int timeout = configManager.getDeployConnectTimeout();

			if (url.contains("?op=deploy&")) {
				InputStream in = Urls.forIO().connectTimeout(timeout).openStream(url);
				String content = Files.forIO().readFrom(in, "utf-8");

				return content;
			} else if (url.contains("?op=log&")) {
				AgentReader sr = new AgentReader(new PhoenixInputStreamReader(url, timeout, configManager.getDeployGetlogRetrycount()));
				AgentProgress progress = new AgentProgress();

				while (sr.hasNext()) {
					String segment = "";
					segment = sr.next(progress);

					try {
						m_listener.onProgress(this, progress, segment);

						if ("failed".equals(progress.getStatus())) {
							updateStatus(AgentStatus.FAILED, segment);
						}
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
		public AgentContext print(String pattern, Object... args) {
			if (m_log.length() == 0 && m_controller.getConfigManager().isShowLogTimestamp()) {
				String timestamp = Formats.forObject().format(new Date(), "yyyy-MM-dd HH:mm:ss");

				m_log.append("[").append(timestamp).append("] ");
			}

			String message = String.format(pattern, args);

			m_log.append(message);
			return this;
		}

		@Override
		public AgentContext println() {
			if (m_log.length() > 0) {
				m_host.addSegment(new SegmentModel().setText(m_log.toString()));
				m_log.setLength(0);
			}

			return this;
		}

		@Override
		public AgentContext println(String pattern, Object... args) {
			println();

			if (m_controller.getConfigManager().isShowLogTimestamp()) {
				String timestamp = Formats.forObject().format(new Date(), "yyyy-MM-dd HH:mm:ss");
				String message = String.format(pattern, args);

				m_host.addSegment(new SegmentModel().setText("[" + timestamp + "] " + message));
			} else {
				String message = String.format(pattern, args);

				m_host.addSegment(new SegmentModel().setText(message));
			}

			return this;
		}

		@Override
		public void setRetriedCount(int retriedCount) {
			m_retriedCount = retriedCount;
		}

		@Override
		public void setState(AgentState state) {
			m_state = state;

			try {
				switch (state) {
				case CREATED:
					m_listener.onStart(this);
					break;
				case SUCCESSFUL:
					m_listener.onEnd(this, AgentStatus.SUCCESS);
					break;
				case FAILED:
					m_listener.onEnd(this, AgentStatus.FAILED);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void updateStatus(AgentStatus status, String message) {
			String text;

			if (m_controller.getConfigManager().isShowLogTimestamp()) {
				String timestamp = Formats.forObject().format(new Date(), "yyyy-MM-dd HH:mm:ss");

				text = "[" + timestamp + "] " + message;
			} else {
				text = message;
			}

			m_status = status;
			m_host.setStatus(status.getName());
			m_host.addSegment(new SegmentModel().setStatus(status.getName()) //
					.setCurrentTicks(100).setTotalTicks(100).setStep(status.getTitle()).setText(text));
		}

		@Override
		public String getWarType() {
			return m_warType;
		}
	}

	static class RolloutTask implements Task {
		private RolloutContext m_ctx;

		private CountDownLatch m_latch;

		public RolloutTask(ControllerTask controller, AgentListener listener, DeployModel model, String warType,
				String host, CountDownLatch latch) {
			m_ctx = new RolloutContext(controller, listener, model, warType, host);
			m_latch = latch;
		}

		@Override
		public String getName() {
			return String.format("%s-%s-%s", getClass().getSimpleName(), m_ctx.getDomain(), m_ctx.getHost());
		}

		@Override
		public void run() {
			try {
				AgentState.execute(m_ctx);
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
