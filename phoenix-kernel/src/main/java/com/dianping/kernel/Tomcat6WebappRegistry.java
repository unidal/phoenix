package com.dianping.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterConfig;

import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.WebRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;

public class Tomcat6WebappRegistry implements LifecycleListener{
	
	private static final String location = "#location#";
	
	private Tomcat6WebappLoader loader;
	private StandardContext context;
	
	public void init(Tomcat6WebappLoader loader){
		this.loader = loader;
		Container container = loader.getContainer();
		if(container instanceof StandardContext){
			this.context = (StandardContext)container;
			this.context.addLifecycleListener(this);
		}
	}
	
	public void register() throws Exception {
		WebRuleSet webRuleSet = loader.getFieldValue(ContextConfig.class, "webRuleSet", null);
		File webXml = new File(loader.getKernelWarRoot(), "WEB-INF/web.xml");
		InputSource source = new InputSource(new FileInputStream(webXml));
		StandardContext ctx = (StandardContext) loader.getContainer();
		Digester digester = ContextConfig.createWebXmlDigester(ctx.getXmlNamespaceAware(), ctx.getXmlValidation());

		ctx.setReplaceWelcomeFiles(true);
		digester.setClassLoader(loader.getWebappClassLoader());
		digester.setUseContextClassLoader(false);
		digester.push(ctx);
		digester.setErrorHandler(new ContextErrorHandler());

		try {
			digester.parse(source);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when parsing %s!", webXml), e);
		} finally {
			webRuleSet.recycle();
			digester.reset();
			source.getByteStream().close();
		}
	}

	protected static class ContextErrorHandler implements ErrorHandler {
		public void error(SAXParseException exception) {
			exception.printStackTrace();
		}

		public void fatalError(SAXParseException exception) {
			exception.printStackTrace();
		}

		public void warning(SAXParseException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		 // Process the event that has occurred
        if (event.getType().equals(Lifecycle.START_EVENT)) {
        	try {
				register();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("StandardContext start event >>>>>>");
        } else if (event.getType().equals(StandardContext.BEFORE_START_EVENT)) {
            System.out.println("StandardContext before start event >>>>>>");
        } else if (event.getType().equals(StandardContext.AFTER_START_EVENT)) {
        	afterStart();
        	System.out.println("StandardContext after start event >>>>>>");
        } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
        	System.out.println("StandardContext stop event >>>>>>");
        } else if (event.getType().equals(Lifecycle.INIT_EVENT)) {
        	System.out.println("StandardContext init event >>>>>>");
        } else if (event.getType().equals(Lifecycle.DESTROY_EVENT)) {
        	System.out.println("StandardContext destory event >>>>>>");
        }
	}
	
	private void afterStart(){
		FilterMap[] filterMaps = this.context.findFilterMaps();
    	List<FilterMap> filterList = new ArrayList<FilterMap>();
    	for(FilterMap fm : filterMaps){
    		filterList.add(fm);
    	}
    	Collections.sort(filterList, new FilterComparator());
    	filterList.toArray(filterMaps);
	}
	
	private class FilterComparator implements Comparator<FilterMap>{
		@Override
		public int compare(FilterMap fm1, FilterMap fm2) {
			FilterDef fd1 = context.findFilterDef(fm1.getFilterName());
			FilterDef fd2 = context.findFilterDef(fm2.getFilterName());
			String loc1 = getInitParameter(fd1,location);
			String loc2 = getInitParameter(fd2,location);
			int loc1Int = 0;
			int loc2Int = 0;
			if(loc1 != null){
				loc1Int = Integer.parseInt(loc1);
			}
			if(loc2 != null){
				loc2Int = Integer.parseInt(loc2);
			}
			if(loc1Int > 0){
				loc1Int = Integer.MAX_VALUE - loc1Int;
			}
			if(loc2Int > 0){
				loc2Int = Integer.MAX_VALUE - loc2Int;
			}
			return loc2Int - loc1Int;
		}
	}
	
	private String getInitParameter(FilterDef filterDef,String name){
		Map map = filterDef.getParameterMap();
        if (map == null)
            return (null);
        else
            return ((String) map.get(name));
	}
	
}
