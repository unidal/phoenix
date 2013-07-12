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
package com.dianping.phoenix.dev.core.tools.wms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.codehaus.plexus.util.StringUtils;
import org.unidal.lookup.annotation.Inject;
import org.zeroturnaround.zip.ZipUtil;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerBizServerForAgentGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerBizServerGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerPomXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerWebXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceEclipseBatGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceEclipseSHGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspacePomXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceStartSHGenerator;
import com.dianping.phoenix.dev.core.tools.vcs.RepositoryNotFoundException;
import com.dianping.phoenix.dev.core.tools.vcs.RepositoryService;

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

            if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
                generateContainerProject(context, out);
            } else {
                generateContainerWar(context, out);
            }

            printContent("Generating phoenix-lib...", out);

            generateLib(context, out);

            printContent("Generating phoenix-workspace pom...", out);

            generateWorkspacePom(context);

            printContent("Generating phoenix-workspace startup script...", out);

            generateWorkspaceScript(context);

            printContent("Generating ws folder...", out);

            if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
                File workspaceFolder = new File(context.getBaseDir(), "ws");

                if (!workspaceFolder.exists()) {
                    try {
                        FileUtils.forceMkdir(workspaceFolder);
                    } catch (IOException e) {
                        throw new WorkspaceManagementException(e);
                    }

                    printContent("Phoenix workspace generated...", out);
                }
            }

            if (WorkspaceConstants.FROM_AGENT.equalsIgnoreCase(context.getFrom())) {
                generateAgentStartScript(context);
            }

            printContent("All done. Cheers~", out);

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
        if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
            for (BizProject project : context.getProjects()) {
                try {
                    repositoryService.checkout(project.getName(), new File(context.getBaseDir(), project.getName()),
                            out);
                } catch (RepositoryNotFoundException e) {
                    throw new WorkspaceManagementException(e);
                }
            }
        } else if (WorkspaceConstants.FROM_AGENT.equalsIgnoreCase(context.getFrom())) {
            for (BizProject project : context.getProjects()) {
                try {
                    project.setName(getFromUrl(project.getName(), context.getBaseDir(), out));
                } catch (Exception e) {
                    throw new WorkspaceManagementException(e);
                }
            }
        }
    }

    public String getFromUrl(String url, File baseDir, OutputStream out) throws Exception {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String fileName = extractFileName(url);
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try {
            is = new BufferedInputStream(new URL(url).openStream());
            os = new BufferedOutputStream(new FileOutputStream(new File(baseDir, fileName)));
            IOUtils.copyLarge(is, os);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {

                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {

                }
            }
        }

        ZipUtil.explode(new File(baseDir, fileName));

        return fileName;
    }

    private String extractFileName(String url) {
        String name = url;
        int pos = name.lastIndexOf("/");
        if (pos >= 0) {
            name = name.substring(pos + 1);
        }

        pos = -1;

        String[] envPatterns = new String[] { "-qa-", "-dev-", "-alpha-", "-product-" };

        for (String pat : envPatterns) {
            pos = name.indexOf(pat);
            if (pos >= 0) {
                name = name.substring(0, pos);
                break;
            }
        }

        return name;
    }

    private void generateAgentStartScript(WorkspaceContext context) throws WorkspaceManagementException {

        WorkspaceStartSHGenerator workspaceStartSHGenerator = new WorkspaceStartSHGenerator();
        File startSh = new File(context.getBaseDir(), "start.sh");
        try {
            workspaceStartSHGenerator.generate(startSh, new ArrayList<String>());
        } catch (Exception e) {
            throw new WorkspaceManagementException(e);
        }
        startSh.setExecutable(true);
    }

    private void generateWorkspaceScript(WorkspaceContext context) throws WorkspaceManagementException {
        if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
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
    }

    private void generateWorkspacePom(WorkspaceContext context) throws WorkspaceManagementException {
        if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
            List<String> projectNames = new ArrayList<String>();
            for (BizProject project : context.getProjects()) {
                projectNames.add(project.getName());
            }

            WorkspacePomXMLGenerator generator = new WorkspacePomXMLGenerator();
            try {
                generator.generate(new File(context.getBaseDir(), "pom.xml"), projectNames);
            } catch (Exception e) {
                throw new WorkspaceManagementException(e);
            }
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
            if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
                ContainerBizServerGenerator containerBizServerGenerator = new ContainerBizServerGenerator();
                containerBizServerGenerator.generate(new File(sourceFolder,
                        "com/dianping/phoenix/container/PhoenixServer.java"), null);
            } else {
                ContainerBizServerForAgentGenerator containerBizServerForAgentGenerator = new ContainerBizServerForAgentGenerator();
                containerBizServerForAgentGenerator.generate(new File(sourceFolder,
                        "com/dianping/phoenix/container/PhoenixServer.java"), null);
            }

        } catch (Exception e) {
            throw new WorkspaceManagementException(e);
        }
    }

    private void generateContainerWar(WorkspaceContext context, OutputStream out) throws WorkspaceManagementException {
        File warBase = new File(context.getBaseDir(), CONTAINER_FOLDER);
        File webInfFolder = new File(warBase, "WEB-INF");
        File phoenixServerFolder = new File(webInfFolder, "classes/com/dianping/phoenix/container/");
        try {
            FileUtils.forceMkdir(webInfFolder);
            FileUtils.forceMkdir(phoenixServerFolder);

            String libZipName = "lib.zip";
            copyFile(libZipName, webInfFolder);
            ZipUtil.unpack(new File(webInfFolder, libZipName), webInfFolder);
            FileUtils.deleteQuietly(new File(webInfFolder, libZipName));

            ContainerWebXMLGenerator containerWebXMLGenerator = new ContainerWebXMLGenerator();
            containerWebXMLGenerator.generate(new File(webInfFolder, "web.xml"), null);

            String serverClassFileName = "AgentPhoenixServer.classfile";
            copyFile(serverClassFileName, phoenixServerFolder);
            FileUtils.moveFile(new File(phoenixServerFolder, serverClassFileName), new File(phoenixServerFolder,
                    "PhoenixServer.class"));

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
            out.write(("[INFO] ------------------------------------------------------------------------" + LINE_SEPARATOR)
                    .getBytes());
            out.write(("[INFO] " + content + LINE_SEPARATOR).getBytes());
            out.write(("[INFO] ------------------------------------------------------------------------" + LINE_SEPARATOR)
                    .getBytes());
        } catch (IOException e) {
            // ignore
        }
    }

    // public static void main(String[] args) throws Exception {
    // PlexusContainer plexusContainer = new DefaultPlexusContainer();
    // WorkspaceManagementServiceImpl wms = new
    // WorkspaceManagementServiceImpl();
    // wms.setRepositoryService((RepositoryService)
    // plexusContainer.lookup(RepositoryService.class));
    // WorkspaceContext context = new WorkspaceContext();
    // List<BizProject> projects = new ArrayList<BizProject>();
    // BizProject p1 = new BizProject();
    // p1.setName("shop-web");
    // projects.add(p1);
    // BizProject p2 = new BizProject();
    // p2.setName("shoplist-web");
    // projects.add(p2);
    // context.setProjects(projects);
    // context.setBaseDir(new File("/Users/leoleung/test"));
    // wms.modify(context, System.out);
    // }

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
