package com.dianping.phoenix.router.urlfilter;

import com.dianping.phoenix.router.BaseTestConfigurator;

public class UrlRewriteFilterConfigurator extends BaseTestConfigurator {

	@Override
	protected Class<?> getTestClass() {
		return UrlRewriteFilterTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new UrlRewriteFilterConfigurator());
	}
	
}
