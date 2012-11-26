package com.dianping.phoenix.deploy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.site.helper.Threads;

public class DefaultDeployExecutor implements DeployExecutor {
	private static ExecutorService s_threadPool = Threads.forPool().getFixedThreadPool("Phoenix-Deploy", 50);

	@Inject
	private DeployPolicy m_policy;

	private Map<Integer, ControllerTask> m_tasks = new HashMap<Integer, ControllerTask>();

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public synchronized void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError) {
		ControllerTask task = new ControllerTask(deployId, name, hosts, version, m_policy, abortOnError);

		m_tasks.put(deployId, task);
		Threads.forGroup("Phoenix").start(task);
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

	static class ControllerTask implements Task {
		private int m_deployId;

		private String m_name;

		private List<String> m_hosts;

		private String m_version;

		private DeployPolicy m_policy;

		private boolean m_abortOnError;

		private StringBuilder m_content = new StringBuilder(8192);

		private int m_hostIndex;

		private boolean m_active;

		public ControllerTask(int deployId, String name, List<String> hosts, String version, DeployPolicy policy,
		      boolean abortOnError) {
			m_deployId = deployId;
			m_name = name;
			m_hosts = hosts;
			m_version = version;
			m_policy = policy;
			m_abortOnError = abortOnError;
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

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public void shutdown() {
			m_active = false;
		}
	}

	static class RolloutTask implements Task {
		private ControllerTask m_controller;

		private String m_host;

		private CountDownLatch m_latch;

		private boolean m_active;

		public RolloutTask(ControllerTask controller, String host, CountDownLatch latch) {
			m_controller = controller;
			m_host = host;
			m_latch = latch;
		}

		@Override
		public void run() {
			m_active = true;

			while (m_active) {
				// TODO
			}

			m_latch.countDown();
		}

		@Override
		public String getName() {
			return RolloutTask.class.getSimpleName();
		}

		@Override
		public void shutdown() {
			m_active = false;
		}
	}
}
