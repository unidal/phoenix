package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.dianping.maven.plugin.phoenix.MojoDataWebUI.DataTransmitter;
import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.transform.DefaultSaxParser;
import com.dianping.maven.plugin.tools.console.ConsoleIO;
import com.dianping.maven.plugin.tools.remedy.PomRemedy;

/**
 * @goal projectEx
 * @requiresProject false
 */
public class CreateProjectMojoEx extends AbstractMojo {
    /**
     * @component
     */
    private WorkspaceFacade m_wsFacade;
    /**
     * @component
     */
    private ConsoleIO       consoleIO;

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

            Workspace model = null;
            model = buildDefaultWorkspace();
            model.setDir(wsDir);

            DataTransmitter<Workspace, Workspace> dataTransmitter = createUI(model, "/createWs.html");

            model = dataTransmitter.awaitResult();

            consoleIO.lineMessage(String.format("target dir: %s", model.getDir()), true);
            consoleIO
                    .lineMessage(
                            String.format("target projectes: %s", StringUtils.join(CollectionUtils.collect(
                                    model.getBizProjects(), new BizProjectToStringTransformer()), ",")), true);

            m_wsFacade.create(model);

            PomRemedy.INSTANCE.remedyPomIn(new File(model.getDir()));
        } catch (Exception e) {
            throw new MojoFailureException("error", e);
        }

    }

    private DataTransmitter<Workspace, Workspace> createUI(Workspace model, String displayUri) throws Exception,
            MalformedURLException {
        DataTransmitter<Workspace, Workspace> dataTransmitter = new DataTransmitter<Workspace, Workspace>(model);
        Map<String, BaseMojoDataServlet<Workspace, Workspace>> servletMapping = new HashMap<String, BaseMojoDataServlet<Workspace, Workspace>>();
        servletMapping.put("/req/*", new DefaultMojoDataServlet(dataTransmitter, m_wsFacade, "/req/"));

        MojoDataWebUI<Workspace, Workspace> webUI = new MojoDataWebUI<Workspace, Workspace>(servletMapping);
        webUI.start();
        webUI.display(displayUri);
        return dataTransmitter;
    }

    private void modifyWorkspace(Workspace model) throws MojoFailureException {

        try {
            m_wsFacade.init(new File(model.getDir()));

            DataTransmitter<Workspace, Workspace> dataTransmitter = createUI(model, "/modifyWs.html");

            model = dataTransmitter.awaitResult();

            consoleIO.lineMessage(String.format("target dir: %s", model.getDir()), true);
            consoleIO
                    .lineMessage(
                            String.format("target projectes: %s", StringUtils.join(CollectionUtils.collect(
                                    model.getBizProjects(), new BizProjectToStringTransformer()), ",")), true);

            m_wsFacade.modify(model);

            PomRemedy.INSTANCE.remedyPomIn(new File(model.getDir()));
        } catch (Exception e) {
            throw new MojoFailureException("error", e);
        }

    }

    private Workspace buildDefaultWorkspace() throws MojoFailureException {
        InputStream defaultWorkspaceXml = this.getClass().getResourceAsStream("/workspace-default.xml");
        Workspace model;
        try {
            model = DefaultSaxParser.parse(defaultWorkspaceXml);
            return model;
        } catch (Exception e) {
            throw new MojoFailureException("error read workspace-default.xml", e);
        }
    }

    private static class BizProjectToStringTransformer implements Transformer {
        @Override
        public Object transform(Object input) {
            return ((BizProject) input).getName();
        }
    }
}
