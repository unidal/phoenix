package com.dianping.phoenix.router;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexRule implements Rule {
	private List<Pattern> patternList = new ArrayList<Pattern>();
	private String targetUrl;

	public RegexRule(List<String> regexList, String targetUrl) {
		this.targetUrl = targetUrl;
		for (String regex : regexList) {
			patternList.add(Pattern.compile(regex));
		}
	}

	public boolean match(String url) {
		for (Pattern pattern : patternList) {
			if (pattern.matcher(url).matches()) {
				return true;
			}
		}
		return false;
	}

	public String trans(String url) {
		return targetUrl + url;
	}
}
