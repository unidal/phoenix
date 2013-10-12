package com.dianping.phoenix.service.cmdb;

import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.device.entity.Responce;
import com.dianping.phoenix.device.transform.DefaultSaxParser;

public class DefaultDeviceManager implements DeviceManager, Initializable, LogEnabled {

	@Inject
	private ConfigManager m_configManager;

	private Logger m_logger;

	@Override
	public void initialize() throws InitializationException {
	}

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
				devices.addAll(responce.getDevices());
			}
		}
		return devices;
	}
}
