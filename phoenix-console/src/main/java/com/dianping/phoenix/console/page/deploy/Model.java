package com.dianping.phoenix.console.page.deploy;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
<<<<<<< HEAD
	private DeployPlan m_plan;

	private DeployModel m_deploy;
=======
    private String m_name;

    private DeployPlan m_plan;

    private String m_status; // for test: doing, succeed, failed

    private Map<String, DeployLog> m_logs;

    private DeployModel m_deploy;
>>>>>>> d85463f406c564cba9c6fdce345284be1a505806

    public Model(Context ctx) {
        super(ctx);
    }

    @Override
    public Action getDefaultAction() {
        return Action.VIEW;
    }

<<<<<<< HEAD
	public DeployModel getDeploy() {
		return m_deploy;
	}
=======
    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }
>>>>>>> d85463f406c564cba9c6fdce345284be1a505806

    public DeployPlan getPlan() {
        return m_plan;
    }

<<<<<<< HEAD
	public void setDeploy(DeployModel deploy) {
		m_deploy = deploy;
	}

	public void setPlan(DeployPlan plan) {
		m_plan = plan;
	}
}
=======
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
>>>>>>> d85463f406c564cba9c6fdce345284be1a505806
