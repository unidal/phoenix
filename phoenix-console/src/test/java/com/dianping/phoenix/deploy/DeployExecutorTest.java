package com.dianping.phoenix.deploy;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.tuple.Triple;
import org.unidal.webres.helper.Files;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.agent.Context;
import com.dianping.phoenix.deploy.agent.Progress;
import com.dianping.phoenix.deploy.event.AgentListener;
import com.dianping.phoenix.deploy.event.DeployListener;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;

public class DeployExecutorTest extends ComponentTestCase {
	private static long RETRY_INTERVAL = 0;

	private static StringBuilder SB = new StringBuilder(2048);

	private static Map<String, String> DEPLOY_URLS = new HashMap<String, String>();

	private static Map<String, String> DEPLOY_LOG_URLS = new HashMap<String, String>();

	boolean m_debug = true;

	private static void log(String pattern, Object... args) {
		if (args.length == 0) {
			SB.append(pattern);
		} else {
			SB.append(String.format(pattern, args));
		}

		SB.append('\n');
	}

	private void checkDeploy(String name, Triple<String, String, String>... triples) throws Exception {
		String policy = DeployPolicy.ONE_BY_ONE.getId();
		DeployExecutor executor = lookup(DeployExecutor.class, policy);
		MockDeployListener deployListener = (MockDeployListener) lookup(DeployListener.class);

		RETRY_INTERVAL = 0;
		SB.setLength(0);
		DEPLOY_URLS.clear();
		DEPLOY_LOG_URLS.clear();

		List<String> hosts = new ArrayList<String>();

		for (Triple<String, String, String> triple : triples) {
			String host = triple.getFirst();
			String deployRes = "executor/" + triple.getMiddle();
			String deployLogRes = "executor/" + triple.getLast();
			URL deployResUrl = getClass().getResource(deployRes);
			URL deployLogResUrl = getClass().getResource(deployLogRes);

			Assert.assertNotNull(String.format("Resource(%s) is not found!", deployRes), deployResUrl);
			Assert.assertNotNull(String.format("Resource(%s) is not found!", deployLogRes), deployLogResUrl);
			Assert.assertNotNull("", deployLogResUrl);

			String deployUrl = deployResUrl.toExternalForm() + "?op=deploy&";
			String deployLogUrl = deployLogResUrl.toExternalForm() + "?op=log&";

			hosts.add(host);
			DEPLOY_URLS.put(host, deployUrl);
			DEPLOY_LOG_URLS.put(host, deployLogUrl);
		}

		DeployPlan plan = new DeployPlan();

		plan.setPolicy(policy);
		plan.setVersion("1.0.1");

		DeployModel model = deployListener.onCreate("demo-app", hosts, plan);

		executor.submit(model, hosts);

		if (!deployListener.getLatch().await(5, m_debug ? TimeUnit.HOURS : TimeUnit.SECONDS)) {
			Assert.fail("Deploy flow was blocked! " + model);
		}

		InputStream modelStream = getClass().getResourceAsStream("executor/" + name + ".xml");
		InputStream resultStream = getClass().getResourceAsStream("executor/" + name + ".txt");

		Assert.assertNotNull(String.format("Resource(%s.xml) is not found!", "executor/" + name), modelStream);
		Assert.assertNotNull(String.format("Resource(%s.txt) is not found!", "executor/" + name), resultStream);

		String expectedModel = Files.forIO().readFrom(modelStream, "utf-8");
		String expectedResult = Files.forIO().readFrom(resultStream, "utf-8");

		Assert.assertEquals(expectedResult, SB.toString());
		Assert.assertEquals(expectedModel, model.toString().replaceAll("\r", ""));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFirstFailure() throws Exception {
		checkDeploy("first-failure", //
		      new Triple<String, String, String>("host-a", "ok.json", "failed.log"), //
		      new Triple<String, String, String>("host-b", "ok.json", "successful.log"), //
		      new Triple<String, String, String>("host-c", "ok.json", "successful.log"), //
		      new Triple<String, String, String>("host-d", "ok.json", "successful.log"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testNormal() throws Exception {
		checkDeploy("normal", //
		      new Triple<String, String, String>("host-a", "ok.json", "successful.log"), //
		      new Triple<String, String, String>("host-b", "ok.json", "successful.log"), //
		      new Triple<String, String, String>("host-c", "ok.json", "successful.log"), //
		      new Triple<String, String, String>("host-d", "ok.json", "successful.log"));
	}

	public static class MockAgentListener implements AgentListener {
		@Override
		public void onEnd(Context ctx, String status) throws Exception {
			log("onEnd(%s,%s,%s)", ctx.getDeployId(), ctx.getHost(), status);
		}

		@Override
		public void onProgress(Context ctx, Progress progress, String log) throws Exception {
			log("onProgress(%s,%s)", ctx.getHost(), progress);

			DeployModel model = ctx.getDeployModel();
			String ip = ctx.getHost();
			HostModel host = model.findHost(ip);
			SegmentModel segment = new SegmentModel();

			segment.setCurrentTicks(progress.getCurrent());
			segment.setTotalTicks(progress.getTotal());
			segment.setStatus(progress.getStatus());
			segment.setStep(progress.getStep());
			segment.setText(log);
			host.addSegment(segment);
		}

		@Override
		public void onStart(Context ctx) throws Exception {
			log("onStart(%s,%s)", ctx.getDeployId(), ctx.getHost());
		}
	}

	public static class MockConfigManager extends ConfigManager {
		@Override
		public String getDeployLogUrl(String host, int deployId) {
			return DEPLOY_LOG_URLS.get(host);
		}

		@Override
		public long getDeployRetryInterval() {
			return RETRY_INTERVAL;
		}

		@Override
		public String getDeployUrl(String host, int deployId, String name, String version) {
			return DEPLOY_URLS.get(host);
		}

		@Override
		public boolean isShowLogTimestamp() {
			return false;
		}
	}

	public static class MockDeployListener implements DeployListener {
		private Map<Integer, DeployModel> m_models = new HashMap<Integer, DeployModel>();

		private int m_nextDeployId = 100;

		private CountDownLatch m_latch = new CountDownLatch(1);

		public CountDownLatch getLatch() {
			return m_latch;
		}

		@Override
		public DeployModel onCreate(String name, List<String> hosts, DeployPlan plan) throws Exception {
			DeployModel model = new DeployModel();
			int deployId = m_nextDeployId++;

			for (String host : hosts) {
				model.addHost(new HostModel().setIp(host).setId(-1));
			}

			model.addHost(new HostModel().setIp("summary").setId(-1));

			model.setId(deployId);
			model.setDomain(name);
			model.setVersion(plan.getVersion());
			model.setAbortOnError(plan.isAbortOnError());
			model.setPlan(plan);
			m_models.put(deployId, model);
			return model;
		}

		@Override
		public void onDeployEnd(int deployId) throws Exception {
			log("onDeployEnd(%s)", deployId);

			m_latch.countDown();
		}

		@Override
		public void onDeployStart(int deployId) throws Exception {
			log("onDeployStart(%s)", deployId);
		}

		@Override
		public void onHostCancel(int deployId, String host) throws Exception {
			log("onHostCancel(%s,%s)", deployId, host);

			DeployModel deployModel = m_models.get(deployId);

			if (deployModel != null) {
				HostModel hostModel = deployModel.findHost(host);
				SegmentModel segment = new SegmentModel();

				segment.setCurrentTicks(100).setTotalTicks(100).setStatus("cancelled");
				hostModel.addSegment(segment);
			}
		}

		@Override
		public void onHostEnd(int deployId, String host) throws Exception {
			log("onHostEnd(%s, %s)", deployId, host);
		}
	}
}
