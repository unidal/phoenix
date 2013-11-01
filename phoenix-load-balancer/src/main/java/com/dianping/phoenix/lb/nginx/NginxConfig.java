/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 30, 2013
 * 
 */
package com.dianping.phoenix.lb.nginx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo Liang
 * 
 */
public class NginxConfig {
    private NginxServer         server;
    private List<NginxUpstream> upstreams = new ArrayList<NginxUpstream>();

    /**
     * @return the servers
     */
    public NginxServer getServer() {
        return server;
    }

    /**
     * @param servers
     *            the servers to set
     */
    public void setServer(NginxServer server) {
        this.server = server;
    }

    /**
     * @return the upstreams
     */
    public List<NginxUpstream> getUpstreams() {
        return upstreams;
    }

    public void addUpstream(NginxUpstream upstream) {
        this.upstreams.add(upstream);
    }

}
