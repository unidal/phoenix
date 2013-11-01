/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.phoenix.lb.constant.Constants;
import com.dianping.phoenix.lb.constant.MessageID;
import com.dianping.phoenix.lb.dao.StrategyDao;
import com.dianping.phoenix.lb.dao.TemplateDao;
import com.dianping.phoenix.lb.dao.VirtualServerDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Configure;
import com.dianping.phoenix.lb.model.configure.entity.Directive;
import com.dianping.phoenix.lb.model.configure.entity.Location;
import com.dianping.phoenix.lb.model.configure.entity.Pool;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.Template;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;
import com.dianping.phoenix.lb.service.ConcurrentControlServiceTemplate;
import com.dianping.phoenix.lb.service.VirtualServerService;
import com.dianping.phoenix.lb.utils.ExceptionUtils;
import com.dianping.phoenix.lb.velocity.VelocityEngineManager;
import com.dianping.phoenix.lb.visitor.NginxConfigVisitor;

/**
 * @author Leo Liang
 * 
 */
public class VirtualServerServiceImpl extends ConcurrentControlServiceTemplate implements VirtualServerService {
    @Autowired
    private VirtualServerDao virtualServerDao;
    @Autowired
    private TemplateDao      templateDao;
    @Autowired
    private StrategyDao      strategyDao;

    /**
     * @param virtualServerDao
     * @param templateDao
     */
    public VirtualServerServiceImpl(VirtualServerDao virtualServerDao, TemplateDao templateDao, StrategyDao strategyDao) {
        super();
        this.virtualServerDao = virtualServerDao;
        this.templateDao = templateDao;
        this.strategyDao = strategyDao;
    }

    /**
     * @param strategyDao
     *            the strategyDao to set
     */
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }

    /**
     * @param virtualServerDao
     *            the virtualServerDao to set
     */
    public void setVirtualServerDao(VirtualServerDao virtualServerDao) {
        this.virtualServerDao = virtualServerDao;
    }

    /**
     * @param templateDao
     *            the templateDao to set
     */
    public void setTemplateDao(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.VirtualServerService#listVirtualServers()
     */
    @Override
    public List<VirtualServer> listVirtualServers() {
        try {
            return read(new ReadOperation<List<VirtualServer>>() {

                @Override
                public List<VirtualServer> doRead() throws Exception {
                    return virtualServerDao.list();
                }
            });
        } catch (BizException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.VirtualServerService#findVirtualServer
     * (java.lang.String)
     */
    @Override
    public VirtualServer findVirtualServer(final String virtualServerName) throws BizException {
        if (StringUtils.isBlank(virtualServerName)) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_NAME_EMPTY);
        }

        return read(new ReadOperation<VirtualServer>() {

            @Override
            public VirtualServer doRead() throws BizException {
                return virtualServerDao.find(virtualServerName);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.VirtualServerService#addVirtualServer
     * (java.lang.String,
     * com.dianping.phoenix.lb.model.configure.entity.VirtualServer)
     */
    @Override
    public void addVirtualServer(String virtualServerName, final VirtualServer virtualServer) throws BizException {
        if (virtualServerName == null || virtualServer == null) {
            return;
        }

        if (!virtualServerName.equals(virtualServer.getName())) {
            return;
        }

        validate(virtualServer);

        write(new WriteOperation<Void>() {

            @Override
            public Void doWrite() throws Exception {
                virtualServerDao.add(virtualServer);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.VirtualServerService#deleteVirtualServer
     * (java.lang.String)
     */
    @Override
    public void deleteVirtualServer(final String virtualServerName) throws BizException {
        if (StringUtils.isBlank(virtualServerName)) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_NAME_EMPTY);
        }

        try {
            write(new WriteOperation<Void>() {

                @Override
                public Void doWrite() throws Exception {
                    virtualServerDao.delete(virtualServerName);
                    return null;
                }
            });
        } catch (BizException e) {
            // ignore
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.VirtualServerService#modifyVirtualServer
     * (java.lang.String,
     * com.dianping.phoenix.lb.model.configure.entity.VirtualServer)
     */
    @Override
    public void modifyVirtualServer(final String virtualServerName, final VirtualServer virtualServer)
            throws BizException {
        if (virtualServerName == null || virtualServer == null) {
            return;
        }

        if (!virtualServerName.equals(virtualServer.getName())) {
            return;
        }

        validate(virtualServer);

        write(new WriteOperation<Void>() {

            @Override
            public Void doWrite() throws Exception {
                virtualServerDao.update(virtualServer);
                return null;
            }
        });

    }

    private void validate(VirtualServer virtualServer) throws BizException {
        boolean deafultPoolExist = false;
        for (Pool pool : virtualServer.getPools()) {
            if (pool.getName().equals(virtualServer.getDefaultPoolName())) {
                deafultPoolExist = true;
                break;
            }
        }

        if (!deafultPoolExist) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_DEFAULTPOOL_NOT_EXISTS,
                    virtualServer.getDefaultPoolName());
        }

        if (templateDao.find(virtualServer.getTemplateName()) == null) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_TEMPLATE_NOT_EXISTS,
                    virtualServer.getTemplateName());
        }

        List<Strategy> strategies = strategyDao.list();
        List<String> strategyNames = new ArrayList<String>();
        for (Strategy strategy : strategies) {
            strategyNames.add(strategy.getName());
        }

        for (Pool pool : virtualServer.getPools()) {
            if (!strategyNames.contains(pool.getLoadbalanceStrategyName())) {
                ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_STRATEGY_NOT_SUPPORT,
                        pool.getLoadbalanceStrategyName(), pool.getName());
            }
        }

        for (Location location : virtualServer.getLocations()) {
            for (Directive directive : location.getDirectives()) {
                if (!Arrays.asList(Constants.DIRECTIVE_TYPES).contains(directive.getType())) {
                    ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_DIRECTIVE_TYPE_NOT_SUPPORT,
                            directive.getType());
                }
            }

        }
    }

    @Override
    public String generateNginxConfig(VirtualServer virtualServer) throws BizException {
        validate(virtualServer);

        Template template = templateDao.find(virtualServer.getTemplateName());
        if (template == null) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_TEMPLATE_NOT_EXISTS,
                    virtualServer.getTemplateName());
        }

        Configure tmpConfigure = new Configure();
        for (Strategy strategy : strategyDao.list()) {
            tmpConfigure.addStrategy(strategy);
        }

        tmpConfigure.addVirtualServer(virtualServer);

        NginxConfigVisitor visitor = new NginxConfigVisitor();
        tmpConfigure.accept(visitor);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("config", visitor.getVisitorResult());
        return VelocityEngineManager.INSTANCE.merge(template.getContent(), context);
    }
}
