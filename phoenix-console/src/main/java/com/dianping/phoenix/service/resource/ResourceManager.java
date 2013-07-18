package com.dianping.phoenix.service.resource;

import java.util.List;
import java.util.Set;

import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.console.page.home.Payload;

public interface ResourceManager {

	public Resource getResource();

	public Domain updateDomainManually(String name);

	public Set<String> getAgentVersionSet();

	public Set<String> getJarNameSet();

	public List<Product> getProducts();

	public Domain getDomain(String name);

	public List<Product> getFilteredProducts(Payload payload);

	public Domain getFilteredDomain(Payload payload, String name);
}
