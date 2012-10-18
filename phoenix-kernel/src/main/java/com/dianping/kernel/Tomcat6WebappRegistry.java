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
import com.dianping.phoenix.bootstrap.Constants;
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
		
		File webXml = null;
		String webXmlPath = System.getProperty(Constants.WEB_XML_PATH_KEY);
		if(webXmlPath != null){
			webXml = new File(webXmlPath);
		}else{
			webXml = new File(loader.getKernelWarRoot(), "WEB-INF/web.xml");
		}
		
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
		for (String listener : listeners) {
			listenerList.add(new ListenerSortElement(listener, this.context.findParameter(listener)));
		}
		SortTool sortTool = new SortTool(false);
		// Sort FilterMap
		List<SortElement> elementList = sortTool.sort(listenerList);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i] = elementList.get(i).getClassName();
		}
	}

	private void reorderFilter() {
		FilterMap[] filterMaps = this.context.findFilterMaps();
		List<FilterSortElement> filterMapList = new ArrayList<FilterSortElement>();
		for (FilterMap fm : filterMaps) {
			filterMapList.add(new FilterSortElement(fm));
		}

		SortTool sortTool = new SortTool();
		// Sort FilterMap
		List<SortElement> elementList = sortTool.sort(filterMapList);
		// Delete does not meet the conditions of the Filter
		for (FilterSortElement fse : filterMapList) {
			if (!elementList.contains(fse)) {
				this.context.removeFilterMap(fse.getFilterMap());
				this.context.removeFilterDef(fse.getFilterDef());
			}
		}

		filterMaps = this.context.findFilterMaps();
		// Set sorted result back
		for (int i = 0; i < filterMaps.length; i++) {
			filterMaps[i] = ((FilterSortElement) elementList.get(i)).getFilterMap();
		}
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
