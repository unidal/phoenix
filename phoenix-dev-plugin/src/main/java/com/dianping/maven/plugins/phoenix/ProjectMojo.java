package com.dianping.maven.plugins.phoenix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.dianping.phoenix.dev.core.WorkspaceFacade;
import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.console.ConsoleIO;

/**
 * @goal project
 * @requiresProject false
 */
public class ProjectMojo extends AbstractMojo {
    /**
     * 
     */
    private static final int    CHOICE_COLUMN = 2;
    /**
     * @component
     */
    private WorkspaceFacade     m_wsFacade;

    /**
     * @component
     */
    private ConsoleIO           consoleIO;

    private static final String PROJECT_FROM  = "vcs";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Workspace model = m_wsFacade.current(new File(System.getProperty("user.dir")));
            if (model == null) {
                createWorkspace();
            } else {
                modifyWorkspace(model);
            }
        } catch (Exception e) {
            throw new MojoFailureException("error reload meta", e);
        }
    }

    private void createWorkspace() throws MojoFailureException {
        printHead();

        String wsDir = null;
        try {
            String currentDir = System.getProperty("user.dir");
            consoleIO.message(String.format("Directory to put code(%s)", currentDir), true);
            wsDir = consoleIO.readInput(currentDir);
        } catch (IOException e) {
            throw new MojoFailureException("error printing message", e);
        }

        m_wsFacade.init(new File(wsDir));

        List<String> bizProjects = addProjectInteractively(null);

        try {
            m_wsFacade.create(buildModel(bizProjects, wsDir));
        } catch (Exception e) {
            throw new MojoFailureException("error create phoenix workspace", e);
        }

    }

    private Workspace buildModel(List<String> bizProjects, String wsDir) throws Exception {
        Workspace model = m_wsFacade.buildDefaultSkeletoModel();

        model.setDir(wsDir);
        for (String bizProjectName : bizProjects) {
            BizProject bizProject = new BizProject();
            bizProject.setName(bizProjectName);
            bizProject.setFrom(PROJECT_FROM);
            model.addBizProject(bizProject);
        }

        return model;
    }

    private List<String> addProjectInteractively(List<String> ignoreProjects) throws MojoFailureException {
        List<String> bizProjects = new ArrayList<String>();
        while (true) {
            try {
                consoleIO.message("Input the pattern of project name wanna add(or 'quit' to skip addition): ");
                String pattern = consoleIO.readInput();

                if (StringUtils.isBlank(pattern)) {
                    consoleIO.lineMessage("No project pattern input.");
                    continue;
                }

                if ("quit".equalsIgnoreCase(pattern)) {
                    break;
                }

                List<String> choices = m_wsFacade.getProjectListByPattern(pattern.trim());

                if (ignoreProjects != null && !ignoreProjects.isEmpty()) {
                    choices.removeAll(ignoreProjects);
                }

                choices.removeAll(bizProjects);

                if (choices == null || choices.isEmpty()) {
                    consoleIO.lineMessage(String.format("No project matches the pattern(%s)", pattern));
                    continue;
                }

                bizProjects.addAll(new ConsoleIO().choice(choices, CHOICE_COLUMN,
                        "Which project(s) to add(separate by comma)"));
            } catch (IOException e) {
                throw new MojoFailureException("error choose projects", e);
            }

        }

        return bizProjects;
    }

    private void printHead() throws MojoFailureException {
        try {
            consoleIO.newLine();
            consoleIO.newLine();
        } catch (IOException e) {
            throw new MojoFailureException("error print head", e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void modifyWorkspace(Workspace model) throws MojoFailureException {

        class BizProjectToStringTransformer implements Transformer {
            @Override
            public Object transform(Object input) {
                return ((BizProject) input).getName();
            }
        }

        class BizProjectFromStringTransformer implements Transformer {
            @Override
            public Object transform(Object input) {
                BizProject bizProject = new BizProject();
                bizProject.setName((String) input);
                bizProject.setFrom(PROJECT_FROM);
                return bizProject;
            }
        }

        m_wsFacade.init(new File(model.getDir()));

        printHead();
        try {
            Collection currentBizProjectNames = CollectionUtils.collect(model.getBizProjects(),
                    new BizProjectToStringTransformer());
            consoleIO.lineMessage(String.format("Current project(s) in workspace (%s)",
                    StringUtils.join(currentBizProjectNames, ",")), true);

            List<String> projectToAdd = addProjectInteractively(new ArrayList<String>(currentBizProjectNames));
            List<String> projectToRemove = null;

            projectToRemove = new ConsoleIO().choice(new ArrayList<String>(currentBizProjectNames), CHOICE_COLUMN,
                    "Which project(s) to remove(separate by comma, or press Enter to skip)");

            model.getBizProjects().addAll(CollectionUtils.collect(projectToAdd, new BizProjectFromStringTransformer()));
            Collection<String> bizProjectsRemained = CollectionUtils.collect(model.getBizProjects(),
                    new BizProjectToStringTransformer());
            bizProjectsRemained.removeAll(projectToRemove);
            model.getBizProjects().clear();
            model.getBizProjects().addAll(
                    CollectionUtils.collect(bizProjectsRemained, new BizProjectFromStringTransformer()));
        } catch (IOException e) {
            throw new MojoFailureException("error choose projects", e);
        }

        try {
            m_wsFacade.modify(model);
        } catch (Exception e) {
            throw new MojoFailureException("error modify phoenix workspace", e);
        }

    }

}
