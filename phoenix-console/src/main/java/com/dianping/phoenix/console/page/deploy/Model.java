package com.dianping.phoenix.console.page.deploy;

import java.util.List;
import java.util.Map;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployLog;
import com.dianping.phoenix.deploy.DeployPlan;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private String m_name;
	
	private DeployPlan m_plan;

    private String m_planStatus;   //for test: doing, succeed, failed
	
	private List<HostDeployStatus> m_hostStatus;
	
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

	public List<HostDeployStatus> getHostStatus() {
   	return m_hostStatus;
   }

	public void setHostStatus(List<HostDeployStatus> hosts) {
   	m_hostStatus = hosts;
   }

	public Map<String, DeployLog> getLogs() {
   	return m_logs;
   }

	public void setLogs(Map<String, DeployLog> logs) {
   	m_logs = logs;
   }

    public String getPlanStatus() {
        return m_planStatus;
    }

    public void setPlanStatus(String planStatus) {
        this.m_planStatus = planStatus;
    }
}
