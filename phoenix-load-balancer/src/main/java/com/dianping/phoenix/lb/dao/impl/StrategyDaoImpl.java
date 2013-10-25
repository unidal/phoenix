/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-18
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.util.List;

import com.dianping.phoenix.lb.dao.StrategyDao;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;

/**
 * @author Leo Liang
 * 
 */
public class StrategyDaoImpl extends AbstractDao implements StrategyDao {

    @Override
    public List<Strategy> list() {
        return store.listStrategies();
    }

}
