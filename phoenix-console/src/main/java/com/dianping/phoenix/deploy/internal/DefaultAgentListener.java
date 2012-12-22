package com.dianping.phoenix.deploy.internal;

import java.util.Date;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.deploy.agent.AgentContext;
import com.dianping.phoenix.deploy.agent.AgentListener;
import com.dianping.phoenix.deploy.agent.AgentProgress;
import com.dianping.phoenix.deploy.agent.AgentStatus;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;

public class DefaultAgentListener implements AgentListener {
	@Inject
	private DeploymentDetailsDao m_deploymentDetailsDao;

	@Override
	public void onEnd(AgentContext ctx, AgentStatus status) throws Exception {
		DeploymentDetails details = m_deploymentDetailsDao.createLocal();

		if (status == AgentStatus.SUCCESS || status == AgentStatus.FAILED) {
			details.setStatus(status.getId());
		} else {
			throw new RuntimeException(String.format("AgentListener onEnd: invalid status(%s)!", status));
		}

		details.setKeyId(ctx.getId());
		details.setEndDate(new Date());
		details.setRawLog(ctx.getRawLog());
		m_deploymentDetailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
	}

	@Override
	public void onProgress(AgentContext ctx, AgentProgress progress, String log) throws Exception {
		DeployModel model = ctx.getDeployModel();
		String ip = ctx.getHost();
		HostModel host = model.findHost(ip);
		SegmentModel segment = new SegmentModel();

		segment.setCurrentTicks(progress.getCurrent());
		segment.setTotalTicks(progress.getTotal());
		segment.setStatus(progress.getStatus());
		segment.setStep(progress.getStep());
		segment.setText(log);

		host.addSegment(segment);
	}

	@Override
	public void onStart(AgentContext ctx) throws Exception {
		DeploymentDetails details = m_deploymentDetailsDao.createLocal();

		details.setKeyId(ctx.getId());
		details.setStatus(2); // 2 - deploying
		details.setBeginDate(new Date());
		m_deploymentDetailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
	}
}
