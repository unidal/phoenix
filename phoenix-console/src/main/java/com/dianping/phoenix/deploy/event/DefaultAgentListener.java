package com.dianping.phoenix.deploy.event;

import java.util.Date;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.deploy.agent.Context;
import com.dianping.phoenix.deploy.agent.Progress;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;

public class DefaultAgentListener implements AgentListener {
	@Inject
	private DeployListener m_deployListener;

	@Inject
	private DeploymentDetailsDao m_detailsDao;

	@Override
	public void onEnd(Context ctx, String status) throws Exception {
		DeploymentDetails details = m_detailsDao.createLocal();

		if ("successful".equals(status)) {
			details.setStatus(3); // 3 - successful
		} else if ("failed".equals(status)) {
			details.setStatus(5); // 5 - failed
		} else {
			throw new RuntimeException(String.format("Internal error: unknown status(%s)!", status));
		}

		details.setKeyId(ctx.getId());
		details.setEndDate(new Date());
		details.setRawLog(ctx.getRawLog());
		m_detailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
	}

	@Override
	public void onError(Context ctx, Throwable e) throws Exception {

	}

	@Override
	public void onProgress(Context ctx, Progress progress, String log) throws Exception {
		int id = ctx.getDeployId();
		DeployModel model = m_deployListener.getModel(id);

		if (model != null) {
			String ip = ctx.getHost();
			HostModel host = model.findHost(ip);
			SegmentModel segment = new SegmentModel();

			segment.setCurrentTicks(progress.getCurrent());
			segment.setTotalTicks(progress.getTotal());
			segment.setStatus(progress.getStatus());
			segment.setText(log);
			host.addSegment(segment);
		}
	}

	@Override
	public void onStart(Context ctx) throws Exception {
		DeploymentDetails details = m_detailsDao.createLocal();

		details.setKeyId(ctx.getId());
		details.setStatus(2); // 2 - deploying
		details.setBeginDate(new Date());
		m_detailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
	}
}
