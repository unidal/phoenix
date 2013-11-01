/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Nov 1, 2013
 * 
 */
package com.dianping.phoenix.lb.velocity;

import org.apache.commons.lang3.StringUtils;

import com.dianping.phoenix.lb.constant.Constants;
import com.dianping.phoenix.lb.model.configure.entity.Directive;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.nginx.NginxLocation.MatchType;
import com.dianping.phoenix.lb.nginx.NginxUpstreamServer;

/**
 * @author Leo Liang
 * 
 */
public class NginxVelocityTools {
    public String locationMatchOp(MatchType matchType) {
        switch (matchType) {
            case COMMON:
                return "";
            case PREFIX:
                return "^~";
            case REGEX_CASE_INSENSITIVE:
                return "~*";
            case REGEX_CASE_SENSITIVE:
                return "~";
            default:
                return "";
        }
    }

    public String lbStrategy(Strategy strategy) {
        if ("ip-hash".equals(strategy.getType())) {
            return "ip_hash;\n";
        } else if ("url-hash".equals(strategy.getType())) {
            return "hash $request_uri;\nhash_method crc32;\n";
        } else {
            return "";
        }
    }

    public String directive(Directive directive) {
        if (Constants.DIRECTIVE_TYPE_PROXYPASS.equals(directive.getType())) {
            return String.format("proxy_pass http://%s;",
                    directive.getDynamicAttribute(Constants.DIRECTIVE_PROXYPASS_POOLNAME));
        } else if (Constants.DIRECTIVE_TYPE_REWRITE.equals(directive.getType())) {
            String next = "";
            String nextBreak = directive.getDynamicAttribute(Constants.DIRECTIVE_REWRITE_BREAK);
            String nextLast = directive.getDynamicAttribute(Constants.DIRECTIVE_REWRITE_LAST);
            if (StringUtils.isNotBlank(nextBreak) && StringUtils.equalsIgnoreCase("true", nextBreak)) {
                next = "break";
            } else if (StringUtils.isNotBlank(nextLast) && StringUtils.equalsIgnoreCase("true", nextLast)) {
                next = "last";
            }
            return String.format("rewrite %s %s %s;",
                    directive.getDynamicAttribute(Constants.DIRECTIVE_REWRITE_BEFORE),
                    directive.getDynamicAttribute(Constants.DIRECTIVE_REWRITE_AFTER), next);
        } else if (Constants.DIRECTIVE_TYPE_RETURN.equals(directive.getType())) {
            return String.format("return %s;", directive.getDynamicAttribute(Constants.DIRECTIVE_RETURN_CODE));
        } else if (Constants.DIRECTIVE_TYPE_STATICRES.equals(directive.getType())) {
            return String.format("root %s;expires %s;\n",
                    directive.getDynamicAttribute(Constants.DIRECTIVE_STATICRES_ROOTDOC),
                    directive.getDynamicAttribute(Constants.DIRECTIVE_STATICRES_EXP));
        }

        return "";
    }

    public String upstreamServer(NginxUpstreamServer server) {
        return String.format("server %s:%s       max_fails=%s  fail_timeout=%s;", server.getMember().getIp(), server
                .getMember().getPort(), server.getMember().getWeight(), server.getMember().getMaxFails());
    }
}
