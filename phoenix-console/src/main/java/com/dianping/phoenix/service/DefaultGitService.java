package com.dianping.phoenix.service;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class DefaultGitService implements GitService {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private StatusReporter m_reporter;

	@Inject
	private ProgressMonitor m_monitor;

	private File m_workingDir = new File("target/gitrepo");

	private Git m_git;

	@Override
	public void clearWorkingDir() throws Exception {
		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

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
		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		// Add
		m_reporter.log(String.format("Adding to git for tag(%s) ... ", tag));
		m_git.add().addFilepattern(".").call();
		m_reporter.log(String
				.format("Adding to git for tag(%s) ... DONE.", tag));

		// Commit
		m_reporter.log(String.format("Commiting to git for tag(%s) ... ", tag));
		m_git.commit().setAll(true).setMessage(description).call();
		m_reporter.log(String.format("Commiting to git for tag(%s) ... DONE.",
				tag));

		// Tag
		m_reporter.log(String.format("Taging to git for tag(%s) ... ", tag));
		m_git.tag().setName(tag).setMessage(description).call();
		m_reporter.log(String
				.format("Taging to git for tag(%s) ... DONE.", tag));
	}

	@Override
	public File getWorkingDir() {
		return m_workingDir;
	}

	@Override
	public void pull() throws Exception {
		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		m_reporter.log("Pulling from git ... ");
		m_git.pull().setProgressMonitor(m_monitor).call();
		m_reporter.log("Pulling from git ... DONE.");
	}

	@Override
	public void push() throws Exception {
		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		// Push heads
		m_reporter.log("Pushing to git heads ... ");
		m_git.push().setProgressMonitor(m_monitor).setPushAll().call();
		m_reporter.log("Pushing to git heads ... DONE.");

		// Push heads
		m_reporter.log("Pushing to git tags ... ");
		m_git.push().setPushTags().setProgressMonitor(m_monitor).call();
		m_reporter.log("Pushing to git tags ... DONE");
	}

	@Override
	public synchronized void setup() throws Exception {
		if (m_git == null) {
			m_workingDir = new File(m_configManager.getGitWorkingDir());
			m_workingDir.mkdirs();

			File gitRepo = new File(m_workingDir, ".git");
			File phoenixHome = new File("src/main/resources/git");

			if (!gitRepo.exists()) {
				String gitURL = m_configManager.getGitOriginUrl();
				m_reporter.log(String.format("Cloning repo from %s ... ",
						gitURL));
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				builder.setGitDir(gitRepo)
				.readEnvironment().findGitDir();
				
				FS fs = builder.getFS();
				if(fs == null){
					fs = FS.DETECTED;
				}
				fs.setUserHome(phoenixHome);
				
				Repository repository = builder.build();
				
				
				m_git = new Git(repository);
				CloneCommand clone = Git.cloneRepository();
				clone.setProgressMonitor(m_monitor);
				clone.setBare(false);
				clone.setDirectory(m_workingDir);
				clone.setCloneAllBranches(true);
				clone.setURI(gitURL);
				try {
					clone.call();
				}catch(Exception e){
					Files.forDir().delete(new File(m_workingDir, ".git"), true);
				}
				m_reporter.log(String.format("Cloning repo from %s ... DONE.",
						gitURL));
			} else {
				m_git = Git.open(m_workingDir,FS.DETECTED.setUserHome(phoenixHome));
			}
		}
	}
}
