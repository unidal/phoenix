package com.dianping.phoenix.service;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.console.dal.deploy.VersionDao;
import com.dianping.phoenix.console.dal.deploy.VersionEntity;

public class DefaultVersionManager implements VersionManager {
	private static final String KERNEL = "kernel";

	@Inject
	private VersionDao m_dao;

	@Override
	public void createVersion(String version, String description, String releaseNotes, String createdBy) {
		try {
			m_dao.findByDomainVersion(KERNEL, version, VersionEntity.READSET_FULL);

			throw new RuntimeException(String.format("Kernel version(%s) is already existed!", version));
		} catch (DalNotFoundException e) {
			// expected
		} catch (DalException e) {
			throw new RuntimeException(String.format("Error when removing kernel version(%s)!", version), e);
		}

		try {
			Version proto = m_dao.createLocal();

			proto.setDomain(KERNEL);
			proto.setVersion(version);
			proto.setDescription(description);
			proto.setReleaseNotes(releaseNotes);
			proto.setCreatedBy(createdBy);
			proto.setStatus(0);

			m_dao.insert(proto);
		} catch (DalException e) {
			throw new RuntimeException(String.format("Error when inserting kernel version(%s)!", version), e);
		}
	}

	@Override
	public void removeVersion(String version) {
		try {
			Version proto = m_dao.findByDomainVersion(KERNEL, version, VersionEntity.READSET_FULL);

			proto.setStatus(1);
			m_dao.updateByPK(proto, VersionEntity.UPDATESET_FULL);
		} catch (DalNotFoundException e) {
			// ignore it
		} catch (DalException e) {
			throw new RuntimeException(String.format("Error when removing kernel version(%s)!", version), e);
		}
	}
}
