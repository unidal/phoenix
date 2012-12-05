package com.dianping.phoenix.service;

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
	public Version createVersion(String version, String description,
			String releaseNotes, String createdBy) throws Exception {
		
		Version v = store(version, description, releaseNotes, createdBy);
		
		Threads.forGroup("Phoenix").start(new VersionExecutor(v.getId(),version, description,this));
		
		return v;
	}
	
	public void submitVersion(String version,String description) throws Exception{
		m_gitService.setup();
		
		File gitDir = m_gitService.getWorkingDir();
	
		m_gitService.pull();
		m_gitService.clearWorkingDir();
		try {
			m_warService.downloadAndExtractTo(version, gitDir);
		} catch (FileNotFoundException fe) {
			String log = String.format(
					"can not find war for version: %s ...", version);
			m_reporter.log(log);
			throw new RuntimeException(log);
		}
	
		m_gitService.commit(version, description);
		m_gitService.push();
	}
	
	@Override
	public void updateVersionSuccessed(int versionId){
		try {
			Version proto = m_dao.findByPK(versionId, VersionEntity.READSET_FULL);


			if (proto.getStatus() != 1) {
				proto.setStatus(1);
				m_dao.updateByPK(proto, VersionEntity.UPDATESET_FULL);
			}
		} catch (DalException e) {
			// ignore it
		}
	}
	
	@Override
	public void clearVersion(String version){
		m_reporter.clearMessage(DefaultStatusReporter.VERSION_LOG, version);
	}

	@Override
	public Version getActiveVersion() throws Exception {
		List<Version> versions = m_dao.findAllActive(KERNEL,
				new Date(new Date().getTime() - 5*60*1000),
				VersionEntity.READSET_FULL);

		return versions!=null && versions.size()>0 ? versions.get(0) : null;
	}
	
	@Override
	public List<Version> getFinishedVersions() throws Exception {
		List<Version> versions = m_dao.findAllFinished(KERNEL,
				VersionEntity.READSET_FULL);

		// order in descend
		Collections.sort(versions, new Comparator<Version>() {
			@Override
			public int compare(Version v1, Version v2) {
				return v2.getVersion().compareTo(v1.getVersion());
			}
		});

		return versions;
	}

	@Override
	public void removeVersion(int id) throws Exception {

		try {
			Version proto = m_dao.findByPK(id, VersionEntity.READSET_FULL);

			m_gitService.removeTag(proto.getVersion());

			if (proto.getStatus() != 2) {
				proto.setStatus(2);
				m_dao.updateByPK(proto, VersionEntity.UPDATESET_FULL);
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

			m_reporter.log(String.format(
					"Kernel version(%s) is already existed!", version));
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

	@Override
	public String getStatus(String version,int index) {
		
//		m_reporter.getMessage(category, subCategory, index)
		return null;
	}
	
	public class VersionLog{
		private int m_index;
		private List<String> m_messages;
		
		public VersionLog(int index, List<String> messages){
			m_index = index;
			m_messages = messages;
		}

		public int getIndex() {
			return m_index;
		}

		public void setIndex(int m_index) {
			this.m_index = m_index;
		}

		public List<String> getMessages() {
			return m_messages;
		}

		public void setMessages(List<String> messages) {
			this.m_messages = messages;
		}
		
		
	}

}
