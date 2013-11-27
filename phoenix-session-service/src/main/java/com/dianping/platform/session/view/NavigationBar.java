package com.dianping.platform.session.view;

import com.dianping.platform.session.console.ConsolePage;
import org.unidal.web.mvc.Page;

public class NavigationBar {
   public Page[] getVisiblePages() {
      return new Page[] {
   
      ConsolePage.HOME

		};
   }
}
