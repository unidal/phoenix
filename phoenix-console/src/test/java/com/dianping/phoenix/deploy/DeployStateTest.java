package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.agent.AgentContext;
import com.dianping.phoenix.deploy.agent.AgentState;
import com.dianping.phoenix.deploy.agent.AgentStatus;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class DeployStateTest extends ComponentTestCase {
	private ConfigManager m_configManager;

	@Before
	public void initConfig() throws InitializationException {
		ConfigManager configManager = new ConfigManager();

		configManager.setConfigFile(getClass().getResource("config.xml").getFile());
		configManager.initialize();

		m_configManager = configManager;
	}

	@Test
	public void testFailedWithBadDeploy() throws Exception {
		BaseContext ctx = new BaseContext(m_configManager) {
			@Override
			public String openUrl(String url) throws IOException {
				if (isDeploy(url)) {
					return "{status: \"error\", message: \"deploy id is already existed.\"}";
				} else {
					return "";
				}
			}
		};

		AgentState.execute(ctx);
		Assert.assertEquals(AgentState.FAILED, ctx.getState());

		String expected = "setState: CREATED\n"
				+ "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
				+ "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... error\n"
				+ "deploy id is already existed.\n"
				+ "updateStatus: failed, [ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n"
				+ "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n"//
				+ "setState: FAILED\n";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	@Test
	public void testFailedWithBadDeployStatus() throws Exception {
		BaseContext ctx = new BaseContext(m_configManager) {
			@Override
			public String openUrl(String url) throws IOException {
				if (isDeploy(url)) {
					return "{status: \"ok\"}";
				} else if (isLog(url)) {
					throw new RuntimeException("Unexpected exception threw.");
				} else {
					return "";
				}
			}
		};

		AgentState.execute(ctx);
		Assert.assertEquals(AgentState.FAILED, ctx.getState());

		String expected = "setState: CREATED\n"
				+ "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
				+ "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\n"
				+ "setState: SUBMITTED\n" //
				+ "[INFO] Getting status from host(localhost) for deploy(123) ... \n"
				+ "java.lang.RuntimeException: Unexpected exception threw.\n"
				+ "updateStatus: failed, [ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n"
				+ "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n" //
				+ "setState: FAILED\n";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	@Test
	public void testFailedWithDeployException() throws Exception {
		BaseContext ctx = new BaseContext(m_configManager) {
			@Override
			public String openUrl(String url) throws IOException {
				if (isDeploy(url)) {
					throw new IOException("IO issue");
				} else {
					return "";
				}
			}
		};

		AgentState.execute(ctx);
		Assert.assertEquals(AgentState.FAILED, ctx.getState());

		String expected = "setState: CREATED\n"
				+ "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
				+ "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: IO issue\n"
				+ "setState: UNREACHABLE\n" //
				+ "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: IO issue\n"
				+ "setState: UNREACHABLE\n" //
				+ "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: IO issue\n"
				+ "setState: UNREACHABLE\n" //
				+ "updateStatus: failed, [ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n"
				+ "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n" //
				+ "setState: FAILED\n";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	@Test
	public void testFailedWithRetriedUnreachables() throws Exception {
		BaseContext ctx = new BaseContext(m_configManager) {
			@Override
			public String openUrl(String url) throws IOException {
				if (isDeploy(url)) {
					throw new UnknownHostException("unknownHost");
				} else {
					return "";
				}
			}
		};

		AgentState.execute(ctx);
		Assert.assertEquals(AgentState.FAILED, ctx.getState());

		String expected = "setState: CREATED\n"
				+ "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
				+ "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.net.UnknownHostException: unknownHost\n"
				+ "setState: UNREACHABLE\n"
				+ "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.net.UnknownHostException: unknownHost\n"
				+ "setState: UNREACHABLE\n"
				+ "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.net.UnknownHostException: unknownHost\n"
				+ "setState: UNREACHABLE\n"
				+ "updateStatus: failed, [ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n"
				+ "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n" //
				+ "setState: FAILED\n" + "";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	@Test
	public void testSuccess() throws Exception {
		BaseContext ctx = new BaseContext(m_configManager) {
			@Override
			public String openUrl(String url) throws IOException {
				if (isDeploy(url)) {
					return "{status: \"ok\"}";
				} else if (isLog(url)) {
					return "[INFO] log and status of " + url + "\r\n";
				} else {
					return "";
				}
			}
		};

		AgentState.execute(ctx);
		Assert.assertEquals(AgentState.SUCCESSFUL, ctx.getState());

		String expected = "setState: CREATED\n"
				+ "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
				+ "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\n"
				+ "setState: SUBMITTED\n" //
				+ "[INFO] Getting status from host(localhost) for deploy(123) ... \n"
				+ "[INFO] log and status of http://localhost:3473/phoenix/agent/deploy?op=log&deployId=123\n"
				+ "setState: SUCCESSFUL\n" //
				+ "[INFO] Deployed phoenix kernel(1.0) to host(localhost) successfully.\n";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	@Test
	public void testSuccessWithRetry() throws Exception {
		BaseContext ctx = new BaseContext(m_configManager) {
			private int m_retryCount = 2;

			@Override
			public String openUrl(String url) throws IOException {
				if (isDeploy(url)) {
					if (m_retryCount-- > 0) {
						throw new IOException("Unavailable");
					} else {
						return "{status: \"ok\"}";
					}
				} else if (isLog(url)) {
					return "[INFO] log and status of " + url + "\r\n";
				} else {
					return "";
				}
			}
		};

		AgentState.execute(ctx);
		Assert.assertEquals(AgentState.SUCCESSFUL, ctx.getState());

		String expected = "setState: CREATED\n"
				+ "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
				+ "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: Unavailable\n"
				+ "setState: UNREACHABLE\n"
				+ "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: Unavailable\n"
				+ "setState: UNREACHABLE\n"
				+ "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\n"
				+ "setState: SUBMITTED\n" //
				+ "[INFO] Getting status from host(localhost) for deploy(123) ... \n"
				+ "[INFO] log and status of http://localhost:3473/phoenix/agent/deploy?op=log&deployId=123\n"
				+ "setState: SUCCESSFUL\n" //
				+ "[INFO] Deployed phoenix kernel(1.0) to host(localhost) successfully.\n";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	static abstract class BaseContext implements AgentContext {
		private ConfigManager m_configManager;

		private AgentState m_state;

		private int m_retriedCount;

		private DeployModel m_model = new DeployModel().setId(123);

		private StringBuilder m_log = new StringBuilder(2048);

		private AgentStatus m_status;

		public BaseContext(ConfigManager configManager) {
			m_configManager = configManager;
		}

		@Override
		public ConfigManager getConfigManager() {
			return m_configManager;
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
			return "test";
		}

		@Override
		public String getHost() {
			return "localhost";
		}

		@Override
		public int getId() {
			return 0;
		}

		public String getLog() {
			return m_log.toString();
		}

		@Override
		public String getRawLog() {
			return null;
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
			return "1.0";
		}

		@Override
		public String getWarType() {
			return "phoenix-kernel";
		}

		protected boolean isDeploy(String url) {
			return url.contains("?op=deploy&");
		}

		protected boolean isLog(String url) {
			return url.contains("?op=log&");
		}

		@Override
		public boolean isSkipTest() {
			return false;
		}

		@Override
		public AgentContext print(String pattern, Object... args) {
			String message = String.format(pattern, args);

			m_log.append(message);
			return this;
		}

		@Override
		public AgentContext println() {
			m_log.append("\r\n");
			return this;
		}

		@Override
		public AgentContext println(String pattern, Object... args) {
			String message = String.format(pattern, args);

			m_log.append(message).append("\r\n");
			return this;
		}

		@Override
		public void setRetriedCount(int retriedCount) {
			m_retriedCount = retriedCount;
		}

		@Override
		public void setState(AgentState state) {
			m_state = state;
			m_log.append(String.format("setState: %s\r\n", state));
		}

		@Override
		public void updateStatus(AgentStatus status, String message) {
			m_status = status;
			m_log.append(String.format("updateStatus: %s, %s\r\n", status.getName(), message));
		}
	}
}
