/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 25, 2013
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.phoenix.lb.dao.ModelStore;

/**
 * @author Leo Liang
 * 
 */
public class AbstractDao {
    @Autowired
    protected ModelStore store;

}
