package com.dianping.phoenix.service.resource;

import java.util.ArrayList;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.service.resource.netty.AgentStatusFetcher;

public class MockResourceManager extends DefaultResourceManager {
	private Resource m_resource = new Resource();

	@Override
	public void initialize() throws InitializationException {
		Product product = new Product();
		product.setName("PhoenixTest");

		Domain domain = new Domain();
		domain.setName("test");

		Host host = new Host();
		host.setIp("192.168.22.73");// Don't use 127.0.0.1 or Localhost
		host.setEnv("dev");
		host.setOwner("tong.song");
		host.setStatus("up");

		PhoenixAgent agent = new PhoenixAgent();
		agent.setVersion("0.0.1");
		agent.setStatus("ok");

		host.setPhoenixAgent(agent);

		Container container = new Container();
		container.setInstallPath("/user/local/tomcat");
		container.setStatus("up");
		container.setType("tomcat");
		container.setVersion("1.6.2");

		host.setContainer(container);

		domain.addHost(host);
		product.addDomain(domain);
		m_resource.addProduct(product);
		lookup(AgentStatusFetcher.class).fetchPhoenixAgentStatus(new ArrayList<Host>(domain.getHosts().values()));

		super.generateMetaInformation(m_resource);
	}

	@Override
	public Resource getResource() {
		return m_resource;
	}
}
