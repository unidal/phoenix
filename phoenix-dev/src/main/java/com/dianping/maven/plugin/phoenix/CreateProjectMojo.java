package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.maven.plugin.common.PropertyProviders;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.transform.DefaultSaxParser;
import com.dianping.maven.plugin.tools.console.ConsoleIO;
import com.dianping.maven.plugin.tools.remedy.PomRemedy;

/**
 * @goal project
 * @requiresProject false
 */
public class CreateProjectMojo extends AbstractMojo {
    /**
     * @component
     */
    private WorkspaceFacade m_wsFacade;

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

        // select workspace dir
        String wsDir = PropertyProviders.fromConsole().forString("workspace-dir", "Directory to put code",
                System.getProperty("user.dir"), null);

        m_wsFacade.init(new File(wsDir));

        List<String> bizProjects = addProjectInteractively(null);

        Workspace model = buildModel(bizProjects, wsDir);

        try {
            m_wsFacade.create(model);
        } catch (Exception e) {
            throw new MojoFailureException("error create phoenix workspace", e);
        }

        try {
            PomRemedy.INSTANCE.remedyPomIn(new File(model.getDir()));
        } catch (Exception e) {
            throw new MojoFailureException("error remedy pom", e);
        }
    }

    private List<String> addProjectInteractively(List<String> ignoreProjects) throws MojoFailureException {
        List<String> bizProjects = new ArrayList<String>();
        Scanner cin = new Scanner(System.in);
        while (true) {
            System.out.print("Input the project prefix wanna add: ");
            String prefix = cin.nextLine();

            if (StringUtils.isBlank(prefix)) {
                System.out.print("No project prefix input.");

                if (addMore(cin)) {
                    continue;
                } else {
                    break;
                }
            }

            try {
                List<String> choices = m_wsFacade.getProjectListByPrefix(prefix.trim());
                if (choices == null || choices.isEmpty()) {
                    System.out.println(String.format("No project matches the prefix(%s)", prefix));
                    continue;
                }

                if (ignoreProjects != null && !ignoreProjects.isEmpty()) {
                    choices.removeAll(ignoreProjects);
                }

                bizProjects.addAll(new ConsoleIO().choice(choices, 3, "Which project(s) to add(separate by comma)"));
            } catch (IOException e) {
                throw new MojoFailureException("error choose projects", e);
            }

            if (!addMore(cin)) {
                break;
            }
        }

        return bizProjects;
    }

    private boolean addMore(Scanner cin) {
        boolean addmore = true;
        while (true) {
            System.out.print("Add more projects?(y/n)  ");
            String loop = cin.next();
            if (StringUtils.isNotBlank(loop)) {
                if ("y".equalsIgnoreCase(loop.trim())) {
                    System.out.println();
                    break;
                } else if ("n".equalsIgnoreCase(loop.trim())) {
                    addmore = false;
                    break;
                }
            }
        }
        return addmore;
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
                return bizProject;
            }
        }

        m_wsFacade.init(new File(model.getDir()));

        Collection currentBizProjectNames = CollectionUtils.collect(model.getBizProjects(),
                new BizProjectToStringTransformer());
        System.out.println(String.format("Current project(s) in workspace (%s)",
                StringUtils.join(currentBizProjectNames, ",")));

        List<String> projectToAdd = addProjectInteractively(new ArrayList<String>(currentBizProjectNames));
        List<String> projectToRemove = null;

        try {
            projectToRemove = new ConsoleIO().choice(new ArrayList<String>(currentBizProjectNames), 3,
                    "Which project(s) to remove(separate by comma)");
        } catch (IOException e) {
            throw new MojoFailureException("error choose projects", e);
        }

        model.getBizProjects().addAll(CollectionUtils.collect(projectToAdd, new BizProjectFromStringTransformer()));
        Collection<String> bizProjectsRemained = CollectionUtils.collect(model.getBizProjects(),
                new BizProjectToStringTransformer());
        bizProjectsRemained.removeAll(projectToRemove);
        model.getBizProjects().clear();
        model.getBizProjects().addAll(
                CollectionUtils.collect(bizProjectsRemained, new BizProjectFromStringTransformer()));

        try {
            m_wsFacade.modify(model);
        } catch (Exception e) {
            throw new MojoFailureException("error modify phoenix workspace", e);
        }

        try {
            PomRemedy.INSTANCE.remedyPomIn(new File(model.getDir()));
        } catch (Exception e) {
            throw new MojoFailureException("error remedy pom", e);
        }
    }

    private Workspace buildModel(List<String> bizProjects, String wsDir) throws MojoFailureException {
        InputStream defaultWorkspaceXml = this.getClass().getResourceAsStream("/workspace-default.xml");
        Workspace model;
        try {
            model = DefaultSaxParser.parse(defaultWorkspaceXml);
        } catch (Exception e) {
            throw new MojoFailureException("error read workspace-default.xml", e);
        }

        model.setDir(wsDir);
        for (String bizProjectName : bizProjects) {
            BizProject bizProject = new BizProject();
            bizProject.setName(bizProjectName);
            model.addBizProject(bizProject);
        }

        return model;
    }
}
