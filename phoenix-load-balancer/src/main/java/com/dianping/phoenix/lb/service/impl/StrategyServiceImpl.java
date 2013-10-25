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
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.service.StrategyService;

/**
 * @author Leo Liang
 * 
 */
public class StrategyServiceImpl implements StrategyService {

    @Autowired
    private StrategyDao strategyDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.lb.service.StrategyService#listStrategies()
     */
    @Override
    public List<Strategy> listStrategies() {
        return strategyDao.list();
    }

}
