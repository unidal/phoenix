package com.dianping.phoenix.agent.core.task.processor.kernel.qa;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.StringUtils;

import com.dianping.phoenix.agent.util.ThreadUtil;
import com.dianping.phoenix.agent.util.url.UrlContentFetcher;

@SuppressWarnings({ "rawtypes" })
public class DefaultQaService implements QaService {

	private final static Logger logger = Logger.getLogger(DefaultQaService.class);

	@Inject
	private UrlContentFetcher urlContentFetcher;

	private final static String submitArgumentPattern = "?op=submitTask&feature=%s&env=%s&host=%s&port=%s";
	private final static String queryArgumentPattern = "?op=getTaskStatus&token=%s";
	private final static ObjectMapper jsonMapper = new ObjectMapper();

	@Override
	public CheckResult isDomainHealthy(DomainHealthCheckInfo domainInfo, int timeout, int queryInterval) {

		long endBefore = System.currentTimeMillis() + timeout;

		String submitArgument = String.format(submitArgumentPattern, domainInfo.getDomain(), domainInfo.getEnv(),
				domainInfo.getHost(), domainInfo.getPort());
		String submitUrl = domainInfo.getQaServiceUrlPrefix() + submitArgument;

		String submitResultJson = readUrlToJsonString(submitUrl);

		Map submitResultMap = safeJsonToMap(submitResultJson);

		String token = (String) submitResultMap.get("token");
		String submitStatus = (String) submitResultMap.get("status");

		if (!"ok".equalsIgnoreCase(submitStatus)) {
			logger.error(String.format("can't submit task to qa service, the response is %s", submitResultJson));
			return CheckResult.SUBMIT_FAILED;
		}

		while (System.currentTimeMillis() < endBefore) {
			String queryArgument = String.format(queryArgumentPattern, token);
			String queryUrl = domainInfo.getQaServiceUrlPrefix() + queryArgument;
			String queryResultJson = readUrlToJsonString(queryUrl);
			Map queryResultMap = safeJsonToMap(queryResultJson);
			// FAIL, PENDING, RUNNING, SUCCESS
			String taskStatus = (String) queryResultMap.get("status");
			if ("SUCCESS".equalsIgnoreCase(taskStatus)) {
				return CheckResult.PASS;
			} else if ("FAIL".equalsIgnoreCase(taskStatus)) {
				return CheckResult.FAIL;
			} else {
				logger.info(String.format("qa service result is %s", taskStatus));
				ThreadUtil.sleepQuiet(queryInterval);
			}
		}
		return CheckResult.TIMEOUT;
	}

	private Map safeJsonToMap(String json) {
		Map result = Collections.EMPTY_MAP;
		try {
			result = jsonMapper.readValue(json, Map.class);
		} catch (Exception e) {
			logger.error(String.format("error parsing json", json), e);
		}
		return result;
	}

	String readUrlToJsonString(String url) {
		logger.info(String.format("calling qa service %s", url));
		String content = "{}";
		try {
			content = StringUtils.trimAll(urlContentFetcher.fetchUrlContent(url));
			logger.info(String.format("got %s", content));
		} catch (Exception e) {
			logger.error("error get url content", e);
		}
		return content;
	}

}
