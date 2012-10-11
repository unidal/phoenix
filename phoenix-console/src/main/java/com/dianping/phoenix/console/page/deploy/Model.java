package com.dianping.phoenix.console.page.deploy;

import java.util.List;
import java.util.regex.Pattern;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.service.HostPlan;
import com.site.web.mvc.ViewModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<String> m_hosts;

	private List<HostPlan> m_hostPlans;

	private HostPlan m_currentHostPlan;

	private String m_plan;

	private String m_log;

	private int m_offset;

	private String m_status;

	public Model(Context ctx) {
		super(ctx);
	}

	public HostPlan getCurrentHostPlan() {
		return m_currentHostPlan;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public List<HostPlan> getHostPlans() {
		return m_hostPlans;
	}

	public List<String> getHosts() {
		return m_hosts;
	}

	public int getOffset() {
		return m_offset;
	}

	public String getPlan() {
		return m_plan;
	}

	public String getQuotedLog() {
		return m_log == null ? null : m_log.replace(Pattern.quote("\""), "\\\"");
	}

	public String getStatus() {
		return m_status;
	}

	public void setCurrentHostPlan(HostPlan currentHostPlan) {
		m_currentHostPlan = currentHostPlan;
	}

	public void setHostPlans(List<HostPlan> hostPlans) {
		m_hostPlans = hostPlans;
	}

	public void setHosts(List<String> hosts) {
		m_hosts = hosts;
	}

	public void setLog(String log) {
		m_log = log;
	}

	public void setOffset(int offset) {
		m_offset = offset;
	}

	public void setPlan(String plan) {
		m_plan = plan;
	}

	public void setStatus(String status) {
		m_status = status;
	}
}
