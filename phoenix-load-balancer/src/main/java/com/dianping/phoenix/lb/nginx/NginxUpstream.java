/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 30, 2013
 * 
 */
package com.dianping.phoenix.lb.nginx;

import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.lb.model.configure.entity.Strategy;

/**
 * @author Leo Liang
 * 
 */
public class NginxUpstream {
    private String                    name;
    private List<NginxUpstreamServer> servers = new ArrayList<NginxUpstreamServer>();
    private Strategy                  lbStrategy;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the servers
     */
    public List<NginxUpstreamServer> getServers() {
        return servers;
    }

    public void addServer(NginxUpstreamServer server) {
        this.servers.add(server);
    }

    /**
     * @return the lbStrategy
     */
    public Strategy getLbStrategy() {
        return lbStrategy;
    }

    /**
     * @param lbStrategy
     *            the lbStrategy to set
     */
    public void setLbStrategy(Strategy lbStrategy) {
        this.lbStrategy = lbStrategy;
    }

}
