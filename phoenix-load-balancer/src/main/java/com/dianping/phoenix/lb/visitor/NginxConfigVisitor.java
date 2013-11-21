/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 30, 2013
 * 
 */
package com.dianping.phoenix.lb.visitor;

import java.util.HashMap;
import java.util.Map;

import com.dianping.phoenix.lb.constant.Constants;
import com.dianping.phoenix.lb.model.configure.entity.Directive;
import com.dianping.phoenix.lb.model.configure.entity.Location;
import com.dianping.phoenix.lb.model.configure.entity.Member;
import com.dianping.phoenix.lb.model.configure.entity.Pool;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;
import com.dianping.phoenix.lb.nginx.NginxConfig;
import com.dianping.phoenix.lb.nginx.NginxLocation;
import com.dianping.phoenix.lb.nginx.NginxLocation.MatchType;
import com.dianping.phoenix.lb.nginx.NginxServer;
import com.dianping.phoenix.lb.nginx.NginxUpstream;
import com.dianping.phoenix.lb.nginx.NginxUpstreamServer;

/**
 * @author Leo Liang
 * 
 */
public class NginxConfigVisitor extends AbstractVisitor<NginxConfig> {
    private Map<String, Strategy> strategies = new HashMap<String, Strategy>();

    public NginxConfigVisitor() {
        result = new NginxConfig();
    }

    @Override
    public void visitStrategy(Strategy strategy) {
        strategies.put(strategy.getName(), strategy);
    }

    @Override
    public void visitVirtualServer(VirtualServer virtualServer) {
        NginxServer server = new NginxServer();
        server.setProperties(virtualServer.getDynamicAttributes());
        server.setListen(virtualServer.getPort());
        server.setServerName(virtualServer.getDomain());
        NginxLocation defaultLocation = new NginxLocation();
        defaultLocation.setMatchType(MatchType.COMMON);
        defaultLocation.setPattern("/");
        Directive directive = new Directive();
        directive.setType("proxy_pass");
        directive.setDynamicAttribute("pool-name", toUpstreamName(virtualServer.getDefaultPoolName()));
        defaultLocation.addDirective(directive);
        server.addLocations(defaultLocation);
        result.setServer(server);

        super.visitVirtualServer(virtualServer);
    }

    @Override
    public void visitPool(Pool pool) {

        NginxUpstream upstream = new NginxUpstream();

        upstream.setLbStrategy(strategies.get(pool.getLoadbalanceStrategyName()));
        upstream.setName(toUpstreamName(pool.getName()));

        for (Member member : pool.getMembers()) {
            NginxUpstreamServer nginxUpstreamServer = new NginxUpstreamServer();
            nginxUpstreamServer.setMember(member);
            upstream.addServer(nginxUpstreamServer);
        }

        result.addUpstream(upstream);
    }

    @Override
    public void visitLocation(Location location) {
        NginxLocation nginxLocation = new NginxLocation();
        nginxLocation.setMatchType(toNginxMatchType(location));
        nginxLocation.setPattern(location.getPattern());
        nginxLocation.setDomain(location.getDomain());
        for (Directive directive : location.getDirectives()) {
            nginxLocation.addDirective(directive);
        }

        result.getServer().addLocations(nginxLocation);
    }

    private String toUpstreamName(String poolName) {
        return poolName;
    }

    private MatchType toNginxMatchType(Location location) {
        if (Constants.LOCATION_MATCHTYPE_PREFIX.equals(location.getMatchType())) {
            return MatchType.PREFIX;
        } else if (Constants.LOCATION_MATCHTYPE_REGEX.equals(location.getMatchType())) {
            return location.getCaseSensitive() ? MatchType.REGEX_CASE_SENSITIVE : MatchType.REGEX_CASE_INSENSITIVE;
        } else {
            return MatchType.COMMON;
        }
    }

}
