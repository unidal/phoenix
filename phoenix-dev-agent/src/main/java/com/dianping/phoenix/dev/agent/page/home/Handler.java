package com.dianping.phoenix.dev.agent.page.home;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
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
    private final static Logger logger              = Logger.getLogger(Handler.class);

    private Gson                gson                = new Gson();
    private static final String DEPLOY_PATH         = "/data/webapps/phoenix-dev/";
    private static final int    START_TIMEOUT       = 3;
    private static final String STARTED_LOG_PATTERN = "Started SocketConnector@0.0.0.0:8080";

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

                    executeShell(new File(param.deployPath, "start.sh").getAbsolutePath());

                } catch (Exception e) {
                    reqRes.addError(Status.WORKSPACE_RUNTIMEERROR, e.getMessage());
                }
            }
        }

        if (reqRes.success && !checkStarted(new File(param.deployPath, "boot.log"), param.startTimeout)) {
            reqRes.addError(Status.START_FAIL, "Start phoenix server fail.");
            executeShell(new File(param.deployPath, "stop.sh").getAbsolutePath());
        }

        if (param == null || StringUtils.isBlank(param.returnUrl)) {
            ctx.getHttpServletResponse().getOutputStream().write(reqRes.toJson().getBytes());
        } else {
            responseResultToReturnUrl(param.returnUrl, reqRes.toJson());
        }

    }

    private void responseResultToReturnUrl(String returnUrl, String json) {
        URL reqUrl;
        try {
            reqUrl = new URL(returnUrl);
            HttpURLConnection conn = (HttpURLConnection) reqUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setDoOutput(true);

            PrintWriter out = new PrintWriter(conn.getOutputStream());

            out.print(URLEncoder.encode("response", "UTF-8") + "=" + URLEncoder.encode(json, "UTF-8"));

            out.close();

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");

            IOUtils.toString(inputStreamReader);
        } catch (Exception e) {
            logger.error(String.format("Request returnUrl(%s) fail.", returnUrl), e);
        }
    }

    private boolean checkStarted(File logFile, int timeout) {
        long nanosTimeout = TimeUnit.MINUTES.toNanos(timeout);
        long lastTime = System.nanoTime();
        while (true) {
            if (startedPatternExist(logFile)) {
                return true;
            }

            long now = System.nanoTime();
            nanosTimeout -= now - lastTime;
            lastTime = now;

            if (nanosTimeout <= 0) {
                if (startedPatternExist(logFile)) {
                    return true;
                }
                logger.warn("Started check timeout.");
                return false;
            }
        }
    }

    private boolean startedPatternExist(File logFile) {
        List<String> logs = null;
        try {
            logs = FileUtils.readLines(logFile);
        } catch (IOException e) {
            // ignore
        }
        if (logs != null) {
            for (String line : logs) {
                if (line.indexOf(STARTED_LOG_PATTERN) >= 0) {
                    logger.info("Started check success.");
                    return true;
                }
            }
        }
        return false;
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
                    File stopScript = new File(deployPath, "stop.sh");
                    if (stopScript.exists()) {
                        executeShell(stopScript.getAbsolutePath());
                    }

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

    private void executeShell(String scriptPath) throws IOException {
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
        OK(200), PARAM_INVALIDATE(201), WAR_INVALIDATE(202), DEPLOYPATH_INVALIDATE(203), WORKSPACE_RUNTIMEERROR(204), START_FAIL(
                205);
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
        private String   deployPath   = DEPLOY_PATH;
        private boolean  forceDeploy  = false;
        private int      startTimeout = START_TIMEOUT;
    }

    public static void main(String[] args) throws Exception {
        // ReqParam param = new ReqParam();
        // param.wars = new String[] { "sss" };
        // Gson gson = new Gson();
        // System.out.println(gson.toJson(param));

        ScriptExecutor scriptExecutor = new DefaultScriptExecutor();
        scriptExecutor.exec("jps -lvm | echo 1", System.out, System.out);
    }
}
