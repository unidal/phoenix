package com.dianping.phoenix.service.visitor;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.response.IVisitor;
import com.dianping.phoenix.agent.response.entity.Container;
import com.dianping.phoenix.agent.response.entity.Domain;
import com.dianping.phoenix.agent.response.entity.Kernel;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;

public class AgentResponseVisitor implements IVisitor {
	private Host m_host;
	private com.dianping.phoenix.agent.resource.entity.Container m_container;
	private App m_app;
	private com.dianping.phoenix.agent.resource.entity.Kernel m_kernel;
	private com.dianping.phoenix.agent.resource.entity.Lib m_lib;

	public AgentResponseVisitor(Host host) {
		m_host = host;
	}

	@Override
	public void visitContainer(Container container) {
		m_container = new com.dianping.phoenix.agent.resource.entity.Container();
		if (container != null) {
			m_container.setInstallPath(container.getInstallPath());
			m_container.setStatus(container.getStatus());
			m_container.setType(container.getName());
			m_container.setVersion(container.getVersion());
		}
	}

	@Override
	public void visitDomain(Domain domain) {
		m_app = domain.getWar() == null ? null : new App();
		if (m_app != null) {
			// set meta info
			m_app.setName(domain.getWar().getName());
			m_app.setVersion(domain.getWar().getVersion());

			// set libs
			for (Lib lib : domain.getWar().getLibs()) {
				visitLib(lib);
				m_app.addLib(m_lib);
			}

			// set phoenix kernel
			visitKernel(domain.getKernel());
			m_app.setKernel(m_kernel);
		}
	}

	@Override
	public void visitKernel(Kernel kernel) {
		m_kernel = kernel.getWar() == null ? null : new com.dianping.phoenix.agent.resource.entity.Kernel();
		if (m_kernel != null) {
			m_kernel.setVersion(kernel.getWar().getVersion());
			for (Lib lib : kernel.getWar().getLibs()) {
				visitLib(lib);
				m_kernel.addLib(m_lib);
			}
		}
	}

	@Override
	public void visitLib(Lib lib) {
		m_lib = new com.dianping.phoenix.agent.resource.entity.Lib();
		m_lib.setArtifactId(lib.getArtifactId());
		m_lib.setGroupId(lib.getGroupId());
		m_lib.setVersion(lib.getVersion());
	}

	@Override
	public void visitResponse(Response response) {
		if (response != null && m_host != null) {
			// set phoenixagent
			PhoenixAgent agent = new PhoenixAgent();
			agent.setStatus(response.getStatus());
			agent.setVersion(response.getVersion());
			m_host.setPhoenixAgent(agent);

			// set container
			visitContainer(response.getContainer());
			m_host.setContainer(m_container);

			// add apps
			for (Domain domain : response.getDomains()) {
				visitDomain(domain);
				if (m_app != null) {
					m_host.getContainer().addApp(m_app);
				}
			}
		}
	}

	@Override
	public void visitWar(War war) {
		// do nothing
	}

}
