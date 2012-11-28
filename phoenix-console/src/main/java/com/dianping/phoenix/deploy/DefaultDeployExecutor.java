package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.agent.DeployState;
import com.dianping.phoenix.deploy.agent.DeployState.Context;
import com.dianping.phoenix.deploy.agent.Progress;
import com.dianping.phoenix.deploy.agent.SegmentReader;
import com.site.helper.Files;
import com.site.helper.Threads;

public class DefaultDeployExecutor implements DeployExecutor {
	private static ExecutorService s_threadPool = Threads.forPool().getFixedThreadPool("Phoenix-Deploy", 50);

	@Inject
	private ConfigManager m_configManager;

	@Inject
	private DeployPolicy m_policy;

	private Map<Integer, ControllerTask> m_tasks = new HashMap<Integer, ControllerTask>();

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}

	@Override
	public DeployUpdate poll(DeployContext ctx) {
		ControllerTask task = m_tasks.get(ctx.getDeployId());
		DeployUpdate update = new DeployUpdate();

		// TODO if not in cache, get it from database

		if (task == null) {
			update.setDone(true);
		} else {
			task.poll(ctx, update);
		}

		return update;
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public synchronized void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError) {
		ControllerTask task = new ControllerTask(deployId, name, hosts, version, m_policy, abortOnError);

		task.setConfigManager(m_configManager);
		m_tasks.put(deployId, task);
		Threads.forGroup("Phoenix").start(task);
	}

	static class ControllerTask implements Task {
		private ConfigManager m_configManager;

		private int m_deployId;

		private String m_domain;

		private List<String> m_hosts;

		private String m_version;

		private DeployPolicy m_policy;

		private boolean m_abortOnError;

		private StringBuilder m_content = new StringBuilder(8192);

		private int m_hostIndex;

		private boolean m_active;

		public ControllerTask(int deployId, String domain, List<String> hosts, String version, DeployPolicy policy,
		      boolean abortOnError) {
			m_deployId = deployId;
			m_domain = domain;
			m_hosts = hosts;
			m_version = version;
			m_policy = policy;
			m_abortOnError = abortOnError;
		}

		public ConfigManager getConfigManager() {
			return m_configManager;
		}

		public int getDeployId() {
			return m_deployId;
		}

		public String getDomain() {
			return m_domain;
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		public DeployPolicy getPolicy() {
			return m_policy;
		}

		public String getVersion() {
			return m_version;
		}

		public boolean isAbortOnError() {
			return m_abortOnError;
		}

		public ControllerTask log(String pattern, Object... args) {
			m_content.append(String.format(pattern, args));
			return this;
		}

		public void poll(DeployContext ctx, DeployUpdate update) {
			int offset = ctx.getOffset();
			int len = m_content.length();

			update.setContent(m_content.substring(offset, len));
			ctx.setOffset(len);
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

		public void setConfigManager(ConfigManager configManager) {
			m_configManager = configManager;
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

				s_threadPool.submit(new RolloutTask(this, host, latch));
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

	static class RolloutTask implements Task {
		private StateContext m_ctx;

		private CountDownLatch m_latch;

		public RolloutTask(ControllerTask controller, String host, CountDownLatch latch) {
			m_ctx = new StateContext(controller, host);
			m_latch = latch;
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public void run() {
			try {
				DeployState.execute(m_ctx);
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

	static class StateContext implements DeployState.Context {
		private ControllerTask m_controller;

		private DeployState m_state;

		private String m_host;

		private int m_retryCount;

		public StateContext(ControllerTask controller, String host) {
			m_controller = controller;
			m_host = host;
		}

		@Override
		public ConfigManager getConfigManager() {
			return m_controller.getConfigManager();
		}

		@Override
		public int getDeployId() {
			return m_controller.getDeployId();
		}

		@Override
		public String getDomain() {
			return m_controller.getDomain();
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
		public DeployState getState() {
			return m_state;
		}

		@Override
		public String getVersion() {
			return m_controller.getVersion();
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

					m_controller.update(progress, segment);
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
		public void setState(DeployState state) {
			m_state = state;
		}
	}
}
