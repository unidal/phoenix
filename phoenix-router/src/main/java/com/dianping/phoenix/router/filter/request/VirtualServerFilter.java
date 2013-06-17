package com.dianping.phoenix.router.filter.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.filter.Filter;
import com.dianping.phoenix.router.filter.FilterChain;
import com.dianping.phoenix.router.model.entity.F5Rule;
import com.dianping.phoenix.router.model.entity.Pool;
import com.dianping.phoenix.router.model.entity.RouterRules;
import com.dianping.phoenix.router.model.entity.UrlRewriteRule;

public class VirtualServerFilter extends ContainerHolder implements Filter<RequestHolder>, Initializable {

    private static Logger                            log                                       = Logger.getLogger(VirtualServerFilter.class);
    @Inject
    private ConfigManager                            config;
    private Map<String, List<F5RuleWrapper>>         virtualServerF5RuleWrapperMapping         = new HashMap<String, List<F5RuleWrapper>>();
    private Map<String, List<UrlRewriteRuleWrapper>> virtualServerUrlRewriteRuleWrapperMapping = new HashMap<String, List<UrlRewriteRuleWrapper>>();

    public RequestHolder filter(RequestHolder urlHolder, FilterChain<RequestHolder> filterChain) throws IOException {
        String virtualServer = urlHolder.getVirtualServer();

        fillF5Rules(urlHolder, virtualServer);

        fillUrlRewriteRules(urlHolder, virtualServer);

        if (urlHolder.getF5Rules() == null) {
            String errMsg = String.format("No f5 rules found for virtual server(%s), original uri is %s",
                    virtualServer, urlHolder.getPath());
            log.error(errMsg);
            throw new RuntimeException(errMsg);
        }

        if (urlHolder.getUrlRewriteRules() == null) {
            String errMsg = String.format("No url rewrite rules found for virtual server(%s), original uri is %s",
                    virtualServer, urlHolder.getPath());
            log.error(errMsg);
            throw new RuntimeException(errMsg);
        }

        log.info(String.format("Reqest handled(virtual server=%s, forward uri=%s).", virtualServer, urlHolder.getPath()));

        return filterChain.doFilter(urlHolder);
    }

    private void fillUrlRewriteRules(RequestHolder urlHolder, String virtualServer) {
        if (virtualServer == null || !virtualServerUrlRewriteRuleWrapperMapping.containsKey(virtualServer)) {
            urlHolder.setUrlRewriteRules(virtualServerUrlRewriteRuleWrapperMapping.get("Default"));
        } else {
            urlHolder.setUrlRewriteRules(virtualServerUrlRewriteRuleWrapperMapping.get(virtualServer));
        }
    }

    private void fillF5Rules(RequestHolder urlHolder, String virtualServer) {
        if (virtualServer == null || !virtualServerF5RuleWrapperMapping.containsKey(virtualServer)) {
            urlHolder.setF5Rules(virtualServerF5RuleWrapperMapping.get("Default"));
        } else {
            urlHolder.setF5Rules(virtualServerF5RuleWrapperMapping.get(virtualServer));
        }
    }

    @Override
    public void initialize() throws InitializationException {
        initF5Rules();
        initRewriteRules();
    }

    private void initRewriteRules() {
        for (Entry<String, RouterRules> entry : config.getVirtualServerRulesMapping().entrySet()) {
            virtualServerUrlRewriteRuleWrapperMapping.put(entry.getKey(), new ArrayList<UrlRewriteRuleWrapper>());

            for (UrlRewriteRule rule : entry.getValue().getUrlRewriteRules()) {
                virtualServerUrlRewriteRuleWrapperMapping.get(entry.getKey()).add(new UrlRewriteRuleWrapper(rule));
            }
        }
    }

    private void initF5Rules() {
        for (Entry<String, RouterRules> entry : config.getVirtualServerRulesMapping().entrySet()) {
            virtualServerF5RuleWrapperMapping.put(entry.getKey(), new ArrayList<F5RuleWrapper>());

            List<F5Rule> f5RuleList = entry.getValue().getF5Rules();
            Map<String, String> pool2UrlPattern = new HashMap<String, String>();
            for (Pool pool : entry.getValue().getPools()) {
                pool2UrlPattern.put(pool.getName(), pool.getUrlPattern());
            }

            for (F5Rule f5Rule : f5RuleList) {
                String targetPool = pool2UrlPattern.get(f5Rule.getTargetPool());
                if (targetPool == null) {
                    targetPool = pool2UrlPattern.get("Default");
                }
                virtualServerF5RuleWrapperMapping.get(entry.getKey()).add(new F5RuleWrapper(f5Rule, targetPool));
            }
        }
    }

}
