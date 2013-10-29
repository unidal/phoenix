/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-21
 * 
 */
package com.dianping.phoenix.lb.dao;

import java.util.List;

import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.Template;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;

/**
 * @author Leo Liang
 * 
 */
public interface ModelStore {

    public List<VirtualServer> listVirtualServers();

    public List<Strategy> listStrategies();

    public List<Template> listTemplates();

    public Template findTemplate(String name);

    public Strategy findStrategy(String name);

    public VirtualServer findVirtualServer(String name);

    public void updateOrCreateTemplate(String name, Template template) throws BizException;

    public void removeTemplate(String name) throws BizException;

    public void updateOrCreateStrategy(String name, Strategy strategy) throws BizException;

    public void removeStrategy(String name) throws BizException;

    public void updateVirtualServer(String name, VirtualServer virtualServer) throws BizException;

    public void removeVirtualServer(String name) throws BizException;

    public void addVirtualServer(String name, VirtualServer virtualServer) throws BizException;

}