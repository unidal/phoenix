package com.dianping.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.WebRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.dianping.kernel.SortTool.SortElement;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;

public class Tomcat6WebappRegistry {

	private static final String INDEX = "_INDEX_";

	private Tomcat6WebappLoader loader;

	private StandardContext context;

	public void init(Tomcat6WebappLoader loader) {
		this.loader = loader;
		Container container = loader.getContainer();
		if (container instanceof StandardContext) {
			this.context = (StandardContext) container;
		}
	}

	public void registerWebXml() throws Exception {
		WebRuleSet webRuleSet = loader.getFieldValue(null, ContextConfig.class, "webRuleSet");
		
		File webXml = loader.getWebXml();
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

	public void reorderWebappElements() {
		reorderListener();
		reorderFilter();
	}
	
	private void reorderListener() {
		String[] listeners = this.context.findApplicationListeners();
		List<ListenerSortElement> listenerList = new ArrayList<ListenerSortElement>();
		this.loader.getLog().info("Re-match combinations before the listeners::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (String listener : listeners) {
			String rule = this.context.findParameter(listener);
			this.loader.getLog().info("listener::"+listener+"   rule::"+rule);
			listenerList.add(new ListenerSortElement(listener, rule));
		}
		this.loader.getLog().info("Re-match combinations before the listeners::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		SortTool sortTool = new SortTool();
		// Sort FilterMap
		List<SortElement> elementList = sortTool.sort(listenerList);
		// Get discard Listener
		List<String> discardListenerList = new ArrayList<String>();
		BeforeSort:for(String listener : listeners){
			AfterSort:for(SortElement se : elementList){
				if(listener.equals(se.getClassName())){
					continue BeforeSort;
				}
			}
			discardListenerList.add(listener);
		}
		
		//Remove discard Listener
		for(String dlisten : discardListenerList){
			this.loader.getLog().warn("----No match is found for the listener::"
					+dlisten
					+" and rule::"
					+this.context.findParameter(dlisten));
			this.context.removeApplicationListener(dlisten);
		}
		
		listeners = this.context.findApplicationListeners();
		this.loader.getLog().info("Re-match combinations after the listeners::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (int i = 0; i < elementList.size(); i++) {
			String listener = elementList.get(i).getClassName();
			String rule = this.context.findParameter(listener);
			this.loader.getLog().info("listener::"+listener+"   rule::"+rule);
			listeners[i] = listener;
		}
		this.loader.getLog().info("Re-match combinations after the listeners::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

	private void reorderFilter() {
		FilterMap[] filterMaps = this.context.findFilterMaps();
		List<FilterSortElement> filterMapList = new ArrayList<FilterSortElement>();
		this.loader.getLog().info("Re-match combinations before the filters::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (FilterMap fm : filterMaps) {
			FilterSortElement fse = new FilterSortElement(fm);
			filterMapList.add(fse);
			this.loader.getLog().info("filterName::"+fse.getName()+"   rule::"+fse.getRule());
		}
		this.loader.getLog().info("Re-match combinations before the filters::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		SortTool sortTool = new SortTool();
		// Sort FilterMap
		List<SortElement> elementList = sortTool.sort(filterMapList);
		// Delete does not meet the conditions of the Filter
		for (FilterSortElement fse : filterMapList) {
			if (!elementList.contains(fse)) {
				this.context.removeFilterMap(fse.getFilterMap());
				this.context.removeFilterDef(fse.getFilterDef());
				this.loader.getLog().warn("----No match is found for the filterName::"+fse.getName()+"   rule::"+fse.getRule());
			}
		}

		filterMaps = this.context.findFilterMaps();
		this.loader.getLog().info("Re-match combinations after the filters::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		// Set sorted result back
		for (int i = 0; i < filterMaps.length; i++) {
			FilterSortElement fse = (FilterSortElement) elementList.get(i);
			filterMaps[i] = fse.getFilterMap();
			this.loader.getLog().info("filterName::"+fse.getName()+"   rule::"+fse.getRule());
		}
		this.loader.getLog().info("Re-match combinations after the filters::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

	public class ListenerSortElement implements SortElement {
		private String className;

		private String rule;

		public ListenerSortElement(String className, String rule) {
			this.className = className;
			this.rule = rule;
		}

		@Override
		public String getRule() {
			return this.rule;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getClassName() {
			return this.className;
		}

	}

	public class FilterSortElement implements SortElement {

		private FilterMap filterMap;

		private FilterDef filterDef;

		public FilterSortElement(FilterMap filterMap) {
			this.filterMap = filterMap;
			this.filterDef = context.findFilterDef(this.filterMap.getFilterName());
		}

		@Override
		public String getRule() {
			return getInitParameter(INDEX);
		}

		@Override
		public String getName() {
			return this.filterMap.getFilterName();
		}

		@Override
		public String getClassName() {
			return this.filterDef.getFilterClass();
		}

		public FilterMap getFilterMap() {
			return this.filterMap;
		}

		public FilterDef getFilterDef() {
			return filterDef;
		}

		@SuppressWarnings("unchecked")
      private String getInitParameter(String paramName) {
			Map<String, String> map = this.filterDef.getParameterMap();

			if (map == null)
				return null;
			else
				return map.get(paramName);
		}

	}

}
