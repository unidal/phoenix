/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 28, 2013
 * 
 */
package com.dianping.phoenix.lb.handler.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dianping.phoenix.lb.handler.Handler;

/**
 * @author Leo Liang
 * 
 */
public class StrategyHandlerManager implements Handler {

    private Map<String, StrategyHandler> handlers;

    public void setHandlers(Map<String, StrategyHandler> handlers) {
        this.handlers = handlers;
    }

    public List<String> availableStrategyName() {
        return new ArrayList<String>(handlers.keySet());
    }
}
