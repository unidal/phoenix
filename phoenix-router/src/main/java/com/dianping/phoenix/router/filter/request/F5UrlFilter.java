package com.dianping.phoenix.router.filter.request;

import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;

import com.dianping.phoenix.router.filter.Filter;
import com.dianping.phoenix.router.filter.FilterChain;

public class F5UrlFilter extends ContainerHolder implements Filter<RequestHolder>, Initializable {

    public RequestHolder filter(RequestHolder urlHolder, FilterChain<RequestHolder> filterChain) throws IOException {
        String path = urlHolder.getPath();
        RequestHolder newUrlHolder = urlHolder;

        for (F5RuleWrapper f5RuleWrapper : urlHolder.getF5Rules()) {
            if (f5RuleWrapper.match(path)) {
                URL newUrl = new URL(f5RuleWrapper.map(path));
                newUrlHolder = new RequestHolder(newUrl, urlHolder);
                break;
            }
        }
        return filterChain.doFilter(newUrlHolder);
    }

    @Override
    public void initialize() throws InitializationException {

    }

}
