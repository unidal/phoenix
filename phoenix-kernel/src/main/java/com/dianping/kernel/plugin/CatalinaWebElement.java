package com.dianping.kernel.plugin;

import org.apache.catalina.core.StandardContext;

public class CatalinaWebElement implements WebappElement{
	
	private StandardContext context;
	
	public CatalinaWebElement(StandardContext context){
		this.context = context;
	}

}
