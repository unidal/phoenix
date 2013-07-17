package com.dianping.phoenix.service.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;

public class MockResourceManager extends DefaultResourceManager {
	private Resource m_resource;
	private Map<String, Set<String>> m_libSet = new HashMap<String, Set<String>>();

	@Override
	public void initialize() throws InitializationException {
		m_cachePath = m_configManager.getResourceCachePath();
		m_resource = super.getResourceFromCacheFile();
		Product product = new Product();
		product.setName("PhoenixTest");

		Domain domain = new Domain();
		domain.setName("test");

		Host host = new Host();
		host.setIp("192.168.22.136");// Don't use 127.0.0.1 or Localhost
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

		m_agentStatusFetcher.fetchPhoenixAgentStatus(new ArrayList<Host>(domain.getHosts().values()));
		Set<String> s = new HashSet<String>();
		if (host.getContainer().getApps().size() > 0) {
			App app = host.getContainer().getApps().get(0);
			for (Lib lib : app.getLibs()) {
				s.add(lib.getArtifactId());
			}
			for (Lib lib : app.getKernel().getLibs()) {
				s.add(lib.getArtifactId());
			}
		}
		m_libSet.put(domain.getName(), s);
	}

	@Override
	public Resource getResource() {
		return m_resource;
	}

	@Override
	public List<Product> getProducts() {
		return new ArrayList<Product>(m_resource.getProducts().values());
	}

	@Override
	public Domain getDomain(String name) {
		return m_resource.getProducts().get("PhoenixTest").getDomains().get("test");
	}

	@Override
	public Map<String, Set<String>> getLibSet() {
		return m_libSet;
	}
}
