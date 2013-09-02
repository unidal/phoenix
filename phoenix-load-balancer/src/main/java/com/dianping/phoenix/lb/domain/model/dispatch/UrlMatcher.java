package com.dianping.phoenix.lb.domain.model.dispatch;

public class UrlMatcher {

	public enum MatchType {
		RegexCaseSensitive("~"), RegexCaseInsensitive("~*"), Prefix(""), PrefixAndSkipRegex("^~"), Exact("=");

		private String nginxOperator;

		private MatchType(String nginxOperator) {
			this.nginxOperator = nginxOperator;
		}

		@Override
		public String toString() {
			return nginxOperator;
		}

	}

	private MatchType type;
	private String target;

	public UrlMatcher(MatchType type, String target) {
		this.type = type;
		this.target = target;
	}

	public MatchType getType() {
		return type;
	}

	public String getTarget() {
		return target;
	}

}
