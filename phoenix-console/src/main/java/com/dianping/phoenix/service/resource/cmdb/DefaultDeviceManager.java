package com.dianping.phoenix.service.resource.cmdb;

import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.device.entity.Attribute;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.device.entity.Responce;
import com.dianping.phoenix.device.transform.DefaultSaxParser;
import com.dianping.phoenix.service.visitor.DeviceVisitor;
import com.dianping.phoenix.utils.StringUtils;

public class DefaultDeviceManager implements DeviceManager, LogEnabled {

	@Inject
	private ConfigManager m_configManager;

	private Logger m_logger;

	private Responce readCmdb(String url) throws ConnectException {
		try {
			URL cmdbUrl = new URL(url);
			URLConnection conn = cmdbUrl.openConnection();
			conn.setConnectTimeout(1000);
			conn.setReadTimeout(1000);
			conn.connect();
			return DefaultSaxParser.parse(conn.getInputStream());
		} catch (Exception e) {
			m_logger.warn(String.format("Read cmdb failed [%s]", url), e);
			throw new ConnectException(String.format("Read cmdb failed [%s]", url));
		}
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public List<Device> getDevices(String name) throws ConnectException {
		List<Device> devices = new ArrayList<Device>();
		if (name != null && name.trim().length() > 0) {
			String cmdbQuery = String.format(m_configManager.getCmdbBaseUrl(),
					String.format("/s?q=app:%s&fl=hostname,private_ip,status,rd_duty,env", name.trim()));
			Responce responce = readCmdb(cmdbQuery);
			if (responce != null && responce.getDevices() != null) {
				for (Device device : responce.getDevices()) {
					devices.add(device);
				}
			}
		}
		return devices;
	}

	@Override
	public Map<String, Map<String, List<Device>>> getDeviceCatalog() throws ConnectException {
		String cmdbQuery = String.format(m_configManager.getCmdbBaseUrl(),
				"/s?q=*&fl=hostname,private_ip,status,rd_duty,env,app,catalog");
		Responce responce = readCmdb(cmdbQuery);
		ExecutorService executor = Executors.newCachedThreadPool();

		if (responce != null) {
			int pageCount = (int) Math.ceil(responce.getNumfound() / 50.0);
			int threadCount = (int) Math.ceil(pageCount / 10.0);// 10_pages/thread
			int curPage = 2;
			CountDownLatch latch = new CountDownLatch(threadCount);
			while (curPage <= pageCount) {
				executor.execute(new QueryTask(responce, curPage,
						(curPage + 9) < pageCount ? (curPage + 9) : pageCount, latch));// 10_pages/thread
				curPage += 10;
			}
			try {
				latch.await(m_configManager.getCmdbTimeoutInSecond(), TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				m_logger.error("Error happens when get cmdb infos, lost some infos.", e);
			}
		}
		executor.shutdown();

		m_logger.info("Read cmdb catalog finished. Total device: " + responce.getDevices().size());

		return generateCatalog(responce.getDevices());
	}

	private Map<String, Map<String, List<Device>>> generateCatalog(List<Device> devices) {
		Map<String, Map<String, List<Device>>> catalog = new LinkedHashMap<String, Map<String, List<Device>>>();
		for (Device device : devices) {
			if (isDeviceEnvRight(device)) {
				String productName = getAttributeText(device.getAttributes().get(DeviceVisitor.KEY_CATALOG), "Unknown");
				String domainName = getAttributeText(device.getAttributes().get(DeviceVisitor.KEY_APP), "Unknown");
				domainName = "Unknown".equals(domainName)
						? String.format("%s-%s", productName, domainName)
						: domainName;

				getDomainFromProduct(getProductFromCatalog(catalog, productName), domainName).add(device);
			}
		}
		return catalog;
	}

	private boolean isDeviceEnvRight(Device device) {
		String env = getAttributeText(device.getAttributes().get(DeviceVisitor.KEY_ENV), "NONENV");
		return m_configManager.getEnvironments().contains(env);
	}

	private String getAttributeText(Attribute attribute, String defaultValue) {
		return attribute != null && !StringUtils.isBlank(attribute.getText()) ? attribute.getText() : defaultValue;
	}

	private Map<String, List<Device>> getProductFromCatalog(Map<String, Map<String, List<Device>>> catalog,
			String productName) {
		Map<String, List<Device>> ret = catalog.get(productName);
		if (ret == null) {
			ret = new LinkedHashMap<String, List<Device>>();
			catalog.put(productName, ret);
		}
		return ret;
	}

	private List<Device> getDomainFromProduct(Map<String, List<Device>> product, String domainName) {
		List<Device> ret = product.get(domainName);
		if (ret == null) {
			ret = new ArrayList<Device>();
			product.put(domainName, ret);
		}
		return ret;
	}

	private class QueryTask implements Runnable {
		private Responce all;
		private int startPage;
		private int endPage;
		private CountDownLatch latch;

		public QueryTask(Responce responce, int startPage, int endPage, CountDownLatch latch) {
			this.all = responce;
			this.startPage = startPage;
			this.endPage = endPage;
			this.latch = latch;
		}

		@Override
		public void run() {
			for (int page = startPage; page <= endPage; page++) {
				String query = String.format(m_configManager.getCmdbBaseUrl(),
						String.format("/s?q=*&fl=hostname,private_ip,status,rd_duty,env,app,catalog&page=%d", page));
				m_logger.info(String.format("Querying: [%s]", query));
				try {
					Responce responce = readCmdb(query);
					for (Device device : responce.getDevices()) {
						all.addDevice(device);
					}
				} catch (ConnectException e) {
					m_logger.error("Read cmdb failed.", e);
				}
			}
			latch.countDown();
		}
	}
}
