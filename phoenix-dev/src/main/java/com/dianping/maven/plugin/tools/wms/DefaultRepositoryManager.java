package com.dianping.maven.plugin.tools.wms;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.configure.Whiteboard;
import com.dianping.maven.plugin.configure.WorkspaceInitializedListener;
import com.dianping.maven.plugin.phoenix.phoenix.entity.Phoenix;

public class DefaultRepositoryManager implements RepositoryManager, Initializable, WorkspaceInitializedListener {

    private static Logger           log                 = Logger.getLogger(DefaultRepositoryManager.class);

    public final static String      SVN_CONFIG_FILENAME = "svnRepo.properties";
    public final static String      GIT_CONFIG_FILENAME = "gitRepo.properties";

    private Map<String, Repository> pname2Repo          = new HashMap<String, Repository>();

    @Inject
    private Phoenix                 phoenixConfig;

    public DefaultRepositoryManager() {
    }

    @Override
    public void onWorkspaceInitialized(File wsDir) {
        try {
            File svnFile = new File(new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER), SVN_CONFIG_FILENAME);
            InputStream svnIn = ResourceUtil.INSTANCE.loadFromFileOrClasspath(svnFile);
            if (svnIn != null) {
                Properties props = new Properties();
                props.load(svnIn);
                for (String pname : props.stringPropertyNames()) {
                    pname2Repo.put(pname.toLowerCase(), new SvnRepository(props.getProperty(pname)));
                }
            } else {
                log.warn(String.format("%s not found on file system or classpath", SVN_CONFIG_FILENAME));
            }

            File gitFile = new File(new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER), GIT_CONFIG_FILENAME);
            InputStream gitIn = ResourceUtil.INSTANCE.loadFromFileOrClasspath(gitFile);
            if (gitIn != null) {
                Properties props = new Properties();
                props.load(gitIn);
                for (String pname : props.stringPropertyNames()) {
                    pname2Repo.put(pname.toLowerCase(), new GitRepository(props.getProperty(pname)));
                }
            } else {
                log.warn(String.format("%s not found on file system or classpath", GIT_CONFIG_FILENAME));
            }
        } catch (Exception e) {
            throw new RuntimeException("error read repository config", e);
        }
    }

    @Override
    public Repository find(String project) {
        return pname2Repo.get(project);
    }

    @Override
    public void initialize() throws InitializationException {
        pname2Repo.put("phoenix-maven-config", new GitRepository(phoenixConfig.getUrlOfConfig()));
        Whiteboard.INSTANCE.addWorkspaceInitializedListener(this);
    }

    @Override
    public List<String> getProjectListByPattern(String pattern) {
    	if("*".equals(pattern)) {
    		return allProject();
    	} else {
    		return intelligentRank(pattern);
    	}
    }
    
	private List<String> allProject() {
		List<String> projectList = new ArrayList<String>();
		for (String pname : pname2Repo.keySet()) {
			projectList.add(pname);
		}
		Collections.sort(projectList);
		return projectList;
	}

	private List<String> intelligentRank(String pattern) {
		List<String> equalList = new ArrayList<String>();
        List<String> startWithList = new ArrayList<String>();
        List<String> indexOfList = new ArrayList<String>();
        for (String pname : pname2Repo.keySet()) {
            if (pname.equalsIgnoreCase(pattern)) {
                equalList.add(pname);
            } else if (pname.startsWith(pattern.toLowerCase())) {
                startWithList.add(pname);
            } else if (pname.indexOf(pattern.toLowerCase()) >= 0) {
                indexOfList.add(pname);
            }
        }

        Collections.sort(equalList);
        Collections.sort(startWithList);
        Collections.sort(indexOfList);
        List<String> projectList = new ArrayList<String>();
        projectList.addAll(equalList);
        projectList.addAll(startWithList);
        projectList.addAll(indexOfList);

        return projectList;
	}

}
