package com.dianping.phoenix.deploy;

import java.io.IOException;

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
	public void testRetryButFailed() throws Exception {
		RetryButFailedContext ctx = new RetryButFailedContext(m_configManager);

		State.execute(ctx);
		Assert.assertEquals(State.FAILED, ctx.getState());

		String expected = "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... \r\n" //
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... \r\n" //
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... \r\n" //
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... \r\n" //
		      + "[ERROR] Failed to deploy phoenix kernel(1.0) to host(localhost).\r\n";
		Assert.assertEquals(expected, ctx.getLog());
	}

	@Test
	public void testSuccess() throws Exception {
		SuccessContext ctx = new SuccessContext(m_configManager);

		State.execute(ctx);
		Assert.assertEquals(State.SUCCESSFUL, ctx.getState());

		String expected = "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\r\n" //
		      + "[INFO] Getting status from host(localhost) for deploy(123) ... \r\n" //
		      + "[INFO] log and status of http://localhost:3473/phoenix/agent/deploy?op=log&deployId=123\r\n" //
		      + "[INFO] Deployed phoenix kernel(1.0) to host(localhost) successfully.\r\n";
		Assert.assertEquals(expected, ctx.getLog());
	}

	@Test
	public void testSuccessWithRetry() throws Exception {
		SuccessWithRetryContext ctx = new SuccessWithRetryContext(m_configManager);

		State.execute(ctx);
		Assert.assertEquals(State.SUCCESSFUL, ctx.getState());

		String expected = "[INFO] Deploying phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... \r\n" //
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... \r\n" //
		      + "[WARN] Retry to deploy phoenix kernel(1.0) to host(localhost) for deploy(123) of domain(test)  ... ACCEPTED\r\n" //
		      + "[INFO] Getting status from host(localhost) for deploy(123) ... \r\n" //
		      + "[INFO] log and status of http://localhost:3473/phoenix/agent/deploy?op=log&deployId=123\r\n" //
		      + "[INFO] Deployed phoenix kernel(1.0) to host(localhost) successfully.\r\n";
		Assert.assertEquals(expected, ctx.getLog());
	}

	static abstract class BaseContext implements Context {
		private ConfigManager m_configManager;

		private int m_retryCount;

		private State m_state;

		private StringBuilder m_log = new StringBuilder(2048);

		public BaseContext(ConfigManager configManager) {
			m_configManager = configManager;
		}

		@Override
		public ConfigManager getConfigManager() {
			return m_configManager;
		}

		@Override
		public String getRawLog() {
			return null;
		}

		@Override
		public int getId() {
			return 0;
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

		public String getLog() {
			return m_log.toString();
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
			return "1.0";
		}

		protected boolean isDeploy(String url) {
			return url.contains("?op=deploy&");
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
		public void setRetryCount(int retryCount) {
			m_retryCount = retryCount;
		}

		@Override
		public void setState(State state) {
			m_state = state;
		}
	}

	static class RetryButFailedContext extends BaseContext {
		public RetryButFailedContext(ConfigManager configManager) {
			super(configManager);
		}

		@Override
		public String openUrl(String url) throws IOException {
			if (isDeploy(url)) {
				throw new IOException("Unavailable");
			} else if (isLog(url)) {
				return "[INFO] log and status of " + url + "\r\n";
			} else {
				return "";
			}
		}
	}

	static class SuccessContext extends BaseContext {
		public SuccessContext(ConfigManager configManager) {
			super(configManager);
		}

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
	}

	static class SuccessWithRetryContext extends BaseContext {
		private int m_retryCount = 2;

		public SuccessWithRetryContext(ConfigManager configManager) {
			super(configManager);
		}

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
	}
}
