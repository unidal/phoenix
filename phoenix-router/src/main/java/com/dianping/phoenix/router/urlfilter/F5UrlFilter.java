package com.dianping.phoenix.router.urlfilter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.model.entity.F5Rule;
import com.dianping.phoenix.router.model.entity.Pool;

public class F5UrlFilter extends ContainerHolder implements UrlFilter, Initializable {

	@Inject
	private ConfigManager config;
	private List<F5RuleWrapper> f5RuleWrapperList = new ArrayList<F5UrlFilter.F5RuleWrapper>();

	public UrlHolder filter(UrlHolder urlHolder, FilterChain filterChain) throws IOException {
		String path = urlHolder.getPath();
		UrlHolder newUrlHolder = urlHolder;
		for (F5RuleWrapper f5RuleWrapper : f5RuleWrapperList) {
			if (f5RuleWrapper.match(path)) {
				URL newUrl = new URL(f5RuleWrapper.map(path));
				newUrlHolder = new UrlHolder(newUrl);
				break;
			}
		}
		return filterChain.doFilter(newUrlHolder);
	}

	@Override
	public void initialize() throws InitializationException {
		List<F5Rule> f5RuleList = config.getRouterRules().getF5Rules();
		Map<String, String> pool2UrlPattern = new HashMap<String, String>();
		for (Pool pool : config.getRouterRules().getPools()) {
			pool2UrlPattern.put(pool.getName(), pool.getUrlPattern());
		}
		for (F5Rule f5Rule : f5RuleList) {
			f5RuleWrapperList.add(new F5RuleWrapper(f5Rule, pool2UrlPattern.get(f5Rule.getTargetPool())));
		}
	}

	private static class F5RuleWrapper implements Rule {
		private List<Pattern> patternList = new ArrayList<Pattern>();
		private String targetUrlPattern;

		public F5RuleWrapper(F5Rule f5Rule, String targetUrlPattern) {
			for (String pattern : f5Rule.getPatterns()) {
				patternList.add(Pattern.compile(pattern));
			}
			this.targetUrlPattern = targetUrlPattern;
		}

		public boolean match(String path) {
			for (Pattern pattern : patternList) {
				if (pattern.matcher(path).matches()) {
					return true;
				}
			}
			return false;
		}

		public String map(String path) {
			return String.format(targetUrlPattern, path);
		}
	}

}
