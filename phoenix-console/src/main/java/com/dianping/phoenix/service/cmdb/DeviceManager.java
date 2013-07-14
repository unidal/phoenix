package com.dianping.phoenix.service.cmdb;

import java.net.ConnectException;
import java.util.List;

import com.dianping.phoenix.device.entity.Device;

public interface DeviceManager {

	public List<Device> getDevices(String name) throws ConnectException;

}
