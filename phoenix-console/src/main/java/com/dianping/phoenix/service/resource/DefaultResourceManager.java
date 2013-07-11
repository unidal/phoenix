package com.dianping.phoenix.service.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Kernel;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.agent.resource.transform.DefaultSaxParser;
import com.dianping.phoenix.agent.resource.transform.DefaultXmlBuilder;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.service.cmdb.DeviceManager;
import com.dianping.phoenix.service.netty.AgentStatusFetcher;
import com.dianping.phoenix.service.visitor.DeviceVisitor;

public class DefaultResourceManager implements ResourceManager, Initializable, LogEnabled {
	@Inject
	private AgentStatusFetcher m_agentStatusFetcher;

	@Inject
	private DeviceManager m_deviceManager;

	private Logger m_logger;
	private DefaultXmlBuilder m_xmlBuilder = new DefaultXmlBuilder();

	private AtomicReference<Resource> m_resource = new AtomicReference<Resource>();
	private AtomicReference<Map<String, List<String>>> m_resourceInfo = new AtomicReference<Map<String, List<String>>>();

	@Override
	public void initialize() throws InitializationException {
		new ConfigFileWatchdog().setDelay(30).start();
		new AgentStatusWatchdog().setDelay(10).start();
	}

	private class ConfigFileWatchdog extends Thread {

		public static final long DEFAULT_DELAY = 1;

		private long m_delay = DEFAULT_DELAY;

		private long m_lastRefresh = 0;

		public ConfigFileWatchdog() {
			setDaemon(true);
			checkAndConfigure();
		}

		public ConfigFileWatchdog setDelay(int minutes) {
			m_delay = minutes;
			return this;
		}

		private void checkAndConfigure() {
			String path1 = "/data/appdatas/phoenix/resource.xml";
			String path2 = "/com/dianping/phoenix/deploy/resource.xml";

			File f = new File(path1);
			if (!f.exists() || !f.isFile()) {
				f = new File(getClass().getResource(path2).getFile());
			}
			if (!f.exists() || !f.isFile()) {
				throw new RuntimeException(String.format("Can't find resource.xml at [%s] nor classpath.", path1));
			}

			if (f.lastModified() > m_lastRefresh) {
				m_lastRefresh = System.currentTimeMillis();

				Resource r = null;
				try {
					r = DefaultSaxParser.parse(new FileInputStream(f));
				} catch (Exception e) {
					throw new RuntimeException("Can't parse project.xml.", e);
				}

				Map<String, List<String>> m = new LinkedHashMap<String, List<String>>();
				if (r != null) {
					for (Product p : r.getProducts()) {
						String name = p.getName();
						List<String> list = new ArrayList<String>();
						for (Domain d : p.getDomains().values()) {
							list.add(d.getName());
						}
						m.put(name, list);
					}
				}
				m_resourceInfo.set(m);
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					TimeUnit.MINUTES.sleep(m_delay);
					checkAndConfigure();
				} catch (Exception e) {
					m_logger.warn("Refresh config failed.", e);
				}
			}
		}
	}

	private class AgentStatusWatchdog extends Thread {
		public static final long DEFAULT_DELAY = 5;

		private long m_delay = DEFAULT_DELAY;

		public AgentStatusWatchdog setDelay(int minutes) {
			m_delay = minutes;
			return this;
		}

		public AgentStatusWatchdog() {
			setDaemon(true);
			refreshAgentStatus(false);
		}

		private void refreshAgentStatus(boolean needInteval) {
			Map<String, List<String>> m = m_resourceInfo.get();
			Resource resource = new Resource();
			for (Entry<String, List<String>> entry : m.entrySet()) {
				Product product = new Product();
				product.setName(entry.getKey());

				for (String domainName : entry.getValue()) {
					Domain domain = getDomainFromName(domainName);
					if (domain != null) {
						product.addDomain(domain);
						if (needInteval) {
							try {
								TimeUnit.SECONDS.sleep(m_delay * 60 / m.size() / entry.getValue().size());
							} catch (InterruptedException e) {
								// ignore it
							}
						}
					}
				}
				resource.addProduct(product);
			}
			analysisResource(resource);
			m_resource.set(resource);
		}

		private void analysisResource(Resource resource) {
			for (Product product : resource.getProducts()) {
				for (Domain domain : product.getDomains().values()) {
					int activeCount = 0;
					int inactiveCount = 0;
					for (Host host : domain.getHosts().values()) {
						domain.addOwner(getUnknowIfBlank(host.getOwner()));
						if (host.getPhoenixAgent() != null && "ok".equals(host.getPhoenixAgent().getStatus())) {
							activeCount++;
						} else {
							inactiveCount++;
						}

						if (host.getContainer() != null) {
							for (App app : host.getContainer().getApps()) {
								Kernel kernel = app.getKernel();
								domain.addKernelVersion(kernel == null ? "NONE" : getUnknowIfBlank(kernel.getVersion()));
								domain.addAppVersion(getUnknowIfBlank(app.getVersion()));
							}
						}
					}
					domain.setActiveCount(activeCount);
					domain.setInactiveCount(inactiveCount);
				}
			}
		}

		private String getUnknowIfBlank(String str) {
			return str != null && str.trim().length() > 0 ? str : "Unknow";
		}

		@Override
		public void run() {
			try {
				TimeUnit.MINUTES.sleep(m_delay);
			} catch (InterruptedException e) {
				// ignore it
			}
			while (true) {
				try {
					refreshAgentStatus(true);
					cacheResource();
				} catch (Exception e) {
					m_logger.warn("Refresh agent status failed.", e);
				}
			}
		}
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public Resource getResource() {
		return m_resource.get();
	}

	private void cacheResource() {
		File cache = new File("/data/appdatas/phoenix/resource-cache.xml");
		if (!cache.exists()) {
			try {
				cache.getParentFile().mkdirs();
				cache.createNewFile();
			} catch (Exception e) {
				m_logger.warn("Can not create resource-cache.xml!", e);
			}
		}
		if (cache.exists()) {
			FileWriter writer = null;
			try {
				String cacheStr = m_xmlBuilder.buildXml(m_resource.get());
				writer = new FileWriter(cache);
				writer.write(cacheStr);
				writer.close();
			} catch (Exception e) {
				m_logger.warn("Write resource to resource-cache.xml failed.", e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						m_logger.warn("Close resource-cache.xml writer failed.");
					}
				}
			}
		}
	}

	private Domain getDomainFromName(String name) {
		if (name != null && name.trim().length() > 0) {
			Domain domain = new Domain();
			domain.setName(name);
			domain.setDescription("N/A");

			List<Device> devices = m_deviceManager.getDevices(name);
			for (Device device : devices) {
				Host host = new Host();
				device.accept(new DeviceVisitor(host));
				domain.addHost(host);
			}
			m_agentStatusFetcher.fetchPhoenixAgentStatus(new ArrayList<Host>(domain.getHosts().values()));
			return domain;
		}
		return null;
	}

	@Override
	public Domain updateDomainManually(String name) {
		Domain domain = getDomainFromName(name);
		if (domain != null) {
			Resource resource = m_resource.get();
			for (Product product : resource.getProducts()) {
				if (product.getDomains().containsKey(name)) {
					product.getDomains().put(name, domain);
					break;
				}
			}
		}
		return domain;
	}

	@Override
	public Domain getDomain(String name) {
		Resource resource = m_resource.get();
		for (Product product : resource.getProducts()) {
			if (product.getDomains().containsKey(name)) {
				return product.getDomains().get(name);
			}
		}
		return null;
	}

	@Override
	public List<Product> getProducts() {
		return new ArrayList<Product>(m_resource.get().getProducts());
	}
}
