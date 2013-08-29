package com.dianping.maven.plugins.phoenix;

import java.io.File;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.dianping.maven.plugins.web.MojoDataWebUI.DataTransmitter;
import com.dianping.maven.plugins.web.UICreator;
import com.dianping.phoenix.dev.core.WorkspaceFacade;
import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.console.ConsoleIO;

/**
 * @goal projectEx
 * @requiresProject false
 */
public class ProjectMojoEx extends AbstractMojo {
    private static final String WORKSPACE_HTML = "/workspace.html";
    private static final String WORKSPACE_FROM = "plugin";
    /**
     * @component
     */
    private WorkspaceFacade     m_wsFacade;
    /**
     * @component
     */
    private ConsoleIO           consoleIO;
    /**
     * @component
     */
    private UICreator           uiCreator;

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

        try {
            String currentDir = System.getProperty("user.dir");
            consoleIO.message(String.format("Directory to put code(%s)", currentDir), true);
            String wsDir = consoleIO.readInput(currentDir);

            m_wsFacade.init(new File(wsDir));

            Workspace model = m_wsFacade.buildDefaultSkeletoModel();
            model.setDir(wsDir);
            model.setFrom(WORKSPACE_FROM);

            DataTransmitter<Workspace, Workspace> dataTransmitter = uiCreator.createUI(model, WORKSPACE_HTML);

            model = dataTransmitter.awaitResult();

            consoleIO.lineMessage(String.format("target dir: %s", model.getDir()), true);
            consoleIO
                    .lineMessage(
                            String.format("target projectes: %s", StringUtils.join(CollectionUtils.collect(
                                    model.getBizProjects(), new BizProjectToStringTransformer()), ",")), true);

            m_wsFacade.create(model);

        } catch (Exception e) {
            throw new MojoFailureException("error", e);
        }

    }

    private void modifyWorkspace(Workspace model) throws MojoFailureException {

        try {
            m_wsFacade.init(new File(model.getDir()));

            DataTransmitter<Workspace, Workspace> dataTransmitter = uiCreator.createUI(model, WORKSPACE_HTML);

            model = dataTransmitter.awaitResult();

            consoleIO.lineMessage(String.format("target dir: %s", model.getDir()), true);
            consoleIO
                    .lineMessage(
                            String.format("target projectes: %s", StringUtils.join(CollectionUtils.collect(
                                    model.getBizProjects(), new BizProjectToStringTransformer()), ",")), true);

            m_wsFacade.modify(model);

        } catch (Exception e) {
            throw new MojoFailureException("error", e);
        }

    }

    private static class BizProjectToStringTransformer implements Transformer {
        @Override
        public Object transform(Object input) {
            return ((BizProject) input).getName();
        }
    }
}
