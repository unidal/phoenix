package com.dianping.phoenix.router.filter.request;

import java.io.IOException;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;

import com.dianping.phoenix.router.filter.Filter;
import com.dianping.phoenix.router.filter.FilterChain;

public class UrlRewriteFilter extends ContainerHolder implements Filter<RequestHolder>, Initializable {

    @Override
    public RequestHolder filter(RequestHolder urlHolder, FilterChain<RequestHolder> filterChain) throws IOException {
        String path = urlHolder.getPath();
        if (isPhoenixPath(path)) {
            int sndSlashIdx = path.indexOf("/", 1);
            String pathPrefix = path.substring(0, sndSlashIdx);
            String pathPostfix = path.substring(sndSlashIdx);
            for (UrlRewriteRuleWrapper rule : urlHolder.getUrlRewriteRules()) {
                if (rule.match(pathPostfix)) {
                    urlHolder.setPath(pathPrefix + rule.map(pathPostfix));
                    break;
                }
            }
        }
        return filterChain.doFilter(urlHolder);
    }

    private boolean isPhoenixPath(String path) {
        return path.startsWith("/_");
    }

    @Override
    public void initialize() throws InitializationException {
    }

}
