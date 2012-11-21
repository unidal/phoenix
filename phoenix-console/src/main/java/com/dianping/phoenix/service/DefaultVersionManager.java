package com.dianping.phoenix.service;

import java.io.File;
import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.console.dal.deploy.VersionDao;
import com.dianping.phoenix.console.dal.deploy.VersionEntity;

public class DefaultVersionManager implements VersionManager {
	private static final String KERNEL = "kernel";

	@Inject
	private WarService m_warService;

	@Inject
	private GitService m_gitService;

	@Inject
	private VersionDao m_dao;

	@Override
	public void createVersion(String version, String description, String releaseNotes, String createdBy)
	      throws Exception {
		File gitDir = m_gitService.getWorkingDir();

		m_gitService.pull();
		m_gitService.clearWorkingDir();
		m_warService.downloadAndExtractTo(version, gitDir);
		m_gitService.commit(version, description);
		m_gitService.push();

		store(version, description, releaseNotes, createdBy);
	}

	@Override
	public List<Version> getActiveVersions() throws Exception {
		List<Version> versions = m_dao.findAllActive(KERNEL, VersionEntity.READSET_FULL);

		return versions;
	}

	@Override
	public void removeVersion(String version) throws Exception {
		try {
			Version proto = m_dao.findByDomainVersion(KERNEL, version, VersionEntity.READSET_FULL);

			proto.setStatus(1);
			m_dao.updateByPK(proto, VersionEntity.UPDATESET_FULL);
		} catch (DalNotFoundException e) {
			// ignore it
		}
	}

	private void store(String version, String description, String releaseNotes, String createdBy) throws DalException {
		try {
			m_dao.findByDomainVersion(KERNEL, version, VersionEntity.READSET_FULL);

			throw new RuntimeException(String.format("Kernel version(%s) is already existed!", version));
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
	}

	public void setWarService(WarService warService) {
		this.m_warService = warService;
	}
}
