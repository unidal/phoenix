/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.phoenix.lb.constant.MessageID;
import com.dianping.phoenix.lb.dao.StrategyDao;
import com.dianping.phoenix.lb.dao.VirtualServerDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.Availability;
import com.dianping.phoenix.lb.model.State;
import com.dianping.phoenix.lb.model.configure.entity.Configure;
import com.dianping.phoenix.lb.model.configure.entity.Directive;
import com.dianping.phoenix.lb.model.configure.entity.Instance;
import com.dianping.phoenix.lb.model.configure.entity.Location;
import com.dianping.phoenix.lb.model.configure.entity.Member;
import com.dianping.phoenix.lb.model.configure.entity.Pool;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;
import com.dianping.phoenix.lb.service.ConcurrentControlServiceTemplate;
import com.dianping.phoenix.lb.service.VirtualServerService;
import com.dianping.phoenix.lb.utils.ExceptionUtils;
import com.dianping.phoenix.lb.velocity.TemplateManager;
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
    private StrategyDao      strategyDao;

    /**
     * @param virtualServerDao
     * @param templateDao
     */
    public VirtualServerServiceImpl(VirtualServerDao virtualServerDao, StrategyDao strategyDao) {
        super();
        this.virtualServerDao = virtualServerDao;
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

            if (pool.getMembers().size() == 0) {
                ExceptionUtils.throwBizException(MessageID.POOL_NO_MEMBER, pool.getName());
            }

            int availMemberCount = 0;
            for (Member member : pool.getMembers()) {
                if (member.getAvailability() == Availability.AVAILABLE && member.getState() == State.ENABLED) {
                    availMemberCount++;
                }
            }

            if (availMemberCount * 100.0d / pool.getMembers().size() < pool.getMinAvailableMemberPercentage()) {
                ExceptionUtils.throwBizException(MessageID.POOL_LOWER_THAN_MINAVAIL_PCT,
                        pool.getMinAvailableMemberPercentage(), pool.getName());
            }
        }

        for (Location location : virtualServer.getLocations()) {
            for (Directive directive : location.getDirectives()) {
                if (!TemplateManager.INSTANCE.availableFiles("directive").contains(directive.getType())) {
                    ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_DIRECTIVE_TYPE_NOT_SUPPORT,
                            directive.getType());
                }
            }

        }
    }

    @Override
    public String generateNginxConfig(VirtualServer virtualServer) throws BizException {
        validate(virtualServer);

        Configure tmpConfigure = new Configure();
        for (Strategy strategy : strategyDao.list()) {
            tmpConfigure.addStrategy(strategy);
        }

        tmpConfigure.addVirtualServer(virtualServer);

        NginxConfigVisitor visitor = new NginxConfigVisitor();
        tmpConfigure.accept(visitor);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("config", visitor.getVisitorResult());
        return VelocityEngineManager.INSTANCE.merge(TemplateManager.INSTANCE.getTemplate("server", "default"), context);
    }

    @Override
    public String push(final String virtualServerName, final int virtualServerVersion) throws BizException {
        if (StringUtils.isBlank(virtualServerName)) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_NAME_EMPTY);
        }

        return read(new ReadOperation<String>() {

            @Override
            public String doRead() throws BizException {
                String tagId = virtualServerDao.tag(virtualServerName, virtualServerVersion);
                VirtualServer tagVs = virtualServerDao.getTag(virtualServerName, tagId);
                List<Instance> instances = virtualServerDao.find(virtualServerName).getInstances();
                String nginxConfig = generateNginxConfig(tagVs);
                pushConfig(nginxConfig, instances);
                return tagId;
            }

        });

    }

    private void pushConfig(String nginxConfig, List<Instance> instances) {
        for (Instance instance : instances) {
            // TODO 
            // 1. commit git
            // 2. push git
            // 3. notify pull git
            // 4. call nginx reload 
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.VirtualServerService#listPushIds(java
     * .lang.String)
     */
    @Override
    public List<String> listPushIds(final String virtualServerName) throws BizException {
        if (StringUtils.isBlank(virtualServerName)) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_NAME_EMPTY);
        }

        return read(new ReadOperation<List<String>>() {

            @Override
            public List<String> doRead() throws BizException {
                return virtualServerDao.listTags(virtualServerName);
            }

        });
    }

    @Override
    public VirtualServer findTagById(final String virtualServerName, final String tagId) throws BizException {
        if (StringUtils.isBlank(virtualServerName)) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_NAME_EMPTY);
        }

        if (StringUtils.isBlank(tagId)) {
            ExceptionUtils.throwBizException(MessageID.VIRTUALSERVER_TAGID_EMPTY);
        }

        return read(new ReadOperation<VirtualServer>() {

            @Override
            public VirtualServer doRead() throws BizException {
                return virtualServerDao.findTagById(virtualServerName, tagId);
            }

        });
    }

}
