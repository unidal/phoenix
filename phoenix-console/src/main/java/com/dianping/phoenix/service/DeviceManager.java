package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.project.entity.BussinessLine;
import com.dianping.phoenix.project.entity.Project;

public interface DeviceManager {

	public List<BussinessLine> getBussinessLineList();

	public Project getProjectByName(String name);

	public Project refreshProjectMannully(String name);
}
