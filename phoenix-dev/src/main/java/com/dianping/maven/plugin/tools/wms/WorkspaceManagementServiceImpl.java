/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-5-14
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
package com.dianping.maven.plugin.tools.wms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.tools.generator.dynamic.ContainerBizServerGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.ContainerPomXMLGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.ContainerWebXMLGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.WorkspaceEclipseBatGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.WorkspaceEclipseSHGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.WorkspacePomXMLGenerator;
import com.dianping.maven.plugin.tools.vcs.RepositoryNotFoundException;
import com.dianping.maven.plugin.tools.vcs.RepositoryService;

/**
 * 
 * @author Leo Liang
 * 
 */
public class WorkspaceManagementServiceImpl implements WorkspaceManagementService {

    private final static String LINE_SEPARATOR      = System.getProperty("line.separator");
    private final static String PHOENIX_BASE_FOLDER = "phoenix/";
    private final static String CONTAINER_FOLDER    = PHOENIX_BASE_FOLDER + "phoenix-container";

    @Inject
    private RepositoryService   repositoryService;

    /**
     * @param repositoryService
     *            the repositoryService to set
     */
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public File create(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException {
        if (context.getProjects() != null && context.getBaseDir() != null && out != null) {

            printContent("Generating phoenix workspace...", out);

            if (context.getBaseDir().exists() && context.isCleanFolder()) {
                try {
                    FileUtils.cleanDirectory(context.getBaseDir());
                } catch (IOException e) {
                    throw new WorkspaceManagementException(e);
                }
                printContent(String.format("Workspace folder(%s) cleared...", context.getBaseDir()), out);
            }

            if (!context.getBaseDir().exists()) {
                try {
                    FileUtils.forceMkdir(context.getBaseDir());
                } catch (IOException e) {
                    throw new WorkspaceManagementException(e);
                }
                printContent(String.format("Workspace folder(%s) created...", context.getBaseDir()), out);
            }

            checkoutSource(context, out);

            printContent("Generating phoenix-container...", out);

            generateContainerProject(context, out);

            printContent("Generating phoenix-lib...", out);

            generateLib(context, out);

            printContent("Generating phoenix-workspace pom...", out);

            generateWorkspacePom(context);

            printContent("Generating phoenix-workspace startup script...", out);

            generateWorkspaceScript(context);

            printContent("Generating ws folder...", out);

            File workspaceFolder = new File(context.getBaseDir(), "ws");

            if (!workspaceFolder.exists()) {
                try {
                    FileUtils.forceMkdir(workspaceFolder);
                } catch (IOException e) {
                    throw new WorkspaceManagementException(e);
                }

                printContent("Phoenix workspace generated...", out);
            }

            return new File(context.getBaseDir(), CONTAINER_FOLDER);

        } else {
            throw new WorkspaceManagementException("projects/basedir can not be null");
        }
    }

    private void generateLib(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException {
        File libFolder = new File(context.getBaseDir(), PHOENIX_BASE_FOLDER + "lib");

        try {
            FileUtils.forceMkdir(libFolder);
            copyFile("byteman-2.1.2.jar", libFolder);
            copyFile("instrumentation-util-0.0.1.jar", libFolder);
        } catch (IOException e) {
            throw new WorkspaceManagementException(e);
        }
    }

    private void checkoutSource(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException {
        for (String project : context.getProjects()) {
            try {
                repositoryService.checkout(project, new File(context.getBaseDir(), project), out);
            } catch (RepositoryNotFoundException e) {
                throw new WorkspaceManagementException(e);
            }

        }
    }

    private void generateWorkspaceScript(WorkspaceContext context) throws WorkspaceManagementException {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                WorkspaceEclipseBatGenerator workspaceEclipseBatGenerator = new WorkspaceEclipseBatGenerator();
                File eclipseBatFile = new File(context.getBaseDir(), "eclipse.bat");
                workspaceEclipseBatGenerator.generate(eclipseBatFile, null);
                eclipseBatFile.setExecutable(true);
            } else {
                WorkspaceEclipseSHGenerator workspaceEclipseSHGenerator = new WorkspaceEclipseSHGenerator();
                File eclipseSHFile = new File(context.getBaseDir(), "eclipse.sh");
                workspaceEclipseSHGenerator.generate(eclipseSHFile, null);
                eclipseSHFile.setExecutable(true);
            }
        } catch (Exception e) {
            throw new WorkspaceManagementException(e);
        }
    }

    private void generateWorkspacePom(WorkspaceContext context) throws WorkspaceManagementException {
        WorkspacePomXMLGenerator generator = new WorkspacePomXMLGenerator();
        try {
            generator.generate(new File(context.getBaseDir(), "pom.xml"), context.getProjects());
        } catch (Exception e) {
            throw new WorkspaceManagementException(e);
        }

    }

    private void generateContainerProject(WorkspaceContext context, OutputStream out)
            throws WorkspaceManagementException {
        File projectBase = new File(context.getBaseDir(), CONTAINER_FOLDER);
        File sourceFolder = new File(projectBase, "src/main/java");
        File resourceFolder = new File(projectBase, "src/main/resources");
        File webinfFolder = new File(projectBase, "src/main/webapp/WEB-INF");
        try {
            FileUtils.forceMkdir(sourceFolder);
            FileUtils.forceMkdir(resourceFolder);
            FileUtils.forceMkdir(webinfFolder);

            copyFile("log4j.xml", resourceFolder);

            // web.xml
            ContainerWebXMLGenerator containerWebXMLGenerator = new ContainerWebXMLGenerator();
            containerWebXMLGenerator.generate(new File(webinfFolder, "web.xml"), null);

            // pom.xml
            ContainerPomXMLGenerator containerPomXMLGenerator = new ContainerPomXMLGenerator();
            Map<String, String> containerPomXMLGeneratorContext = new HashMap<String, String>();
            containerPomXMLGeneratorContext.put("phoenixRouterVersion", context.getPhoenixRouterVersion());
            containerPomXMLGenerator.generate(new File(projectBase, "pom.xml"), containerPomXMLGeneratorContext);

            // BizServer.java
            ContainerBizServerGenerator containerBizServerGenerator = new ContainerBizServerGenerator();
            containerBizServerGenerator.generate(new File(sourceFolder,
                    "com/dianping/phoenix/container/PhoenixServer.java"), null);

        } catch (Exception e) {
            throw new WorkspaceManagementException(e);
        }
    }

    private void copyFile(String fileName, File resourceFolder) throws FileNotFoundException, IOException {
        InputStream source = this.getClass().getResourceAsStream("/" + fileName);
        FileOutputStream dest = new FileOutputStream(new File(resourceFolder, fileName));
        IOUtils.copy(source, dest);
        IOUtils.closeQuietly(source);
        IOUtils.closeQuietly(dest);
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

    public static void main(String[] args) throws Exception {
        PlexusContainer plexusContainer = new DefaultPlexusContainer();
        WorkspaceManagementServiceImpl wms = new WorkspaceManagementServiceImpl();
        wms.setRepositoryService(plexusContainer.lookup(RepositoryService.class));
        WorkspaceContext context = new WorkspaceContext();
        List<String> projects = new ArrayList<String>();
        projects.add("shop-web");
        projects.add("shoplist-web");
        projects.add("user-web");
        projects.add("user-service");
        // projects.add("user-base-service");
        context.setProjects(projects);
        context.setBaseDir(new File("/Users/leoleung/test"));
        wms.modify(context, System.out);
    }

    @Override
    public File modify(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException {
        if (context.getProjects() != null && context.getBaseDir() != null && out != null) {

            printContent("Modifying phoenix workspace...", out);

            checkoutSource(context, out);

            printContent("Regenerating phoenix-workspace pom...", out);

            generateWorkspacePom(context);

            return new File(context.getBaseDir(), CONTAINER_FOLDER);

        } else {
            throw new WorkspaceManagementException("projects/basedir can not be null");
        }
    }
}
