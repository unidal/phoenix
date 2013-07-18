package com.dianping.phoenix.service.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.agent.resource.transform.DefaultSaxParser;
import com.dianping.phoenix.agent.resource.transform.DefaultXmlBuilder;
import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.console.page.home.Payload;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.service.cmdb.DeviceManager;
import com.dianping.phoenix.service.netty.AgentStatusFetcher;
import com.dianping.phoenix.service.visitor.DeviceVisitor;
import com.dianping.phoenix.service.visitor.resource.AgentFilterStrategy;
import com.dianping.phoenix.service.visitor.resource.FilteredResourceBuilder;
import com.dianping.phoenix.service.visitor.resource.JarFilterStrategy;

public class DefaultResourceManager implements ResourceManager, Initializable, LogEnabled {
	@Inject
	protected AgentStatusFetcher m_agentStatusFetcher;

	@Inject
	private DeviceManager m_deviceManager;

	@Inject
	protected ConfigManager m_configManager;

	private Logger m_logger;
	private DefaultXmlBuilder m_xmlBuilder = new DefaultXmlBuilder();

	private AtomicReference<Resource> m_resource = new AtomicReference<Resource>();
	private AtomicReference<Map<String, Domain>> m_domains = new AtomicReference<Map<String, Domain>>();
	private AtomicReference<Map<String, List<String>>> m_resourceInfo = new AtomicReference<Map<String, List<String>>>();

	private AtomicReference<Set<String>> m_jarNameSet = new AtomicReference<Set<String>>();
	private AtomicReference<Set<String>> m_agentVersionSet = new AtomicReference<Set<String>>();

	private Resource m_resourceCache;
	protected String m_cachePath;

	@Override
	public void initialize() throws InitializationException {
		m_cachePath = m_configManager.getResourceCachePath();

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
					for (Product p : r.getProducts().values()) {
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
			Map<String, Domain> domains = new HashMap<String, Domain>();

			for (Entry<String, List<String>> entry : m.entrySet()) {
				Product product = new Product();
				product.setName(entry.getKey());

				for (String domainName : entry.getValue()) {
					Domain domain = getDomainFromName(domainName);
					if (domain != null) {
						product.addDomain(domain);
						domains.put(domainName, domain);
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

			m_resourceCache = resource;
			m_resource.set(resource);
			m_domains.set(domains);

			analysisResource(resource);
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

	void analysisResource(Resource resource) {
		Set<String> agentset = new HashSet<String>();
		Set<String> jarset = new HashSet<String>();

		for (Product product : resource.getProducts().values()) {
			for (Domain domain : product.getDomains().values()) {
				int activeCount = 0;
				int inactiveCount = 0;
				for (Host host : domain.getHosts().values()) {
					domain.addOwner(getUnknowIfBlank(host.getOwner()));
					if (host.getPhoenixAgent() != null) {
						agentset.add(host.getPhoenixAgent().getVersion());
						if ("ok".equals(host.getPhoenixAgent().getStatus())) {
							activeCount++;
						}
					} else {
						inactiveCount++;
					}

					if (host.getContainer() != null) {
						for (App app : host.getContainer().getApps()) {
							Kernel kernel = app.getKernel();
							domain.addKernelVersion(kernel == null ? "NONE" : getUnknowIfBlank(kernel.getVersion()));
							domain.addAppVersion(getUnknowIfBlank(app.getVersion()));
							for (Lib lib : app.getLibs()) {
								jarset.add(lib.getArtifactId());
							}
							if (kernel != null) {
								for (Lib lib : kernel.getLibs()) {
									jarset.add(lib.getArtifactId());
								}
							}
						}
					}
				}
				domain.setActiveCount(activeCount);
				domain.setInactiveCount(inactiveCount);
			}
		}
		setJarNameSet(jarset);
		setAgentVersionSet(agentset);
	}

	private String getUnknowIfBlank(String str) {
		return str != null && str.trim().length() > 0 ? str : "Unknow";
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
		File cache = new File(m_cachePath, "resource-cache.xml");
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

			List<Device> devices;
			try {
				devices = m_deviceManager.getDevices(name);
				for (Device device : devices) {
					Host host = new Host();
					device.accept(new DeviceVisitor(host));
					domain.addHost(host);
				}
				m_agentStatusFetcher.fetchPhoenixAgentStatus(new ArrayList<Host>(domain.getHosts().values()));
			} catch (Exception e) {
				domain = getDomainFromCache(name);
			}
			return domain;
		}
		return null;
	}

	private Domain getDomainFromCache(String domainName) {
		if (m_resourceCache == null) {
			m_resourceCache = getResourceFromCacheFile();
		}
		if (m_resourceCache != null) {
			for (Product product : m_resourceCache.getProducts().values()) {
				if (product.getDomains() != null && product.getDomains().containsKey(domainName)) {
					return product.getDomains().get(domainName);
				}
			}
		}
		return null;
	}

	protected Resource getResourceFromCacheFile() {
		m_logger.warn("Fetch agent status from real host failed, attempt to load resource from cache file.");

		File cache = new File(m_cachePath, "resource-cache.xml");
		if (cache.exists()) {
			try {
				System.out.println(cache.getAbsolutePath());
				Resource res = DefaultSaxParser.parse(new FileInputStream(cache));
				return res;
			} catch (Exception e) {
				m_logger.warn("Can not parse resource cache file.");
			}
		} else {
			m_logger.warn("Can not find resource cache file.");
		}
		return null;
	}

	@Override
	public Domain updateDomainManually(String name) {
		Domain domain = getDomainFromName(name);
		if (domain != null) {
			Resource resource = m_resource.get();
			for (Product product : resource.getProducts().values()) {
				if (product.getDomains().containsKey(name)) {
					product.getDomains().put(name, domain);
					m_domains.get().put(name, domain);
					break;
				}
			}
		}
		return domain;
	}

	@Override
	public Domain getDomain(String name) {
		Map<String, Domain> domains = m_domains.get();
		if (domains != null && domains.containsKey(name)) {
			return domains.get(name);
		}
		return null;
	}

	@Override
	public List<Product> getProducts() {
		return new ArrayList<Product>(m_resource.get().getProducts().values());
	}

	@Override
	public List<Product> getFilteredProducts(Payload payload) {
		Resource resource = new FilteredResourceBuilder("phoenix-agent".equals(payload.getType())
				? new AgentFilterStrategy(getResource(), payload)
				: new JarFilterStrategy(getResource(), payload)).getFilteredResource();
		return new ArrayList<Product>(resource.getProducts().values());
	}

	@Override
	public Domain getFilteredDomain(Payload payload, String name) {
		Resource resource = new FilteredResourceBuilder("phoenix-agent".equals(payload.getType())
				? new AgentFilterStrategy(getResource(), payload)
				: new JarFilterStrategy(getResource(), payload)).getFilteredResource();

		for (Product product : resource.getProducts().values()) {
			if (product.getDomains().containsKey(name)) {
				return product.getDomains().get(name);
			}
		}

		return new Domain(name);
	}

	@Override
	public Set<String> getAgentVersionSet() {
		return m_agentVersionSet.get();
	}

	@Override
	public Set<String> getJarNameSet() {
		return m_jarNameSet.get();
	}

	void setAgentVersionSet(Set<String> set) {
		m_agentVersionSet.set(set);
	}

	void setJarNameSet(Set<String> set) {
		m_jarNameSet.set(set);
	}
}
