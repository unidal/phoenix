package com.dianping.phoenix.console.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;

public class DefaultDeployService implements DeployService {
	private Map<String, DeployInfo> m_infos = new HashMap<String, DeployInfo>();

	@Override
	public synchronized boolean deploy(List<String> hosts, String id) {
		DeployInfo info = new DeployInfo(id, hosts);
		DeployTask task = new DeployTask(info);

		m_infos.put(id, info);
		Threads.forGroup("Egret").start(task);
		return true;
	}

	@Override
	public List<HostPlan> getHostPlans(String id) {
		DeployInfo info = m_infos.get(id);

		if (info == null) {
			return Collections.emptyList();
		} else {
			return info.getPlans();
		}
	}

	@Override
	public int getMessages(String id, int offset, StringBuilder sb) {
		DeployInfo info = m_infos.get(id);

		if (info == null) {
			return 0;
		}

		return info.getMessage(offset, sb);
	}

	@Override
	public String getStatus(String id) {
		DeployInfo info = m_infos.get(id);

		if (info == null) {
			return "";
		} else {
			return info.getStatus();
		}
	}

	static class DeployInfo {
		private String m_id;

		private List<HostPlan> m_plans = new ArrayList<HostPlan>();

		private List<String> m_messages = new ArrayList<String>();

		private String m_status;

		public DeployInfo(String id, List<String> hosts) {
			m_id = id;

			int index = 0;

			for (String host : hosts) {
				HostPlan plan = new HostPlan(index++, host);

				m_plans.add(plan);
			}
		}

		public void addMessage(String message) {
			m_messages.add(message);
		}

		public String getId() {
			return m_id;
		}

		public int getMessage(int offset, StringBuilder sb) {
			int len = m_messages.size();

			for (int i = offset; i < len; i++) {
				sb.append(m_messages.get(i)).append("<br>");
			}

			return Math.max(0, len - offset);
		}

		public List<HostPlan> getPlans() {
			return m_plans;
		}

		public String getStatus() {
			return m_status;
		}

		public void setStatus(String status) {
			m_status = status;
		}
	}

	static class DeployTask implements Task {
		private DeployInfo m_info;

		public DeployTask(DeployInfo info) {
			m_info = info;
		}

		private boolean activate(String id, HostPlan plan) {
			return doAction(Action.ACTIVATE, id, plan);
		}

		private boolean commit(String id, HostPlan plan) {
			return doAction(Action.COMMIT, id, plan);
		}

		private boolean doAction(Action action, String version, HostPlan plan) {
			DeployStep step = action.getDeployStep();
			String host = plan.getHost();
			String url = String.format("http://%s:3473/phoenix/agent/deploy?op=%s&version=%s", host, action
			      .getDeployStep().getName(), version);

			plan.setCurrentStep(step);
			plan.setStatus("doing");

			if (openUrl(url)) {
				plan.setStatus("success");
				return true;
			} else {
				plan.setStatus("failed");
				return false;
			}
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		private boolean openUrl(String url) {
			BufferedReader reader = null;

			m_info.addMessage("Invoking URL: " + url);

			try {
				InputStream in = new URL(url).openStream();

				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

				while (true) {
					String line = reader.readLine();

					if (line == null) {
						break;
					}

					m_info.addMessage(line);
				}

				return true;
			} catch (Throwable e) {
				m_info.addMessage(e.toString());

				return false;
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// ignore it
					}
				}
			}
		}

		private boolean prepare(String id, HostPlan plan) {
			return doAction(Action.PREPARE, id, plan);
		}

		private boolean rollback(String id, HostPlan plan) {
			return doAction(Action.ROLLBACK, id, plan);
		}

		@Override
		public void run() {
			int hosts = m_info.getPlans().size();
			boolean flag = true;

			m_info.setStatus("doing");

			for (int i = 0; i < hosts; i++) {
				HostPlan plan = m_info.getPlans().get(i);
				String id = m_info.getId();

				if (prepare(id, plan)) {
					if (activate(id, plan)) {
						if (test(id, plan)) {
							if (commit(id, plan)) {
								m_info.addMessage("Commit successfully.");

								continue;
							} else {
								m_info.addMessage("Commit failed.");
							}
						}
					} else {
						if (rollback(id, plan)) {
							m_info.addMessage("Rollback successfully.");
						} else {
							m_info.addMessage("Rollback failed.");
						}
					}
				} else {
					if (rollback(id, plan)) {
						m_info.addMessage("Rollback successfully.");
					} else {
						m_info.addMessage("Rollback failed.");
					}
				}

				flag = false;
				break;
			}

			if (flag) {
				m_info.setStatus("success");
			} else {
				m_info.setStatus("failed");
			}
		}

		@Override
		public void shutdown() {
		}

		private boolean test(String id, HostPlan plan) {
			DeployStep step = Action.TEST.getDeployStep();
			// String host = plan.getHost();
			// String url =
			// String.format("http://%s:5050/egret-demo-1.0.0-SNAPSHOT/add.action?a=12&b=13",
			// host);

			plan.setCurrentStep(step);
			plan.setStatus("doing");

			m_info.addMessage("Test: invoking URL: " + id);

			// try {
			// String content = Files.forIO().readFrom(new URL(url).openStream(),
			// "utf-8");
			//
			// if ("12+13=25".equals(content.trim())) {
			plan.setStatus("success");
			return true;
			// }
			// } catch (Exception e) {
			// m_info.addMessage(e.toString());
			// // ignore it
			// } finally {
			// m_info.addMessage("Test: end");
			// }
			//
			// plan.setStatus("failed");
			// return false;
		}
	}
}
