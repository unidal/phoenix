package com.dianping.phoenix.dev.agent.page.home;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.dev.agent.shell.DefaultScriptExecutor;
import com.dianping.phoenix.dev.agent.shell.ScriptExecutor;
import com.dianping.phoenix.dev.core.WorkspaceFacade;
import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceConstants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Handler implements PageHandler<Context> {
    @Inject
    private WorkspaceFacade     m_workspaceFacade;

    private Gson                gson        = new Gson();
    private static final String DEPLOY_PATH = "/data/webapps/phoenix-dev/";

    @Override
    @PayloadMeta(Payload.class)
    @InboundActionMeta(name = "home")
    public void handleInbound(Context ctx) throws ServletException, IOException {

    }

    @Override
    @OutboundActionMeta(name = "home")
    public void handleOutbound(Context ctx) throws ServletException, IOException {

        ReqResult reqRes = new ReqResult();
        ReqParam param = validateAndParse(ctx.getPayload(), reqRes);

        if (param != null) {
            if (checkWarUrls(param.wars, reqRes) && checkDeployPath(param.deployPath, param.forceDeploy, reqRes)) {
                m_workspaceFacade.init(new File(param.deployPath));
                try {
                    m_workspaceFacade.create(convertToWorkspace(param));

                    startPhoenixServer(new File(param.deployPath, "start.sh").getAbsolutePath());

                } catch (Exception e) {
                    reqRes.addError(Status.WORKSPACE_RUNTIMEERROR, e.getMessage());
                }
            }
        }

        ctx.getHttpServletResponse().getOutputStream().write(reqRes.toJson().getBytes());

    }

    private Workspace convertToWorkspace(ReqParam param) {
        Workspace model = m_workspaceFacade.buildDefaultSkeletoModel();
        model.setFrom(WorkspaceConstants.FROM_AGENT);

        model.setDir(param.deployPath);
        for (String bizProjectName : param.wars) {
            BizProject bizProject = new BizProject();
            bizProject.setName(bizProjectName);
            model.addBizProject(bizProject);
        }

        return model;
    }

    private boolean checkDeployPath(String deployPath, boolean forceDeploy, ReqResult reqRes) {
        File deployDir = new File(deployPath);

        if (deployDir.exists() && deployDir.isFile()) {
            reqRes.addError(Status.DEPLOYPATH_INVALIDATE,
                    String.format("Deploy path %s exists but not a dir.", deployPath));
            return false;
        }

        if (deployDir.exists()) {
            File[] subFiles = deployDir.listFiles();
            if (subFiles.length > 0 && !forceDeploy) {
                reqRes.addError(Status.DEPLOYPATH_INVALIDATE, String.format("Deploy dir %s not empty.", deployPath));
                return false;
            } else if (subFiles.length > 0 && forceDeploy) {
                try {
                    killPhoenixServer();

                    FileUtils.cleanDirectory(deployDir);
                } catch (Exception e) {
                    reqRes.addError(Status.DEPLOYPATH_INVALIDATE, e.getMessage());
                    return false;
                }

            }

        } else {
            try {
                FileUtils.forceMkdir(deployDir);
            } catch (Exception e) {
                reqRes.addError(Status.DEPLOYPATH_INVALIDATE, e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void killPhoenixServer() throws IOException {
        ScriptExecutor scriptExecutor = new DefaultScriptExecutor();
        scriptExecutor
                .exec("jps -lvm | awk -v javaclass=com.dianping.phoenix.container.PhoenixServer '$2==javaclass{cmd=sprintf(\"kill -9 %s\", $1, $1);system(cmd)}'",
                        System.out, System.out);
    }

    private void startPhoenixServer(String scriptPath) throws IOException {
        ScriptExecutor scriptExecutor = new DefaultScriptExecutor();
        scriptExecutor.exec(scriptPath, System.out, System.out);
    }

    private boolean checkWarUrls(String[] wars, ReqResult reqRes) {
        if (wars == null || wars.length == 0) {
            reqRes.addError(Status.WAR_INVALIDATE, "Request wars blank.");
            return false;
        }
        return true;
    }

    private ReqParam validateAndParse(Payload payload, ReqResult reqRes) {
        ReqParam reqParam = null;
        if (StringUtils.isBlank(payload.getParams())) {
            reqRes.addError(Status.PARAM_INVALIDATE, "Request params blank.");
            return null;
        }

        try {
            reqParam = gson.fromJson(payload.getParams(), ReqParam.class);
        } catch (JsonSyntaxException e) {
            reqRes.addError(Status.PARAM_INVALIDATE, "Request params is not a validating json.");
            return null;
        }

        return reqParam;
    }

    private static enum Status {
        OK(200), PARAM_INVALIDATE(201), WAR_INVALIDATE(202), DEPLOYPATH_INVALIDATE(203), WORKSPACE_RUNTIMEERROR(204);
        private int code;

        private Status(int code) {
            this.code = code;
        }

    }

    private class ReqResult {
        private Map<Integer, List<String>> errors  = new HashMap<Integer, List<String>>();
        private boolean                    success = true;

        public void addError(Status errorCode, String errorMsg) {
            if (!errors.containsKey(errorCode)) {
                errors.put(errorCode.code, new ArrayList<String>());
            }
            errors.get(errorCode.code).add(errorMsg);
            success = false;
        }

        public String toJson() {
            return gson.toJson(this);
        }

    }

    private static class ReqParam {
        private String[] wars;
        private String   returnUrl;
        private String   deployPath  = DEPLOY_PATH;
        private boolean  forceDeploy = false;

    }
}
