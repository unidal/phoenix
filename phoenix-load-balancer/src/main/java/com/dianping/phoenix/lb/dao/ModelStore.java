/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-21
 * 
 */
package com.dianping.phoenix.lb.dao;

import java.io.IOException;
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

    public void updateOrCreateTemplate(String name, Template template) throws IOException;

    public void removeTemplate(String name) throws IOException;

    public void updateOrCreateStrategy(String name, Strategy strategy) throws IOException;

    public void removeStrategy(String name) throws IOException;

    public void updateVirtualServer(String name, VirtualServer virtualServer) throws IOException, BizException;

    public void removeVirtualServer(String name) throws BizException, IOException;

    public void addVirtualServer(String name, VirtualServer virtualServer) throws IOException, BizException;

}