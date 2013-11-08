package com.dianping.phoenix.deliverable;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.helper.Threads;
import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.console.dal.deploy.DeliverableDao;
import com.dianping.phoenix.console.dal.deploy.DeliverableEntity;
import com.dianping.phoenix.service.GitContext;
import com.dianping.phoenix.service.GitService;
import com.dianping.phoenix.service.LogService;
import com.dianping.phoenix.service.WarService;

public class DefaultDeliverableManager implements DeliverableManager, LogEnabled {
	@Inject
	private WarService m_warService;

	@Inject
	private GitService m_gitService;

	@Inject
	private DeliverableDao m_dao;

	@Inject
	private LogService m_logService;

	private Logger m_logger;

	@Override
	public boolean createDeliverable(String type, String version, String description) throws Exception {
		try {
			Deliverable deliverable = m_dao.findActiveByTypeAndVersion(type, version, DeliverableEntity.READSET_FULL);
			if (deliverable != null) {
				m_logger.warn(String.format("Version(%s) of %s is already existed!", type, version));
			}
			m_logService.log(type + ":" + version, "Version(%s) of %s is already existed!", type, version);
			return false;
		} catch (DalNotFoundException e) {
			// expected
		}

		Deliverable d = createLocal(type, version, description, "N/A", "phoenix");

		m_dao.insert(d);
		d.setKeyId(d.getId());

		Threads.forGroup("Phoenix").start(new CreateTask(d));
		return true;
	}

	private Deliverable createLocal(String type, String version, String description, String releaseNotes,
			String createdBy) {
		Deliverable d = m_dao.createLocal();

		d.setWarType(type);
		d.setWarVersion(version);
		d.setDescription(description);
		d.setReleaseNotes(releaseNotes);
		d.setCreatedBy(createdBy);
		d.setStatus(1); // 1 - creating

		return d;
	}

	@Override
	public List<Deliverable> getAllDeliverables(String type, DeliverableStatus status) throws Exception {
		return m_dao.findAllByTypeAndStatus(type, status.getId(), DeliverableEntity.READSET_FULL);
	}

	@Override
	public Deliverable getDeliverable(String type, DeliverableStatus status) throws Exception {
		return m_dao.findByTypeAndStatus(type, status.getId(), DeliverableEntity.READSET_FULL);
	}

	@Override
	public boolean removeDeliverable(int id) throws Exception {
		try {
			Deliverable d = m_dao.findByPK(id, DeliverableEntity.READSET_FULL);
			GitContext ctx = new GitContext(d);

			m_gitService.setup(ctx);
			m_gitService.removeTag(ctx);

			d.setStatus(DeliverableStatus.REMOVED.getId());
			m_dao.updateByPK(d, DeliverableEntity.UPDATESET_FULL);
			return true;
		} catch (DalNotFoundException e) {
			// ignore it
			return false;
		}
	}

	class CreateTask implements Task {
		private Deliverable m_d;

		public CreateTask(Deliverable deliverable) {
			m_d = deliverable;
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public void run() {
			try {
				GitContext ctx = new GitContext(m_d);
				String type = m_d.getWarType();
				String version = m_d.getWarVersion();
				String key = type + ":" + version;
				Transaction t = Cat.newTransaction(type, version);

				try {
					m_gitService.setup(ctx);

					File gitDir = m_gitService.getWorkingDir();

					m_gitService.pull(ctx);
					m_gitService.clear(ctx);

					m_logService.log(key, "Downloading war(%s:%s) ... ", type, version);
					m_warService.downloadAndExtractTo(type, version, gitDir);
					m_logService.log(key, "Downloading war(%s:%s) ... DONE", type, version);

					m_gitService.commit(ctx);
					m_gitService.push(ctx);

					m_d.setStatus(DeliverableStatus.ACTIVE.getId());
					m_dao.updateByPK(m_d, DeliverableEntity.UPDATESET_FULL);
				} catch (Exception e) {
					m_logService.log(key, "Error when creating the deliverable(%s:%s)! Message: %s.", type, version, e);
					t.setStatus(e);
					Cat.logError(e);

					try {
						m_d.setStatus(DeliverableStatus.ABORTED.getId());
						m_dao.updateByPK(m_d, DeliverableEntity.UPDATESET_FULL);
					} catch (DalException ex) {
						m_logService.log(key, "Error when removing the deliverable(%s:%s)! Message: %s.", type,
								version, ex);

						Cat.logError(ex);
					}
				} finally {
					t.complete();
				}
			} catch (Exception e) {
				m_logger.error("Error when creating deliverable!", e);
			}
		}

		@Override
		public void shutdown() {
		}
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}
}
