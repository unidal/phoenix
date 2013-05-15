package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.project.entity.Project;

public interface DeviceManager {

	public Project findProjectBy(String name) throws Exception;

	public List<Project> searchProjects(String keyword) throws Exception;
	
	public List<String> getBussinessLineList();
	
	public List<String> getDomainListByBussinessLine(String bussinessLine);
	
}
