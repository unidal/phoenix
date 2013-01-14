package com.dianping.service.view;

import com.dianping.service.editor.EditorPage;
import org.unidal.web.mvc.Page;

public class NavigationBar {
   public Page[] getVisiblePages() {
      return new Page[] {
   
      EditorPage.HOME

		};
   }
}
