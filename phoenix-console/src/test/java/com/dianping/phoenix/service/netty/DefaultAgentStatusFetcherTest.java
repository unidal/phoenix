package com.dianping.phoenix.service.netty;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.project.entity.Host;

public class DefaultAgentStatusFetcherTest extends ComponentTestCase {

	@Test
	public void test() {
		try {
			AgentStatusFetcher statusFetcher = lookup(AgentStatusFetcher.class);
			List<Host> hosts = new ArrayList<Host>();
			for (int idx = 0; idx < 1000; idx++) {
				Host host = new Host();
				host.setIp("192.168.66.48");
				hosts.add(host);
			}
			long begin = System.currentTimeMillis();
			statusFetcher.fetchPhoenixAgentStatus(hosts);
			System.out.println("Time costs: " + (System.currentTimeMillis() - begin));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
