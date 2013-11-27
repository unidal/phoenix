package com.dianping.phoenix.service.resource;

import java.util.List;
import java.util.Set;

import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.console.page.home.Payload;
import com.dianping.phoenix.deploy.agent.AgentContext;

public interface ResourceManager {

	public Resource getResource();

	public Domain refreshDomainManually(String domain);

	public void refreshHostInternally(AgentContext context);

	public Set<String> getAgentVersionSet();

	public Set<String> getResourceJarNameSet();

	public Set<String> getDomainJarNameSet(String domain);

	public Domain getDomain(String name);

	public List<Product> getFilteredProducts(Payload payload);

	public Domain getFilteredDomain(Payload payload, String name);
}
