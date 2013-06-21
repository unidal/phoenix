package com.dianping.phoenix.service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.map.LRUMap;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.device.entity.Attribute;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.device.entity.Facet;
import com.dianping.phoenix.device.entity.Responce;
import com.dianping.phoenix.device.entity.Value;
import com.dianping.phoenix.device.transform.DefaultSaxParser;
import com.dianping.phoenix.project.entity.Host;
import com.dianping.phoenix.project.entity.Project;

public class DefaultDeviceManager implements DeviceManager, Initializable {

	@Inject
	private ConfigManager m_configManager;

	private List<String> m_bussinessLineList = new ArrayList<String>();

	private Map<String, List<String>> m_bussinessLineToDomainListMap = new HashMap<String, List<String>>();

	private static final String KEY_OWNER = "rd_duty";

	private static final String KEY_IP = "private_ip";

	private static final String KEY_STATUS = "status";

	private static final String KEY_ENV = "env";

	@Override
	public Project findProjectBy(String name) throws Exception {
		String ipUrlPattern = m_configManager.getCmdbIpUrlPattern();
		Responce root = readCmdb(String.format(ipUrlPattern, name));
		Project project = new Project(name);
		// TODO:set description
		project.setDescription("");
		if (root != null && root.getDevices() != null) {
			for (Device device : root.getDevices()) {
				Map<String, Attribute> attributeMap = device.getAttributes();
				if (attributeMap == null) {
					continue;
				}
				Attribute owner = attributeMap.get(KEY_OWNER);
				Attribute ip = attributeMap.get(KEY_IP);
				Attribute env = attributeMap.get(KEY_ENV);
				Attribute status = attributeMap.get(KEY_STATUS);
				if (owner != null && project.getOwner() != null) {
					project.setOwner(owner.getText());
				}
				Host host = new Host();
				if (ip != null) {
					project.addHost(host);
					host.setIp(ip.getText());
					if (env != null) {
						host.setEnv(env.getText());
					}
					if (status != null) {
						host.setStatus(status.getText());
					}
				}
				// TODO:set war info
			}
			if (project.getHosts() != null) {
				Collections.sort(project.getHosts(), new IPComparator());
			}

		}
		return project;
	}

	protected static class IPComparator implements Comparator<Host> {

		@Override
		public int compare(Host o1, Host o2) {
			String ip1 = o1.getIp();
			String ip2 = o2.getIp();
			return getValueOfIP(ip1).compareTo(getValueOfIP(ip2));
		}

		private Long getValueOfIP(String ip) {
			Long result = 0L;
			for (String num : ip.split("\\.")) {
				try {
					result = (result << 8) + Long.parseLong(num);
				} catch (NumberFormatException e) {
					result = result << 8;
				}
			}
			return result;
		}
	}

	@Override
	public void initialize() throws InitializationException {

	}

	@Override
	public List<Project> searchProjects(String keyword) throws Exception {
		List<Project> list = new ArrayList<Project>();
		List<String> nameList = getDomainListByBussinessLine("信息线");

		if (keyword == null || keyword.trim().length() == 0) {
			for (String name : nameList) {
				list.add(new Project(name));
			}
		} else {
			for (String name : nameList) {
				if (name.contains(keyword)) {
					list.add(new Project(name));
				}
			}
		}

		return list;
	}

	private Responce readCmdb(String url) {
		try {
			URL cmdbUrl = new URL(url);
			InputStream in = null;
			in = cmdbUrl.openStream();
			return DefaultSaxParser.parse(in);
		} catch (Exception e) {
			return null;
		}
	}

	private List<String> getAttributeList(String url, String attName) {
		Responce root = readCmdb(url);
		List<String> result = null;
		if (root != null) {
			result = new ArrayList<String>();
			for (Facet facet : root.getFacets()) {
				if (facet.getAttribute().equals(attName)) {
					for (Value value : facet.getValues()) {
						result.add(value.getName());
					}
					break;
				}
			}
		}
		return result;
	}

	@Override
	public List<String> getBussinessLineList() {
		String catalogUrl = m_configManager.getCmdbCatalogUrl();
		List<String> result = getAttributeList(catalogUrl, "catalog");
		if (result != null) {
			m_bussinessLineList = result;
		}
		return m_bussinessLineList;
	}

	@Override
	public List<String> getDomainListByBussinessLine(String bussinessLine) {
		String domainUrlPattern = m_configManager.getCmdbDomainUrlPattern();
		List<String> result = getAttributeList(String.format(domainUrlPattern, bussinessLine), "app");
		if (result == null && m_bussinessLineToDomainListMap.containsKey(bussinessLine)) {
			return m_bussinessLineToDomainListMap.get(bussinessLine);
		} else if (result == null && !m_bussinessLineToDomainListMap.containsKey(bussinessLine)) {
			m_bussinessLineToDomainListMap.put(bussinessLine, new ArrayList<String>());
			return m_bussinessLineToDomainListMap.get(bussinessLine);
		} else {
			m_bussinessLineToDomainListMap.put(bussinessLine, result);
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		LRUMap lruMap = new LRUMap();
		for (int idx = 0; idx < 100; idx++) {
			lruMap.put(idx, idx);
		}
		for (Entry<Integer, Integer> entry : (Set<Entry<Integer, Integer>>) lruMap.entrySet()) {
			System.out.println(String.format("%d : %d", entry.getKey(), entry.getValue()));
		}
	}
}
