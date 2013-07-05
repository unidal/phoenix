package com.dianping.maven.plugin.tools.vcs;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;

public class GitCodeRetriever implements ICodeRetriever {
    private GitCodeRetrieveConfig gitConfig;

    @Override
    public void retrieveCode() {
        String localPath = gitConfig.getLocalPath();
        LogService logService = new LogService(gitConfig.getLogOutput());
        ProgressMonitor pm = new GitCodeRetrieveProcessMonitor(logService, String.format("Cloning %s",
                gitConfig.getRepoUrl()));
        try {
//            logService.log(System.getProperty("line.separator"));
            Git git = Git.cloneRepository().setDirectory(new File(localPath)).setURI(gitConfig.getRepoUrl())
                    .setProgressMonitor(pm).call();
            String branch = gitConfig.getBranchName();
            git.checkout().setName(branch).call();
            logService.log(System.getProperty("line.separator"));
        } catch (Exception e) {
            throw new RetrieveException(e);
        }
    }

    @Override
    public void setConfig(CodeRetrieveConfig config) {
        gitConfig = (GitCodeRetrieveConfig) config;
        gitConfig.validate();
    }

    public static void main(String[] args) {
        GitCodeRetriever gitCodeRetriever = new GitCodeRetriever();
        GitCodeRetrieveConfig config = new GitCodeRetrieveConfig("http://code.dianpingoa.com/tuangou/tuangou-web.git",
                "/Users/leoleung/test", System.out, "master");
        gitCodeRetriever.setConfig(config);
        gitCodeRetriever.retrieveCode();
    }
}
