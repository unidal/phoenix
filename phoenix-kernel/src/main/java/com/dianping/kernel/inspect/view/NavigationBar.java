package com.dianping.kernel.inspect.view;

import com.dianping.kernel.inspect.InspectPage;
import com.site.web.mvc.Page;

public class NavigationBar {
	public Page[] getVisiblePages() {
		return new Page[] {

		InspectPage.HOME,

		InspectPage.DESCRIPTION,

		InspectPage.CLASSPATH

		};
	}
}
