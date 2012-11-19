package com.dianping.phoenix.build;

import java.io.File;

import org.unidal.web.jsp.AbstractTagLibrary;
import org.unidal.web.jsp.annotation.TaglibMeta;

@TaglibMeta(uri = "http://www.dianping.com/phoenix/console", shortName = "a", name = "app", description = "Application specific JSP tag library", //
tagFiles = { "layout.tag" })
public class ApplicationTagLibrary extends AbstractTagLibrary {
	public static void main(String[] args) {
		new ApplicationTagLibrary().generateTldFile(new File("."));
	}

	@Override
	protected boolean isWarProject() {
		return true;
	}
}
