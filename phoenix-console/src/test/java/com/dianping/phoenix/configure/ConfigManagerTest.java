package com.dianping.phoenix.configure;

import junit.framework.Assert;

import org.junit.Test;

public class ConfigManagerTest {
	@Test
	public void test() throws Exception {
		ConfigManager manager = new ConfigManager();

		manager.setConfigFile(getClass().getResource("config.xml").getFile());
		manager.initialize();

		Assert.assertEquals("http://192.168.8.45:8080/artifactory/dianping-snapshots/com/dianping/platform/phoenix-kernel/1.0/phoenix-kernel-1.0.war", manager.getWarUrl("1.0"));
		Assert.assertEquals("ssh://git@10.1.4.81:58422/kernel.git", manager.getGitOriginUrl());
		Assert.assertEquals("target/gitrepo", manager.getGitWorkingDir());
	}
}
