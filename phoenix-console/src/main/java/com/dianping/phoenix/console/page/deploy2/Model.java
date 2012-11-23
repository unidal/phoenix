package com.dianping.phoenix.console.page.deploy2;

import java.util.List;
import java.util.Map;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployLog;
import com.dianping.phoenix.deploy.DeployPlan;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private String m_name;
	
	private DeployPlan m_plan;
	
	private List<String> m_hosts;
	
	private Map<String, DeployLog> m_logs;
	
	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public String getName() {
   	return m_name;
   }

	public void setName(String name) {
   	m_name = name;
   }

	public DeployPlan getPlan() {
   	return m_plan;
   }

	public void setPlan(DeployPlan plan) {
   	m_plan = plan;
   }

	public List<String> getHosts() {
   	return m_hosts;
   }

	public void setHosts(List<String> hosts) {
   	m_hosts = hosts;
   }

	public Map<String, DeployLog> getLogs() {
   	return m_logs;
   }

	public void setLogs(Map<String, DeployLog> logs) {
   	m_logs = logs;
   }
}
