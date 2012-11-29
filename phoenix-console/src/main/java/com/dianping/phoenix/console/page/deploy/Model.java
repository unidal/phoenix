package com.dianping.phoenix.console.page.deploy;

import java.util.Map;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployLog;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
    private String m_name;

    private DeployPlan m_plan;

    private String m_status; // for test: doing, succeed, failed

    private Map<String, DeployLog> m_logs;

    private DeployModel m_deploy;

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

    public Map<String, DeployLog> getLogs() {
        return m_logs;
    }

    public void setLogs(Map<String, DeployLog> logs) {
        m_logs = logs;
    }

    public String getStatus() {
        return m_status;
    }

    public void setStatus(String status) {
        this.m_status = status;
    }

    public DeployModel getDeploy() {
        return m_deploy;
    }

    public void setDeploy(DeployModel deploy) {
        m_deploy = deploy;
    }
}