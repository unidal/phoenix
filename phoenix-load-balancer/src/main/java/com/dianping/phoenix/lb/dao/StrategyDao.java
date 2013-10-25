/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-16
 * 
 */
package com.dianping.phoenix.lb.dao;

import java.util.List;

import com.dianping.phoenix.lb.model.configure.entity.Strategy;

/**
 * @author Leo Liang
 * 
 */
public interface StrategyDao {

    List<Strategy> list();

}
