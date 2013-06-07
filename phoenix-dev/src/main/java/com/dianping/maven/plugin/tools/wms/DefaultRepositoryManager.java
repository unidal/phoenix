package com.dianping.maven.plugin.tools.wms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.phoenix.phoenix.entity.Phoenix;

public class DefaultRepositoryManager implements RepositoryManager,Initializable {

	private static Logger log = Logger.getLogger(DefaultRepositoryManager.class);

	public final static String SVN_CONFIG_FILENAME = "svnRepo.properties";
	public final static String GIT_CONFIG_FILENAME = "gitRepo.properties";

	private Map<String, Repository> pname2Repo = new HashMap<String, Repository>();
	
	@Inject
	private Phoenix phoenixConfig;

	public DefaultRepositoryManager() {
	}
	
	@Override
	public void init(File wsDir) {
		try {
			InputStream svnIn = loadFromGitOrClasspath(wsDir, SVN_CONFIG_FILENAME);
			if (svnIn != null) {
				Properties props = new Properties();
				props.load(svnIn);
				for (Object pname : props.keySet()) {
					pname2Repo.put((String) pname, new SvnRepository(props.getProperty((String) pname)));
				}
			} else {
				log.warn(String.format("%s not found on file system or classpath", SVN_CONFIG_FILENAME));
			}

			InputStream gitIn = loadFromGitOrClasspath(wsDir, GIT_CONFIG_FILENAME);
			if (gitIn != null) {
				Properties props = new Properties();
				props.load(gitIn);
				for (Object pname : props.keySet()) {
					pname2Repo.put((String) pname, new GitRepository(props.getProperty((String) pname)));
				}
			} else {
				log.warn(String.format("%s not found on file system or classpath", GIT_CONFIG_FILENAME));
			}
		} catch (Exception e) {
			throw new RuntimeException("error read repository config", e);
		}
	}

	private InputStream loadFromGitOrClasspath(File wsDir, String fileName) throws IOException {
		File file = new File(new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER), fileName);
		InputStream in;
		if (file.exists()) {
			in = new FileInputStream(file);
			log.info(String.format("read %s from %s", fileName, file.getAbsolutePath()));
		} else {
			log.info(String.format("try to read %s from classpath", fileName));
			in = this.getClass().getResourceAsStream("/" + fileName);
		}
		return in;
	}

	@Override
	public Repository find(String project) {
		return pname2Repo.get(project);
	}

	@Override
	public void initialize() throws InitializationException {
		pname2Repo.put("phoenix-maven-config", new GitRepository(phoenixConfig.getUrlOfConfig()));
	}

	@Override
	public List<String> getProjectList() {
		ArrayList<String> projectList = new ArrayList<String>();
		for (String pname : pname2Repo.keySet()) {
			projectList.add(pname);
		}
		return projectList;
	}

}
