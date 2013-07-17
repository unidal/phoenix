package com.dianping.phoenix.service.netty;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Lib;

public class DefaultAgentStatusFetcherTest extends ComponentTestCase {

	@Test
	public void test() {
		try {
			AgentStatusFetcher statusFetcher = lookup(AgentStatusFetcher.class);
			List<Host> hosts = new ArrayList<Host>();
			for (int idx = 0; idx < 1; idx++) {
				Host host = new Host();
				host.setIp("192.168.22.94");
				hosts.add(host);
			}
			long begin = System.currentTimeMillis();
			statusFetcher.fetchPhoenixAgentStatus(hosts);
			Host host = hosts.get(0);
			System.out.println("AgentStatus: " + host.getPhoenixAgent().getStatus());
			System.out.println("AgentVersion: " + host.getPhoenixAgent().getVersion());
			System.out.println("ContainerStatus: " + host.getContainer().getStatus());
			System.out.println("ContainerVersion: " + host.getContainer().getVersion());
			System.out.println("ContainerName: " + host.getContainer().getType());
			System.out.println("Time costs: " + (System.currentTimeMillis() - begin));
			for (App app : host.getContainer().getApps()) {
				System.out.println("\n##############################");
				System.out.println("AppName: " + app.getName());
				System.out.println("AppVersion: " + app.getVersion());
				for (Lib lib : app.getLibs()) {
					System.out.println("\nLibName: " + lib.getArtifactId());
					System.out.println("LibGroupId: " + lib.getGroupId());
					System.out.println("LibVersion: " + lib.getVersion());
				}
				System.out.println("\n=====================================\n");
				System.out.println("KernelVersion: " + app.getKernel().getVersion());
				for (Lib lib : app.getKernel().getLibs()) {
					System.out.println("\nKernelLibName: " + lib.getArtifactId());
					System.out.println("KernelGroupId: " + lib.getGroupId());
					System.out.println("KernelVersion: " + lib.getVersion());
				}
				System.out.println("##############################\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
