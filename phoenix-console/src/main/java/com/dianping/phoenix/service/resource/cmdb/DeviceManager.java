package com.dianping.phoenix.service.resource.cmdb;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import com.dianping.phoenix.device.entity.Device;

public interface DeviceManager {

	public List<Device> getDevices(String domainName) throws ConnectException;

	public Map<String, Map<String, List<Device>>> getDeviceCatalog() throws ConnectException;
}
