package com.dianping.phoenix.deploy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.site.helper.Threads;

public class DefaultDeployExecutor implements DeployExecutor {
	@Inject
	private DeployPolicy m_policy;

	private Map<Integer, MonitorTask> m_tasks = new HashMap<Integer, MonitorTask>();

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError) {
		MonitorTask task = new MonitorTask(deployId, name, hosts, version, abortOnError);

		m_tasks.put(deployId, task);
		Threads.forGroup("Phoenix").start(task);
	}

	@Override
	public DeployUpdate poll(DeployContext ctx) {
		MonitorTask task = m_tasks.get(ctx.getDeployId());

		if (task == null) {
			return new DeployUpdate(true);
		}

		// TODO
		return null;
	}

	static class MonitorTask implements Task {
		public MonitorTask(int deployId, String name, List<String> hosts, String version, boolean abortOnError) {
		}

		@Override
		public void run() {

		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public void shutdown() {
		}
	}
}
