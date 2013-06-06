/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-6-6
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.maven.plugin.tools.vcs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.tools.wms.GitRepository;
import com.dianping.maven.plugin.tools.wms.Repository;
import com.dianping.maven.plugin.tools.wms.RepositoryManager;
import com.dianping.maven.plugin.tools.wms.SvnRepository;

/**
 * 
 * @author Leo Liang
 * 
 */
public class DefaultRepositoryServiceImpl implements RepositoryService {
    private final static String  LINE_SEPARATOR = System.getProperty("line.separator");
    @Inject
    private RepositoryManager    repositoryManager;
    @Inject
    private CodeRetrieverManager codeRetrieverManager;

    @Override
    public void checkout(String project, File outputFolder, OutputStream logOutput) throws RepositoryNotFoundException {
        Repository repository = repositoryManager.find(project);
        if (repository == null) {
            throw new RepositoryNotFoundException(String.format("Project(%s) not found...", project));
        }

        if (!outputFolder.exists()) {
            CodeRetrieveConfig codeRetrieveConfig = toCodeRetrieveConfig(repository, outputFolder.getAbsolutePath(),
                    logOutput);
            if (codeRetrieveConfig != null) {
                printContent(String.format("Checking out project %s(repo:%s)...", project, repository.getRepoUrl()),
                        logOutput);
                codeRetrieverManager.getCodeRetriever(codeRetrieveConfig).retrieveCode();
            } else {
                printContent(String.format("Project repository(%s) unknown...", project), logOutput);
            }
        } else {
            printContent(
                    String.format("Dir already exists(%s), skip project(%s) source checkout!",
                            outputFolder.getAbsolutePath(), project), logOutput);
        }
    }

    private void printContent(String content, OutputStream out) {

        try {
            out.write(("---------------------------------------------" + LINE_SEPARATOR).getBytes());
            out.write((content + LINE_SEPARATOR).getBytes());
            out.write(("---------------------------------------------" + LINE_SEPARATOR).getBytes());
        } catch (IOException e) {
            // ignore
        }
    }

    private CodeRetrieveConfig toCodeRetrieveConfig(Repository repository, String path, OutputStream out) {
        if (repository instanceof SvnRepository) {
            return new SVNCodeRetrieveConfig(repository.getRepoUrl(), path, out,
                    ((SvnRepository) repository).getRevision());
        } else if (repository instanceof GitRepository) {
            return new GitCodeRetrieveConfig(repository.getRepoUrl(), path, out,
                    ((GitRepository) repository).getBranch());
        } else {
            return null;
        }
    }

}
