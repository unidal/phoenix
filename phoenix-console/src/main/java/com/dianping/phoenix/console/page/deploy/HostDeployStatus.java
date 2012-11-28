package com.dianping.phoenix.console.page.deploy;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 12-11-28
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public class HostDeployStatus {

    private String m_host;

    private String m_action;

    private int m_progress;

    private String m_status;

    public String getHost() {
        return m_host;
    }

    public void setHost(String host) {
        this.m_host = host;
    }

    public String getAction() {
        return m_action;
    }

    public void setAction(String action) {
        this.m_action = action;
    }

    public int getProgress() {
        return m_progress;
    }

    public void setProgress(int progress) {
        this.m_progress = progress;
    }

    public String getStatus() {
        return m_status;
    }

    public void setStatus(String status) {
        this.m_status = status;
    }
}
