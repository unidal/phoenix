package com.dianping.phoenix.router;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.filter.FilterChain;
import com.dianping.phoenix.router.filter.request.DefaultFilterChain;
import com.dianping.phoenix.router.filter.request.F5UrlFilter;
import com.dianping.phoenix.router.filter.request.UrlRewriteFilter;

public class BaseTestConfigurator extends AbstractResourceConfigurator {

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ConfigManager.class, MockConfigManager.class));
		all.add(C(F5UrlFilter.class).req(ConfigManager.class));
		all.add(C(UrlRewriteFilter.class).req(ConfigManager.class));
		all.add(C(FilterChain.class, DefaultFilterChain.class) //
				.is(PER_LOOKUP) //
				.req(F5UrlFilter.class).req(UrlRewriteFilter.class));
		all.add(C(RequestMapper.class));
		
		return all;
	}
	
}
