package com.dianping.phoenix.session.view;

import com.dianping.phoenix.session.console.ConsolePage;
import org.unidal.web.mvc.Page;

public class NavigationBar {
   public Page[] getVisiblePages() {
      return new Page[] {
   
      ConsolePage.HOME

		};
   }
}
