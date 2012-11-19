package com.dianping.phoenix.console.service;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

public class DefaultGitService implements GitService, Initializable {
	@Inject
	private StatusReporter m_reporter;
	@Inject
	private ProgressMonitor m_monitor;

	private File m_workingDir = new File("target/gitrepo");
	private String m_gitURL = "git@github.com:firefox007/phoenix_test.git";
	private Git m_git;

	@Override
	public void clearWorkingDir() throws Exception {
		m_reporter.log("Clearing git ... ");

		String[] names = m_workingDir.list();

		if (names != null) {
			for (String name : names) {
				if (".git".equals(name)) {
					continue;
				}

				Files.forDir().delete(new File(m_workingDir, name), true);
			}
		}
		m_reporter.log("Cleared git for ... ");
	}

	@Override
	public void commit(String tag, String description) throws Exception {
		// Add
		m_reporter.log(String.format("Adding to git for tag(%s) ... ", tag));
		m_git.add().addFilepattern(".").call();
		m_reporter.log(String.format("Added to git for tag(%s) ... ", tag));

		// Commit
		m_reporter.log(String.format("Commiting to git for tag(%s) ... ", tag));
		m_git.commit().setAll(true).setMessage(description).call();
		m_reporter.log(String.format("Commited to git for tag(%s) ... ", tag));

		// Tag
		m_reporter.log(String.format("Taging to git for tag(%s) ... ", tag));
		m_git.tag().setName(tag).setMessage(description).call();
		m_reporter.log(String.format("Taged to git for tag(%s) ... ", tag));
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
				m_reporter.log(String.format("Cloning repo from %s ... ", m_gitURL));
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				Repository repository = builder.setGitDir(gitRepo)
						.readEnvironment().findGitDir().build();

				m_git = new Git(repository);
				CloneCommand clone = Git.cloneRepository();
				clone.setProgressMonitor(m_monitor);
				clone.setBare(false);
				clone.setDirectory(m_workingDir);
				clone.setCloneAllBranches(true);
				clone.setURI(m_gitURL);
				clone.call();
				// m_git = Git.init().setDirectory(m_workingDir).call();
				// Repository repository = m_git.getRepository();
				// FileBasedConfig storedConfig = (FileBasedConfig) repository
				// .getConfig();
				// storedConfig.setString("branch", "master", "remote",
				// "origin");
				// storedConfig.setString("branch", "master", "merge",
				// "refs/heads/master");
				// storedConfig.setString("remote", "origin", "fetch",
				// "+refs/heads/*:refs/remotes/origin/*");
				// storedConfig.setString("remote", "origin", "url", m_gitURL);
				// storedConfig.save();
				m_reporter.log(String.format("Cloned repo from %s ... ", m_gitURL));
			} else {
				m_git = Git.open(m_workingDir);
			}

		} catch (Exception e) {
			throw new InitializationException(String.format("Error when initializing git repository(%s)!", m_workingDir),
			      e);
		}

	}

	@Override
	public void pull() throws Exception {
		m_reporter.log("Pulling from git ... ");
		m_git.pull().setProgressMonitor(m_monitor).call();
		m_reporter.log("Pulled from git ... ");
	}

	@Override
	public void push() throws Exception {
		// Push heads
		m_reporter.log("Pushing to git heads ... ");
		m_git.push().setProgressMonitor(m_monitor).setPushAll().call();
		m_reporter.log("Pushed to git heads ... ");

		// Push heads
		m_reporter.log("Pushing to git tags ... ");
		m_git.push().setPushTags().setProgressMonitor(m_monitor).call();
		m_reporter.log("Pushed to git tags ... ");
	}
}
