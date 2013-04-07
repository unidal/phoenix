package com.dianping.phoenix.router;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

public class RouteService extends ContainerHolder {

	private static Logger log = Logger.getLogger(RouteService.class);

	@Inject
	private RuleManager ruleMgr;

	protected String getTargetUrl(String originUrl, String queryString) {

		String targetUrl = originUrl;
		if (shouldDispatchToBizCtx(originUrl)) {
			targetUrl = ruleMgr.trans(originUrl);
			if (!StringUtils.isBlank(queryString)) {
				targetUrl = String.format("%s?%s", targetUrl, queryString);
			}
			log.info(String.format("trans url %s to %s", originUrl, targetUrl));
		}
		return targetUrl;
	}

	private boolean shouldDispatchToBizCtx(String reqUri) {
		return !reqUri.startsWith("_") && !"/favicon.ico".equals(reqUri);
	}

}
