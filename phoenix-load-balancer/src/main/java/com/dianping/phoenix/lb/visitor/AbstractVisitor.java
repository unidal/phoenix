/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 30, 2013
 * 
 */
package com.dianping.phoenix.lb.visitor;

import com.dianping.phoenix.lb.model.configure.transform.BaseVisitor;

/**
 * @author Leo Liang
 * 
 */
public abstract class AbstractVisitor<T> extends BaseVisitor {
    protected T result;

    public T getVisitorResult() {
        return result;
    }
}
