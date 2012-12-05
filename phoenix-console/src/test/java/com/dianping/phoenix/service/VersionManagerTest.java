package com.dianping.phoenix.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.console.dal.deploy.Version;

public class VersionManagerTest extends ComponentTestCase {
	@Test
	public void testSubmitVersion() throws Exception {
		VersionManager manager = lookup(VersionManager.class);
		GitService git = lookup(GitService.class);

		git.setup();

		String tag = "mock-1.0" + System.currentTimeMillis();
		
		Version version = manager.store(tag, "mock description", "test", "mock");
		
		manager.submitVersion(tag, "mock description");
		
		version = manager.getActiveVersion();
		Assert.assertTrue(version != null);
		
		manager.updateVersionSuccessed(version.getId());
		
		List<Version> versions = manager.getFinishedVersions();
		
		Assert.assertTrue(versions.size() > 0);
		
		for(Version v : versions){
			manager.removeVersion(version.getId());
		}
		
	}
}
