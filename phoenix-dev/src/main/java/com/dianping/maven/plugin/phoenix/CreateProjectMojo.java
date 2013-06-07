package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.maven.plugin.common.PropertyProviders;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.PhoenixProject;
import com.dianping.maven.plugin.phoenix.model.entity.Router;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
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
    private WorkspaceFacade           m_wsFacade;
    private static final List<String> availableValues = Arrays.asList(new String[] { "dpindex-web", "shop-web",
            "shoplist-web", "user-web", "user-service", "user-base-service" });

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

        List<String> bizProjects = new ArrayList<String>();
        try {
            bizProjects = new ConsoleIO().choice(availableValues, 3, "Which project(s) to checkout(separate by comma)");
        } catch (IOException e) {
            throw new MojoFailureException("error choose projects", e);
        }

        // select workspace dir
        String wsDir = PropertyProviders.fromConsole().forString("workspace-dir", "Directory to put code",
                System.getProperty("user.dir"), null);

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

        Collection currentBizProjectNames = CollectionUtils.collect(model.getBizProjects(),
                new BizProjectToStringTransformer());
        System.out.println(String.format("Current project(s) in workspace (%s)",
                StringUtils.join(currentBizProjectNames, ",")));

        List<String> projectToAdd = null;
        List<String> projectToRemove = null;

        try {
            projectToAdd = new ConsoleIO().choice(
                    new ArrayList<String>(CollectionUtils.subtract(availableValues, currentBizProjectNames)), 3,
                    "Which project(s) to add(separate by comma)");
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

    private Workspace buildModel(List<String> bizProjects, String wsDir) {
        Workspace model = new Workspace();
        model.setDir(wsDir);
        for (String bizProjectName : bizProjects) {
            BizProject bizProject = new BizProject();
            bizProject.setName(bizProjectName);
            model.addBizProject(bizProject);
        }

        PhoenixProject phoenixProject = new PhoenixProject();
        // router
        Router router = new Router();
        router.setDefaultUrlPattern("http://w.51ping.com%s");
        router.setPort(8080);
        router.setVersion("0.1-SNAPSHOT");
        phoenixProject.setRouter(router);

        model.setPhoenixProject(phoenixProject);
        return model;
    }
}
