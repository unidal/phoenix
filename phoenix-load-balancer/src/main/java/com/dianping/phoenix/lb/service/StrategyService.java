/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service;

import java.util.List;

import com.dianping.phoenix.lb.model.configure.entity.Strategy;

/**
 * @author Leo Liang
 * 
 */
public interface StrategyService {
    List<Strategy> listStrategies();
}
