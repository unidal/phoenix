package com.dianping.phoenix.console.service;

import java.util.List;

import com.dianping.phoenix.console.model.entity.Project;

public interface ProjectService {
	public Project findByName(String projectName);

	public List<String> getPatches();

	public List<Project> search(String keyword);
}