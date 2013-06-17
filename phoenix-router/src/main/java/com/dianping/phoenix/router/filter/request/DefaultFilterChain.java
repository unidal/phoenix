package com.dianping.phoenix.router.filter.request;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.router.filter.AbstractFilterChain;
import com.dianping.phoenix.router.filter.Filter;
import com.dianping.phoenix.router.filter.FilterChain;

public class DefaultFilterChain extends AbstractFilterChain<RequestHolder> implements FilterChain<RequestHolder>,
        Initializable {

    @Inject
    private VirtualServerFilter virtualServerFilter;
    @Inject
    private F5UrlFilter         f5Filter;
    @Inject
    private UrlRewriteFilter    urlRewriteFilter;
    @Inject
    private HeaderFilter        headerFilter;

    public DefaultFilterChain() {
    }

    public DefaultFilterChain(Filter<RequestHolder>... urlFilters) {
        for (Filter<RequestHolder> filter : urlFilters) {
            addFilter(filter);
        }
    }

    @Override
    public void initialize() throws InitializationException {
        addFilter(virtualServerFilter);
        addFilter(f5Filter);
        addFilter(urlRewriteFilter);
        addFilter(headerFilter);
    }
}
