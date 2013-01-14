package com.dianping.phoenix.configure;

import junit.framework.Assert;

import org.junit.Test;

public class ConfigManagerTest {
	@Test
	public void test() throws Exception {
		ConfigManager manager = new ConfigManager();

		manager.setConfigFile(getClass().getResource("config.xml").getFile());
		manager.initialize();

		Assert.assertEquals("http://192.168.8.45:8080/artifactory/dianping-snapshots/" + //
		      "com/dianping/platform/phoenix-kernel/1.0/phoenix-kernel-1.0.war", manager.getWarUrl("1.0"));
		Assert.assertEquals("ssh://git@10.1.4.81:58422/kernel.git", manager.getGitOriginUrl());
		Assert.assertEquals("target/gitrepo", manager.getGitWorkingDir());

		Assert.assertEquals("http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=1&domain=user-web"
		      + "&version=0.1-SNAPSHOT&kernelGitUrl=ssh://git@10.1.4.81:58422/kernel.git"
		      + "&qaServiceUrlPrefix=http://192.168.26.23:8080/qa/service/task&qaServiceTimeout=300000",
		      manager.getDeployUrl("localhost", 1, "user-web", "0.1-SNAPSHOT", false));
		Assert.assertEquals("http://localhost:3473/phoenix/agent/deploy?op=deploy&deployId=1&domain=user-web"
		      + "&version=0.1-SNAPSHOT&kernelGitUrl=ssh://git@10.1.4.81:58422/kernel.git",
		      manager.getDeployUrl("localhost", 1, "user-web", "0.1-SNAPSHOT", true));
		Assert.assertEquals("http://localhost:3473/phoenix/agent/deploy?" + //
		      "op=status&deployId=1", manager.getDeployStatusUrl("localhost", 1));
		Assert.assertEquals("http://localhost:3473/phoenix/agent/deploy?" + //
		      "op=log&deployId=1", manager.getDeployLogUrl("localhost", 1));
	}
}
