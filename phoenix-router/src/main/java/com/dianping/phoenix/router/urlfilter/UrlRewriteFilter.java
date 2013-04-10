package com.dianping.phoenix.router.urlfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.model.entity.UrlRewriteRule;

public class UrlRewriteFilter extends ContainerHolder implements UrlFilter, Initializable {

	@Inject
	private ConfigManager config;
	private List<UrlRewriteRuleWrapper> ruleWrapperList = new ArrayList<UrlRewriteRuleWrapper>();

	@Override
	public UrlHolder filter(UrlHolder urlHolder, FilterChain filterChain) throws IOException {
		String path = urlHolder.getPath();
		if (isPhoenixPath(path)) {
			int sndSlashIdx = path.indexOf("/", 1);
			String pathPrefix = path.substring(0, sndSlashIdx);
			String pathPostfix = path.substring(sndSlashIdx);
			for (UrlRewriteRuleWrapper rule : ruleWrapperList) {
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
		for(UrlRewriteRule rule : config.getRouterRules().getUrlRewriteRules()) {
			ruleWrapperList.add(new UrlRewriteRuleWrapper(rule));
		}
	}

	private static class UrlRewriteRuleWrapper implements Rule {

		private String pathRegex;
		private Pattern pathPattern;
		private String pathTarget;

		public UrlRewriteRuleWrapper(UrlRewriteRule rule) {
			pathRegex = rule.getMaskUrl();
			pathPattern = Pattern.compile(pathRegex);
			pathTarget = rule.getRealUrl();
		}

		@Override
		public boolean match(String path) {
			return pathPattern.matcher(path).matches();
		}

		@Override
		public String map(String path) {
			return path.replaceAll(pathRegex, pathTarget);
		}

	}

}
