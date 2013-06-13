package com.dianping.phoenix.service;

import com.dianping.phoenix.project.entity.Host;
import com.dianping.phoenix.project.entity.Project;

public class MockDeviceManager extends DefaultDeviceManager implements DeviceManager {

	@Override
	public Project findProjectBy(String name) throws Exception {
		Project p = new Project();
		p.setName("user-web");
		p.setOwner("marsqing");
		p.setDescription("user-web for test");

		Host hosty = new Host();
		hosty.setIp("192.168.26.48");
		hosty.setEnv("DEV");
		hosty.setStatus("在线");
		p.addHost(hosty);
		
		for (int idx = 1; idx <= 5; idx++) {
			Host host = new Host();
			host.setIp(String.format("127.0.0.%d", idx));
			host.setEnv("DEV");
			host.setStatus("在线");
			p.addHost(host);
		}

		return p;
	}
}
