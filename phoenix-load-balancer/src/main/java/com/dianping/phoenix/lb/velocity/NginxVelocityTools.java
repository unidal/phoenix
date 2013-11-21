/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Nov 1, 2013
 * 
 */
package com.dianping.phoenix.lb.velocity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dianping.phoenix.lb.model.Availability;
import com.dianping.phoenix.lb.model.State;
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

    public String properties(Map<String, String> properties) {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String template = getTemplate("properties", entry.getKey());
            if (StringUtils.isNotBlank(template)) {
                Map<String, Object> context = new HashMap<String, Object>();
                context.put("value", entry.getValue());
                content.append(VelocityEngineManager.INSTANCE.merge(template, context));
            } else {
                content.append(entry.getKey() + " " +entry.getValue()).append(";");
            }
            content.append("\n");
        }
        return content.toString();
    }

    public String lbStrategy(Strategy strategy) {
        String template = getTemplate("strategy", strategy.getType());
        if (StringUtils.isNotBlank(template)) {
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("strategy", strategy);
            return VelocityEngineManager.INSTANCE.merge(template, context);
        } else {
            return "";
        }
    }

    public String directive(Directive directive) {
        String template = getTemplate("directive", directive.getType());
        if (StringUtils.isNotBlank(template)) {
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("directive", directive);
            return VelocityEngineManager.INSTANCE.merge(template, context);
        } else {
            return "";
        }
    }

    public String upstreamServer(NginxUpstreamServer server) {
        if (server.getMember().getAvailability() == Availability.AVAILABLE
                && server.getMember().getState() == State.ENABLED) {
            String template = getTemplate("upstream", "default");
            if (StringUtils.isNotBlank(template)) {
                Map<String, Object> context = new HashMap<String, Object>();
                context.put("server", server);
                return VelocityEngineManager.INSTANCE.merge(template, context);
            }
        }
        return "";
    }

    public String nowTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private String getTemplate(String schema, String file) {
        return TemplateManager.INSTANCE.getTemplate(schema, file);
    }
}
