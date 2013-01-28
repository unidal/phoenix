package com.dianping.phoenix.service;

import java.io.File;
import java.util.Collection;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.version.VersionContext;

public class DefaultGitService implements GitService {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private StatusReporter m_reporter;

	private File m_workingDir = new File("target/gitrepo");

	private Git m_git;

	private static final String REFS_TAGS = "refs/tags/";

	@Override
	public void clearWorkingDir(VersionContext context) throws Exception {
		String version = context.getVersion();
		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Clearing git ... ");

		String[] names = m_workingDir.list();

		if (names != null) {
			for (String name : names) {
				if (".git".equals(name)) {
					continue;
				}

				Files.forDir().delete(new File(m_workingDir, name), true);
			}
		}
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Cleared git for ... ");
	}

	@Override
	public ObjectId commit(VersionContext context) throws Exception {

		String tag = context.getVersion();
		String description = context.getDescription();

		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		// Add
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("Adding to git for tag(%s) ... ", tag));
		m_git.add().addFilepattern(".").call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("Adding to git for tag(%s) ... DONE.", tag));

		// Commit
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("Commiting to git for tag(%s) ... ", tag));
		RevCommit revCommit = m_git.commit().setAll(true)
				.setMessage(description).call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("Commiting to git for tag(%s) ... DONE.", tag));

		// Tag
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("Taging to git for tag(%s) ... ", tag));
		try {
			m_git.tag().setName(tag).setMessage(description).call();
		} catch (Exception e) {
			m_reporter.categoryLog(DefaultStatusReporter.VERSION_LOG, tag,
					String.format("Tag(%s) already exists!", tag), e);
		}

		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("Taging to git for tag(%s) ... DONE.", tag));

		return revCommit.getId();
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
	public void pull(VersionContext context) throws Exception {

		String version = context.getVersion();

		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Pulling from git ... ");
		m_git.pull()
				.setProgressMonitor(
						new GitProgressMonitor(
								DefaultStatusReporter.VERSION_LOG, context,m_reporter))
				.call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Pulling from git ... DONE.");
	}

	@Override
	public void push(VersionContext context) throws Exception {

		String version = context.getVersion();

		if (m_git == null) {
			throw new IllegalStateException(
					"Please call setup() to initiailize git first!");
		}

		// Push heads
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Pushing to git heads ... ");
		m_git.push()
				.setProgressMonitor(
						new GitProgressMonitor(
								DefaultStatusReporter.VERSION_LOG, context,m_reporter))
				.setPushAll().call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Pushing to git heads ... DONE.");

		// Push heads
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Pushing to git tags ... ");
		m_git.push()
				.setPushTags()
				.setProgressMonitor(
						new GitProgressMonitor(
								DefaultStatusReporter.VERSION_LOG, context,m_reporter))
				.call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, version,
				"Pushing to git tags ... DONE");
	}

	@Override
	public void removeTag(VersionContext context) throws Exception {

		String tag = context.getVersion();

		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("removing tag(%s) from local git ... ", tag));
		m_git.tagDelete().setTags(tag).call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag, String
				.format("removing tag(%s) from local git ... DONE. ", tag));

		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag,
				String.format("removing tag(%s) from remote git ... ", tag));
		m_git.push()
				.setRefSpecs(new RefSpec(":" + REFS_TAGS + tag))
				.setProgressMonitor(
						new GitProgressMonitor(
								DefaultStatusReporter.VERSION_LOG, context,m_reporter))
				.call();
		m_reporter.log(DefaultStatusReporter.VERSION_LOG, tag, String
				.format("removing tag(%s) from remote git ... DONE. ", tag));
	}

	@Override
	public synchronized void setup(VersionContext context) throws Exception {

		String version = context.getVersion();

		if (m_git == null) {
			m_workingDir = new File(m_configManager.getGitWorkingDir());
			m_workingDir.mkdirs();

			File gitRepo = new File(m_workingDir, ".git");
			File phoenixHome = new File(this.getClass().getClassLoader()
					.getResource("git").toURI());

			if (!gitRepo.exists()) {
				String gitURL = m_configManager.getGitOriginUrl();
				m_reporter.log(DefaultStatusReporter.VERSION_LOG,
						version,
						String.format("Cloning repo from %s ... ", gitURL));
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				builder.setGitDir(gitRepo).readEnvironment().findGitDir();

				FS fs = builder.getFS();
				if (fs == null) {
					fs = FS.DETECTED;
				}
				fs.setUserHome(phoenixHome);

				Repository repository = builder.build();

				m_git = new Git(repository);
				CloneCommand clone = Git.cloneRepository();
				clone.setProgressMonitor(new GitProgressMonitor(
						DefaultStatusReporter.VERSION_LOG, context,m_reporter));
				clone.setBare(false);
				clone.setDirectory(m_workingDir);
				clone.setCloneAllBranches(true);
				clone.setURI(gitURL);
				try {
					clone.call();
				} catch (Exception e) {
					Files.forDir().delete(new File(m_workingDir, ".git"), true);
					e.printStackTrace();
					throw e;
				}
				m_reporter
						.log(DefaultStatusReporter.VERSION_LOG,
								version, String.format(
										"Cloning repo from %s ... DONE.",
										gitURL));
			} else {
				m_git = Git.open(m_workingDir,
						FS.DETECTED.setUserHome(phoenixHome));
			}
		}
	}
}
