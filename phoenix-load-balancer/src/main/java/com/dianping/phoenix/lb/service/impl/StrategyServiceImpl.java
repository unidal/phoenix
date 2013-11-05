/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.phoenix.lb.dao.StrategyDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.service.ConcurrentControlServiceTemplate;
import com.dianping.phoenix.lb.service.StrategyService;

/**
 * @author Leo Liang
 * 
 */
public class StrategyServiceImpl extends ConcurrentControlServiceTemplate implements StrategyService {

    @Autowired
    private StrategyDao strategyDao;

    /**
     * @param strategyDao
     */
    public StrategyServiceImpl(StrategyDao strategyDao) {
        super();
        this.strategyDao = strategyDao;
    }

    /**
     * @param strategyDao
     *            the strategyDao to set
     */
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.lb.service.StrategyService#listStrategies()
     */
    @Override
    public List<Strategy> listStrategies() {
        try {
            return read(new ReadOperation<List<Strategy>>() {

                @Override
                public List<Strategy> doRead() throws Exception {
                    return strategyDao.list();
                }
            });
        } catch (BizException e) {
            // ignore
            return null;
        }
    }
}
