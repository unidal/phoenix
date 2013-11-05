/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service;

import java.util.List;

import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;

/**
 * @author Leo Liang
 * 
 */
public interface VirtualServerService {
    List<VirtualServer> listVirtualServers();

    VirtualServer findVirtualServer(String virtualServerName) throws BizException;

    void addVirtualServer(String virtualServerName, VirtualServer virtualServer) throws BizException;

    void deleteVirtualServer(String virtualServerName) throws BizException;

    void modifyVirtualServer(String virtualServerName, VirtualServer virtualServer) throws BizException;

    String generateNginxConfig(VirtualServer virtualServer) throws BizException;
}
