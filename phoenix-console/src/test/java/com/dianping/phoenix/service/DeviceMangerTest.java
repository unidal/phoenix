package com.dianping.phoenix.service;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.phoenix.project.entity.Host;

public class DeviceMangerTest{
	@Test
	public void testIPComparation(){
		DefaultDeviceManager.IPComparator comparator = new DefaultDeviceManager.IPComparator();
		Host h1 = new Host();
		Host h2 = new Host();
		Host h3 = new Host();
		h1.setIp("10.9.99.9");
		h2.setIp("255.100.100.9");
		h3.setIp("10.9.99.10");
		Assert.assertTrue(true);
		Assert.assertTrue(comparator.compare(h1, h2)<0);
		Assert.assertTrue(comparator.compare(h1, h3)<0);

	}
}
