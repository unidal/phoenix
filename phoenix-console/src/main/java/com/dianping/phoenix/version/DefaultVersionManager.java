package com.dianping.phoenix.version;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.console.dal.deploy.DeliverableDao;
import com.dianping.phoenix.console.dal.deploy.DeliverableEntity;
import com.dianping.phoenix.service.DefaultStatusReporter;
import com.dianping.phoenix.service.GitService;
import com.dianping.phoenix.service.StatusReporter;
import com.dianping.phoenix.service.WarService;

public class DefaultVersionManager implements VersionManager {
	@Inject
	private StatusReporter m_reporter;

	@Inject
	private WarService m_warService;

	@Inject
	private GitService m_gitService;

	@Inject
	private DeliverableDao m_dao;

	@Override
	public void clearVersion(String version) {
		m_reporter.clearMessage(DefaultStatusReporter.VERSION_LOG, version);
	}

	@Override
	public Deliverable addVersion(String type, String version, String description, String releaseNotes, String createdBy)
	      throws Exception {
		if (checkExists(type, version)) {
			m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
			      String.format("Version(%s) of %s is already existed!", type, version));
			return null;
		}

		Deliverable v = createLocal(type, version, description, releaseNotes, createdBy);
		m_dao.insert(v);

		Threads.forGroup("Phoenix").start(new VersionExecutor(new VersionContext(v), this));
		return v;
	}

	private boolean checkExists(String type, String version) throws DalException {
		try {
			m_dao.findActiveByTypeAndVersion(type, version, DeliverableEntity.READSET_FULL);

			return true;
		} catch (DalNotFoundException e) {
			// expected
		}

		return false;
	}

	@Override
	public String getActiveVersion(String warType) throws Exception {
		List<Deliverable> versions = m_dao.findAllByTypeAndStatus(warType, 1, DeliverableEntity.READSET_FULL); // TODO bad impl

		return versions.size() > 0 ? versions.get(0).getWarVersion() : null;
	}

	@Override
	public List<Deliverable> getFinishedVersions(String warType) throws Exception {
		List<Deliverable> versions = m_dao.findAllByTypeAndStatus(warType, 2, DeliverableEntity.READSET_FULL);

		// order in descend
		Collections.sort(versions, new Comparator<Deliverable>() {
			@Override
			public int compare(Deliverable v1, Deliverable v2) {
				return v2.getId() - v1.getId();
			}
		});

		return versions;
	}

	@Override
	public StatusReporter getReporter() {
		return m_reporter;
	}

	@Override
	public VersionLog getStatus(String version, int index) {

		VersionLog vl = null;

		List<String> results = m_reporter.getMessage(DefaultStatusReporter.VERSION_LOG, version, index);

		if (results != null && results.size() > 0) {
			vl = new VersionLog(index + results.size(), results);
		}

		return vl;
	}

	@Override
	public void removeVersion(int id) throws Exception {
		Deliverable v = m_dao.findByPK(id, DeliverableEntity.READSET_FULL);
		VersionContext ctx = new VersionContext(v);

		m_gitService.setup(ctx);
		m_gitService.removeTag(ctx);

		if (v.getStatus() != 3) {
			v.setStatus(3);
			m_dao.updateByPK(v, DeliverableEntity.UPDATESET_FULL);
		}
	}

	private Deliverable createLocal(String type, String version, String description, String releaseNotes,
	      String createdBy) {
		Deliverable proto = m_dao.createLocal();

		proto.setWarType(type);
		proto.setWarVersion(version);
		proto.setDescription(description);
		proto.setReleaseNotes(releaseNotes);
		proto.setCreatedBy(createdBy);
		proto.setStatus(1); // 1 - creating

		return proto;
	}

	public void submitVersion(VersionContext ctx) throws VersionException {
		try {
			m_gitService.setup(ctx);

			File gitDir = m_gitService.getWorkingDir();

			m_gitService.pull(ctx);
			m_gitService.clearWorkingDir(ctx);

			try {
				m_warService.downloadAndExtractTo(ctx.getType(), ctx.getVersion(), gitDir);
			} catch (FileNotFoundException fe) {
				String log = String.format("Error whenexWar(%s:%s) is not found!", ctx.getType(), ctx.getVersion());
				m_reporter.log(DefaultStatusReporter.VERSION_LOG, ctx.getVersion(), log);
				throw new RuntimeException(log);
			}

			m_gitService.commit(ctx);
			m_gitService.push(ctx);
		} catch (Exception e) {
			throw new VersionException("", e);
		}
	}

	@Override
	public void updateVersionStatus(int id, VersionStatus status) throws DalException {
		Deliverable v = m_dao.findByPK(id, DeliverableEntity.READSET_FULL);

		v.setStatus(status.getId());
		m_dao.updateByPK(v, DeliverableEntity.UPDATESET_FULL);
	}
}
