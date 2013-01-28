package com.dianping.phoenix.service;

import java.io.File;
import java.util.Collection;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class DefaultGitService implements GitService {
	private static final String REFS_TAGS = "refs/tags/";

	@Inject
	private ConfigManager m_configManager;

	@Inject
	private LogService m_log;

	private File m_workingDir = new File("target/gitrepo");

	private Git m_git;

	@Override
	public void clear(GitContext ctx) throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		String key = ctx.getType() + ":" + ctx.getVersion();
		String[] names = m_workingDir.list();

		m_log.log(key, "Cleaning up git repository ...");

		if (names != null) {
			for (String name : names) {
				if (".git".equals(name)) {
					continue;
				}

				Files.forDir().delete(new File(m_workingDir, name), true);
			}
		}

		m_log.log(key, "Cleaning up git repository ... DONE");
	}

	@Override
	public String commit(GitContext ctx) throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		String key = ctx.getType() + ":" + ctx.getVersion();
		String tag = ctx.getVersion();
		String description = ctx.getDescription();

		// git add .
		m_log.log(key, "Adding all files to git ...");
		m_git.add().addFilepattern(".").call();
		m_log.log(key, "Adding all files to git ... DONE");

		// git commit -am <description>
		m_log.log(key, "Committing to git ...", tag);
		RevCommit commit = m_git.commit().setAll(true).setMessage(description).call();
		m_log.log(key, "Committing to git ... DONE", tag);

		// git tag -m <description> <tag>
		try {
			m_log.log(key, "Applying tag(%s) ...", tag);

			m_git.tag().setName(tag).setMessage(description).call();
		} catch (Exception e) {
			m_log.log(key, "[WARN] " + e.toString());
			e.printStackTrace();
		} finally {
			m_log.log(key, "Applying tag(%s) ... DONE", tag);
		}

		return commit.getId().getName();
	}

	@Override
	public File getWorkingDir() {
		return m_workingDir;
	}

	@Override
	public Collection<Ref> lsRemote() throws Exception {
		return m_git.lsRemote().call();
	}

	@Override
	public void pull(GitContext ctx) throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		String key = ctx.getType() + ":" + ctx.getVersion();

		m_log.log(key, "Pulling from git ...");
		m_git.pull().setProgressMonitor(new GitProgressMonitor(m_log, key)).call();
		m_log.log(key, "Pulling from git ... DONE");
	}

	@Override
	public void push(GitContext ctx) throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		String key = ctx.getType() + ":" + ctx.getVersion();

		// git push
		m_log.log(key, "Pushing to git ...");
		m_git.push().setProgressMonitor(new GitProgressMonitor(m_log, key)).setPushAll().call();
		m_log.log(key, "Pushing to git ... DONE");

		// TODO why do we need git push twice?
		// git push --tags
		m_log.log(key, "Pushing to git with tags ...");
		m_git.push().setPushTags().setProgressMonitor(new GitProgressMonitor(m_log, key)).call();
		m_log.log(key, "Pushing to git with tags ... DONE");
	}

	@Override
	public void removeTag(GitContext ctx) throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		String key = ctx.getType() + ":" + ctx.getVersion();
		String tag = ctx.getVersion();

		// git tag -d <tag>
		m_log.log(key, "Removing tag(%s) from local git ...", tag);
		m_git.tagDelete().setTags(tag).call();
		m_log.log(key, "Removing tag(%s) from local git ... DONE", tag);

		// git push --tags <ref-specs>
		m_log.log(key, "Removing tag(%s) from remote git ...", tag);
		m_git.push().setRefSpecs(new RefSpec(":" + REFS_TAGS + tag))
		      .setProgressMonitor(new GitProgressMonitor(m_log, key)).call();
		m_log.log(key, "Removing tag(%s) from remote git ... DONE", tag);
	}

	@Override
	public synchronized void setup(GitContext ctx) throws Exception {
		if (m_git == null) {
			String type = ctx.getType();
			m_workingDir = new File(m_configManager.getGitWorkingDir(type));
			m_workingDir.mkdirs();

			File gitRepo = new File(m_workingDir, ".git");
			File phoenixHome = new File(this.getClass().getClassLoader().getResource("git").toURI());

			if (!gitRepo.exists()) {
				String key = ctx.getType() + ":" + ctx.getVersion();
				String gitURL = m_configManager.getGitOriginUrl(type);
				FileRepositoryBuilder builder = new FileRepositoryBuilder();

				m_log.log(key, "Cloning from git(%s) ...", gitURL);
				builder.setGitDir(gitRepo).readEnvironment().findGitDir();

				FS fs = builder.getFS();

				if (fs == null) {
					fs = FS.DETECTED;
				}

				fs.setUserHome(phoenixHome);

				Repository repository = builder.build();

				m_git = new Git(repository);

				CloneCommand clone = Git.cloneRepository();

				clone.setProgressMonitor(new GitProgressMonitor(m_log, key));
				clone.setBare(false);
				clone.setDirectory(m_workingDir);
				clone.setCloneAllBranches(true);
				clone.setURI(gitURL);

				try {
					clone.call();
				} catch (Exception e) {
					Files.forDir().delete(new File(m_workingDir, ".git"), true);

					throw e;
				}

				m_log.log(key, "Cloning from git(%s) ... DONE", gitURL);
			} else {
				m_git = Git.open(m_workingDir, FS.DETECTED.setUserHome(phoenixHome));
			}
		}
	}
}
