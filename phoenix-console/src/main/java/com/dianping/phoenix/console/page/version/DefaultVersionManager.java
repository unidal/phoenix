package com.dianping.phoenix.console.page.version;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.console.dal.deploy.VersionDao;
import com.dianping.phoenix.console.dal.deploy.VersionEntity;
import com.dianping.phoenix.service.DefaultStatusReporter;
import com.dianping.phoenix.service.GitService;
import com.dianping.phoenix.service.StatusReporter;
import com.dianping.phoenix.service.WarService;

public class DefaultVersionManager implements VersionManager {
	private static final String KERNEL = "kernel";

	@Inject
	private StatusReporter m_reporter;

	@Inject
	private WarService m_warService;

	@Inject
	private GitService m_gitService;

	@Inject
	private VersionDao m_dao;

	@Override
	public void clearVersion(String version) {
		m_reporter.clearMessage(DefaultStatusReporter.VERSION_LOG, version);
	}

	@Override
	public Version createVersion(String version, String description,
			String releaseNotes, String createdBy) throws Exception {

		Version v = store(version, description, releaseNotes, createdBy);

		Threads.forGroup("Phoenix").start(
				new VersionExecutor(new VersionContext(v.getId(), version,
						description, releaseNotes, createdBy), this));

		return v;
	}

	@Override
	public Version getActiveVersion() throws Exception {
		List<Version> versions = m_dao.findAllActive(KERNEL, new Date(
				new Date().getTime() - 5 * 60 * 1000),
				VersionEntity.READSET_FULL);

		return versions != null && versions.size() > 0 ? versions.get(0) : null;
	}

	@Override
	public List<Version> getFinishedVersions() throws Exception {
		List<Version> versions = m_dao.findAllFinished(KERNEL,
				VersionEntity.READSET_FULL);

		// order in descend
		Collections.sort(versions, new Comparator<Version>() {
			@Override
			public int compare(Version v1, Version v2) {
//				return v2.getVersion().compareTo(v1.getVersion());
				return v2.getId() - v1.getId();
			}
		});

		return versions;
	}

	@Override
	public VersionLog getStatus(String version, int index) {

		VersionLog vl = null;

		List<String> results = m_reporter.getMessage(
				DefaultStatusReporter.VERSION_LOG, version, index);

		if (results != null && results.size() > 0) {
			vl = new VersionLog(index + results.size(), results);
		}

		return vl;
	}

	@Override
	public void removeVersion(int id) throws Exception {
		try {
			Version v = m_dao.findByPK(id, VersionEntity.READSET_FULL);

			VersionContext context = new VersionContext(v.getId(),
					v.getVersion(), v.getDescription(), v.getReleaseNotes(),
					v.getCreatedBy());

			m_gitService.setup(context);

			m_gitService.removeTag(context);

			if (v.getStatus() != 2) {
				v.setStatus(2);
				m_dao.updateByPK(v, VersionEntity.UPDATESET_FULL);
			}
		} catch (DalNotFoundException e) {
			// ignore it
		}
	}

	@Override
	public Version store(String version, String description,
			String releaseNotes, String createdBy) throws DalException {
		try {
			m_dao.findByDomainVersion(KERNEL, version,
					VersionEntity.READSET_FULL);

			m_reporter.categoryLog(DefaultStatusReporter.VERSION_LOG, version,
					String.format("Kernel version(%s) is already existed!",
							version));
		} catch (DalNotFoundException e) {
			// expected
		}

		Version proto = m_dao.createLocal();

		proto.setDomain(KERNEL);
		proto.setVersion(version);
		proto.setDescription(description);
		proto.setReleaseNotes(releaseNotes);
		proto.setCreatedBy(createdBy);
		proto.setStatus(0);

		m_dao.insert(proto);
		return proto;
	}

	public void submitVersion(VersionContext context) throws VersionException {
		try {
			m_gitService.setup(context);

			File gitDir = m_gitService.getWorkingDir();

			m_gitService.pull(context);
			m_gitService.clearWorkingDir(context);
			try {
				m_warService.downloadAndExtractTo(context, gitDir);
			} catch (FileNotFoundException fe) {
				String log = String.format(
						"can not find war for version: %s ...",
						context.getVersion());
				m_reporter.categoryLog(DefaultStatusReporter.VERSION_LOG,
						context.getVersion(), log);
				throw new RuntimeException(log);
			}

			m_gitService.commit(context);
			m_gitService.push(context);
		} catch (Exception e) {
			throw new VersionException(e);
		}
	}

	@Override
	public void updateVersionSuccessed(int versionId) throws DalException {
		Version proto = m_dao.findByPK(versionId, VersionEntity.READSET_FULL);

		if (proto.getStatus() != 1) {
			proto.setStatus(1);
			m_dao.updateByPK(proto, VersionEntity.UPDATESET_FULL);
		}
	}

	public class VersionLog {
		private int m_index;
		private List<String> m_messages;

		public VersionLog(int index, List<String> messages) {
			m_index = index;
			m_messages = messages;
		}

		public int getIndex() {
			return m_index;
		}

		public List<String> getMessages() {
			return m_messages;
		}

		public void setIndex(int m_index) {
			this.m_index = m_index;
		}

		public void setMessages(List<String> messages) {
			this.m_messages = messages;
		}

	}

}
