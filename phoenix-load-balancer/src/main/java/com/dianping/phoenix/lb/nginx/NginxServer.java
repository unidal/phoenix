/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 30, 2013
 * 
 */
package com.dianping.phoenix.lb.nginx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Leo Liang
 * 
 */
public class NginxServer {
    private List<NginxLocation> locations = new ArrayList<NginxLocation>();
    private int                 listen    = 80;
    private String              serverName;
    private Map<String, String> properties;

    /**
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * @return the locations
     */
    public List<NginxLocation> getLocations() {
        return locations;
    }

    public void addLocations(NginxLocation location) {
        this.locations.add(location);
    }

    /**
     * @return the listen
     */
    public int getListen() {
        return listen;
    }

    /**
     * @param listen
     *            the listen to set
     */
    public void setListen(int listen) {
        this.listen = listen;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName
     *            the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

}
