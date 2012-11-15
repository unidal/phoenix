package com.dianping.phoenix.console.service;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.jgit.api.Git;

import com.site.helper.Files;

public class DefaultGitService implements GitService, Initializable {
	private File m_workingDir = new File("target/gitrepo");

	private Git m_git;

	@Override
	public void clearWorkingDir() throws Exception {
		String[] names = m_workingDir.list();

		if (names != null) {
			for (String name : names) {
				if (".git".equals(name)) {
					continue;
				}

				Files.forDir().delete(new File(m_workingDir, name), true);
			}
		}
	}

	@Override
	public void commit(String tag, String description) throws Exception {
	}

	@Override
	public void pull() throws Exception {
	}

	@Override
	public void push() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public File getWorkingDir() {
		return m_workingDir;
	}

	@Override
	public void initialize() throws InitializationException {
		File gitRepo = new File(m_workingDir, ".git");

		m_workingDir.mkdirs();

		try {
			if (!gitRepo.exists()) {
				m_git = Git.init().setDirectory(m_workingDir).call();
			} else {
				m_git = Git.open(m_workingDir);
			}
		} catch (Exception e) {
			throw new InitializationException(String.format("Error when initializing git repository(%s)!", m_workingDir),
			      e);
		}
	}
}
