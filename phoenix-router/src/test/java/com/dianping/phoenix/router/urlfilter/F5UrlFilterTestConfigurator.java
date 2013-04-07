package com.dianping.phoenix.router.urlfilter;

import com.dianping.phoenix.router.BaseTestConfigurator;


public class F5UrlFilterTestConfigurator extends BaseTestConfigurator {

	@Override
	protected Class<?> getTestClass() {
		return F5UrlFilterTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new F5UrlFilterTestConfigurator());
	}

}
