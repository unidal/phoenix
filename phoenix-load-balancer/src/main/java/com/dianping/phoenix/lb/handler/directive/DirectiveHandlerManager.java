/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 28, 2013
 * 
 */
package com.dianping.phoenix.lb.handler.directive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Leo Liang
 * 
 */
public class DirectiveHandlerManager {
    private Map<String, DirectiveHandler> handlers;

    public void setHandlers(Map<String, DirectiveHandler> handlers) {
        this.handlers = handlers;
    }

    public List<String> availableDirectiveName() {
        return new ArrayList<String>(handlers.keySet());
    }
}
