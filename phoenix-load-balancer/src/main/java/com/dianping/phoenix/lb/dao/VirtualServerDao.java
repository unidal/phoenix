/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-16
 * 
 */
package com.dianping.phoenix.lb.dao;

import java.util.List;

import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;

/**
 * @author Leo Liang
 * 
 */
public interface VirtualServerDao {

    VirtualServer find(String virtualServerName);

    void add(VirtualServer virtualServer) throws BizException;

    void update(VirtualServer virtualServer) throws BizException;

    List<VirtualServer> list();

    void delete(String virtualServerName) throws BizException;

}
