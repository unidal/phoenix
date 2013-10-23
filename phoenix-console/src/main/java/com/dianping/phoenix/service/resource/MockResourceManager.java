package com.dianping.phoenix.service.resource;

import java.util.ArrayList;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.service.resource.netty.AgentStatusFetcher;
import com.dianping.phoenix.service.visitor.resource.ResourceAnalyzer;

public class MockResourceManager extends DefaultResourceManager {
	private Resource m_resource;

	@Override
	public void initialize() throws InitializationException {
		m_resource = super.getResourceFromCacheFile(m_configManager.getResourceCachePath());
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

		product = new Product();
		domain = new Domain();
		product.setName("PhoenixTestMock");
		domain.setName("test2");

		int max = 20;

		for (int idx = 0; idx < max; idx++) {
			host = new Host();
			container = new Container();
			agent = new PhoenixAgent();
			agent.setStatus("ok");
			agent.setVersion("0.0." + idx);
			host.setPhoenixAgent(agent);

			App app = new App();
			app.setName("app:" + idx);
			Lib lib = new Lib();
			lib.setArtifactId("test" + idx);
			lib.setVersion(String.valueOf(idx));
			app.addLib(lib);

			lib = new Lib();
			lib.setArtifactId("test" + (idx + 1));
			lib.setVersion(String.valueOf(idx + 1));
			app.addLib(lib);

			lib = new Lib();
			lib.setArtifactId("test" + (idx + 2));
			lib.setVersion(String.valueOf(idx + 2));
			app.addLib(lib);

			container.addApp(app);
			host.setIp("127.0.0." + idx);
			host.setContainer(container);
			domain.addHost(host);
		}

		product.addDomain(domain);
		m_resource.addProduct(product);
		ResourceAnalyzer analyzer = new ResourceAnalyzer(m_resource);
		setAgentVersionSet(analyzer.getAgentVersionSet());
		setJarNameSet(analyzer.getJarNameSet());
		setDomainToJarNameSet(analyzer.getDomainToJarNameSet());
		setDomains(analyzer.getDomains());
	}

	@Override
	public Resource getResource() {
		return m_resource;
	}
}
