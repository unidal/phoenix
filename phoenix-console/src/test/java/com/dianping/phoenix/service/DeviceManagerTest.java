package com.dianping.phoenix.service;

import java.util.List;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.service.resource.cmdb.DeviceManager;
import com.dianping.phoenix.service.visitor.DeviceVisitor;

public class DeviceManagerTest extends ComponentTestCase {

	@Test
	public void testCmdb() {
		try {
			DeviceManager deviceManager = lookup(DeviceManager.class);
			List<Device> list = deviceManager.getDevices("shop-web");
			DeviceVisitor visitor = new DeviceVisitor();
			for (Device device : list) {
				Host host = new Host();
				device.accept(visitor.setHost(host));
				System.out.println(host.getIp());
				System.out.println(host.getEnv());
				System.out.println(host.getStatus());
				System.out.println(host.getOwner());
				System.out.println("\n=================\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
