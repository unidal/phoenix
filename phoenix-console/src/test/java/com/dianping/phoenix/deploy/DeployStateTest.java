package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.agent.Context;
import com.dianping.phoenix.deploy.agent.State;

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

		State.execute(ctx);
		Assert.assertEquals(State.FAILED, ctx.getState());

		String expected = "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
		      + "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... error\n"
		      + "deploy id is already existed.\n" + "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n";
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

		State.execute(ctx);
		Assert.assertEquals(State.FAILED, ctx.getState());

		String expected = "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
		      + "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\n"
		      + "[INFO] Getting status from host(localhost) for deploy(123) ... \n"
		      + "java.lang.RuntimeException: Unexpected exception threw.\n"
		      + "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n";
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

		State.execute(ctx);
		Assert.assertEquals(State.FAILED, ctx.getState());

		String expected = "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
		      + "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: IO issue\n"
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: IO issue\n"
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: IO issue\n"
		      + "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n";
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

		State.execute(ctx);
		Assert.assertEquals(State.FAILED, ctx.getState());

		String expected = "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
		      + "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.net.UnknownHostException: unknownHost\n"
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.net.UnknownHostException: unknownHost\n"
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.net.UnknownHostException: unknownHost\n"
		      + "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\n";
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

		State.execute(ctx);
		Assert.assertEquals(State.SUCCESSFUL, ctx.getState());

		String expected = "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
		      + "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\n"
		      + "[INFO] Getting status from host(localhost) for deploy(123) ... \n"
		      + "[INFO] log and status of http://localhost:3473/phoenix/agent/deploy?op=log&deployId=123\n"
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

		State.execute(ctx);
		Assert.assertEquals(State.SUCCESSFUL, ctx.getState());

		String expected = "[INFO] Deploy URL: http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=123&domain=test&version=1.0\n"
		      + "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: Unavailable\n"
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... java.io.IOException: Unavailable\n"
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\n"
		      + "[INFO] Getting status from host(localhost) for deploy(123) ... \n"
		      + "[INFO] log and status of http://localhost:3473/phoenix/agent/deploy?op=log&deployId=123\n"
		      + "[INFO] Deployed phoenix kernel(1.0) to host(localhost) successfully.\n";
		Assert.assertEquals(expected, ctx.getLog().replaceAll("\r", ""));
	}

	static abstract class BaseContext implements Context {
		private ConfigManager m_configManager;

		private State m_state;

		private int m_retriedCount;

		private boolean m_failed;

		private StringBuilder m_log = new StringBuilder(2048);

		public BaseContext(ConfigManager configManager) {
			m_configManager = configManager;
		}

		@Override
		public ConfigManager getConfigManager() {
			return m_configManager;
		}

		@Override
		public int getDeployId() {
			return 123;
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
		public State getState() {
			return m_state;
		}

		@Override
		public String getVersion() {
			return "1.0";
		}

		protected boolean isDeploy(String url) {
			return url.contains("?op=deploy&");
		}

		@Override
		public boolean isFailed() {
			return m_failed;
		}

		protected boolean isLog(String url) {
			return url.contains("?op=log&");
		}

		@Override
		public Context print(String pattern, Object... args) {
			String message = String.format(pattern, args);

			m_log.append(message);
			return this;
		}

		@Override
		public Context println() {
			m_log.append("\r\n");
			return this;
		}

		@Override
		public Context println(String pattern, Object... args) {
			String message = String.format(pattern, args);

			m_log.append(message).append("\r\n");
			return this;
		}

		@Override
		public void setFailed(boolean failed) {
			m_failed = failed;
		}

		@Override
		public void setRetriedCount(int retriedCount) {
			m_retriedCount = retriedCount;
		}

		@Override
		public void setState(State state) {
			m_state = state;
		}

		@Override
		public void updateStatus(String status, String message) {
		}
	}
}
