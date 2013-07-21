package com.dianping.phoenix.service.visitor.resource;

import com.dianping.phoenix.agent.resource.IVisitor;
import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Kernel;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;

public abstract class BaseResourceVisitor implements IVisitor {

	@Override
	public void visitApp(App app) {
		if (app.getKernel() != null) {
			visitKernel(app.getKernel());
		}
		for (Lib lib : app.getLibs()) {
			visitLib(lib);
		}
	}

	@Override
	public void visitContainer(Container container) {
		for (App app : container.getApps()) {
			visitApp(app);
		}
	}

	@Override
	public void visitDomain(Domain domain) {
		for (Host host : domain.getHosts().values()) {
			visitHost(host);
		}
	}

	@Override
	public void visitHost(Host host) {
		if (host.getPhoenixAgent() != null) {
			visitPhoenixAgent(host.getPhoenixAgent());
		}
		if (host.getContainer() != null) {
			visitContainer(host.getContainer());
		}
	}

	@Override
	public void visitKernel(Kernel kernel) {
		for (Lib lib : kernel.getLibs()) {
			visitLib(lib);
		}
	}

	@Override
	public void visitLib(Lib lib) {
	}

	@Override
	public void visitPhoenixAgent(PhoenixAgent phoenixAgent) {
	}

	@Override
	public void visitProduct(Product product) {
		for (Domain domain : product.getDomains().values()) {
			visitDomain(domain);
		}
	}

	@Override
	public void visitResource(Resource resource) {
		for (Product product : resource.getProducts().values()) {
			visitProduct(product);
		}
	}
}
