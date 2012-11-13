package com.dianping.kernel.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.directory.DirContext;

import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.ProxyDirContext;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.dianping.kernel.plugin.SortTool.SortElement;
import com.dianping.phoenix.bootstrap.AbstractCatalinaWebappLoader;
import com.dianping.phoenix.bootstrap.Constants;

public class CatalinaWebappPatcher implements WebappPatcher {
	private static final String INDEX = "_INDEX_";

	private AbstractCatalinaWebappLoader m_loader;

	private StandardContext m_context;

	private StandardContext m_proxyContext;

	public void applyKernelWebXml() throws Exception {
		File webXml = m_loader.getWebXml();
		InputSource source = new InputSource(new FileInputStream(webXml));
		StandardContext ctx = m_proxyContext;
		Digester digester = ContextConfig.createWebXmlDigester(ctx.getXmlNamespaceAware(), ctx.getXmlValidation());

		ctx.setReplaceWelcomeFiles(true);
		digester.setClassLoader(m_loader.getWebappClassLoader());
		digester.setUseContextClassLoader(false);
		digester.push(ctx);
		digester.setErrorHandler(new ContextErrorHandler());

		try {
			digester.parse(source);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when parsing %s!", webXml), e);
		} finally {
			digester.reset();
			source.getByteStream().close();
		}
	}

	public void finish() {
		m_loader.finish();
	}

	public void init(AbstractCatalinaWebappLoader loader) throws Exception {
		m_loader = loader;
		Container container = loader.getContainer();

		if (container instanceof StandardContext) {
			m_context = (StandardContext) container;
			m_proxyContext = loader.getStandardContext();
			LifecycleSupport lifecycle = m_loader.getFieldValue(m_context, ContainerBase.class, "lifecycle");
			m_loader.setFieldValue(lifecycle, "lifecycle", m_proxyContext);
		}
	}

	public void mergeWebResources() throws Exception {
		ProxyDirContext proxyDirContext = (ProxyDirContext) m_context.getResources();
		DirContext dirContext = proxyDirContext.getDirContext();
		FileDirContext kernelFileDirContext = new FileDirContext(dirContext.getEnvironment());
		kernelFileDirContext.setDocBase(m_loader.getKernelWarRoot().getAbsolutePath());
		CompositeDirContext kernelProxyDirContext = new CompositeDirContext(dirContext, kernelFileDirContext);

		m_loader.setFieldValue(proxyDirContext, "dirContext", kernelProxyDirContext);
	}

	protected void sortFilter() {
		FilterMap[] filterMaps = m_context.findFilterMaps();
		List<FilterSortElement> filterMapList = new ArrayList<FilterSortElement>();
		m_loader.getLog().info("Re-match combinations before the filters::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (FilterMap fm : filterMaps) {
			FilterSortElement fse = new FilterSortElement(fm);
			filterMapList.add(fse);
			m_loader.getLog().info("filterName::" + fse.getName() + "   rule::" + fse.getRule());
		}
		m_loader.getLog().info("Re-match combinations before the filters::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		SortTool sortTool = new SortTool();
		// Sort FilterMap
		List<SortElement> elementList = sortTool.sort(filterMapList);
		// Delete does not meet the conditions of the Filter
		for (FilterSortElement fse : filterMapList) {
			if (!elementList.contains(fse)) {
				m_context.removeFilterMap(fse.getFilterMap());
				m_context.removeFilterDef(fse.getFilterDef());
				m_loader.getLog().warn(
				      "----No match is found for the filterName::" + fse.getName() + "   rule::" + fse.getRule());
			}
		}

		filterMaps = m_context.findFilterMaps();
		m_loader.getLog().info("Re-match combinations after the filters::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		// Set sorted result back
		for (int i = 0; i < filterMaps.length; i++) {
			FilterSortElement fse = (FilterSortElement) elementList.get(i);
			filterMaps[i] = fse.getFilterMap();
			m_loader.getLog().info("filterName::" + fse.getName() + "   rule::" + fse.getRule());
		}
		
		m_loader.getLog().info("Re-match combinations after the filters::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}
	
	/**
	 * Check filter for kernel
	 */
	private void filterWebFilter(){
		List<FilterMap> fmList = new ArrayList<FilterMap>();
		FilterMap[] filterMaps = m_context.findFilterMaps();
		StandardContext kernelContext = (StandardContext)m_context.getServletContext().getAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_KERNEL);
		for (FilterMap fm : filterMaps) {
			FilterDef fd = kernelContext.findFilterDef(fm.getFilterName());
			if(fd != null){
				try {
					m_loader.getWebappClassLoader().loadClass(fd.getFilterClass());
				} catch (ClassNotFoundException e) {
					fmList.add(fm);
				}
			}
		}
		for(FilterMap filterMap : fmList){
			m_context.removeFilterDef(m_context.findFilterDef(filterMap.getFilterName()));
			m_context.removeFilterMap(filterMap);
			m_loader.getLog().warn("filterName::" + filterMap.getFilterName() + " is filtered for this App" );
		}
	}

	protected void sortListener() {
		String[] listeners = m_context.findApplicationListeners();
		List<ListenerSortElement> listenerList = new ArrayList<ListenerSortElement>();
		m_loader.getLog().info("Re-match combinations before the listeners::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (String listener : listeners) {
			String rule = m_context.findParameter(listener);
			m_loader.getLog().info("listener::" + listener + "   rule::" + rule);
			listenerList.add(new ListenerSortElement(listener, rule));
		}
		m_loader.getLog().info("Re-match combinations before the listeners::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		SortTool sortTool = new SortTool();
		// Sort FilterMap
		List<SortElement> elementList = sortTool.sort(listenerList);
		// Get discard Listener
		List<String> discardListenerList = new ArrayList<String>();
		BeforeSort: for (String listener : listeners) {
			for (SortElement se : elementList) {
				if (listener.equals(se.getClassName())) {
					continue BeforeSort;
				}
			}
			discardListenerList.add(listener);
		}

		// Remove discard Listener
		for (String dlisten : discardListenerList) {
			m_loader.getLog().warn(
			      "----No match is found for the listener::" + dlisten + " and rule::" + m_context.findParameter(dlisten));
			m_context.removeApplicationListener(dlisten);
		}

		listeners = m_context.findApplicationListeners();
		m_loader.getLog().info("Re-match combinations after the listeners::::start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		for (int i = 0; i < elementList.size(); i++) {
			String listener = elementList.get(i).getClassName();
			String rule = m_context.findParameter(listener);
			m_loader.getLog().info("listener::" + listener + "   rule::" + rule);
			listeners[i] = listener;
		}
		
		m_loader.getLog().info("Re-match combinations after the listeners::::end<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}
	
	public void filterWebListener(){
		//Check listener for kernel
		List<String> lisList = new ArrayList<String>();
		StandardContext kernelContext = (StandardContext)m_context.getServletContext().getAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_KERNEL);
		String[] listeners = m_context.findApplicationListeners();
		String[] kernelListeners = kernelContext.findApplicationListeners();
		for (String listener : listeners) {
			for(String kernelListener : kernelListeners){
				if(listener.equals(kernelListener)){
					try {
						m_loader.getWebappClassLoader().loadClass(listener);
					} catch (ClassNotFoundException e) {
						lisList.add(listener);
					}
				}
			}
		}
		for(String lis : lisList){
			m_context.removeApplicationListener(lis);
			m_loader.getLog().warn("listenerName::" + lis + " is filtered for this App" );
		}
	}
	
	public void filterWebServlet(){
		//Check listener for kernel
		List<String> servList = new ArrayList<String>();
		StandardContext kernelContext = (StandardContext)m_context.getServletContext().getAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_KERNEL);
		String[] patterns = m_context.findServletMappings();
		for (String pattern : patterns) {
			String name = kernelContext.findServletMapping(pattern);
			if(name != null){
				Wrapper wrapper = (Wrapper)m_context.findChild(name);
				try {
					m_loader.getWebappClassLoader().loadClass(wrapper.getServletClass());
				} catch (ClassNotFoundException e) {
					servList.add(pattern);
				}
			}
		}
		for(String serv : servList){
			m_context.removeServletMapping(serv);
			m_loader.getLog().warn("servletName::" + m_context.findServletMapping(serv) + " is filtered for this App" );
		}
	}

	public void sortWebXmlElements() {
		sortListener();
		sortFilter();
	}
	
	public void filterWebXmlElements() {
		filterWebFilter();
		filterWebListener();
		filterWebServlet();
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

	public class FilterSortElement implements SortElement {
		private FilterMap filterMap;

		private FilterDef filterDef;

		public FilterSortElement(FilterMap filterMap) {
			this.filterMap = filterMap;
			this.filterDef = m_context.findFilterDef(this.filterMap.getFilterName());
		}

		@Override
		public String getClassName() {
			return this.filterDef.getFilterClass();
		}

		public FilterDef getFilterDef() {
			return filterDef;
		}

		public FilterMap getFilterMap() {
			return this.filterMap;
		}

		@SuppressWarnings("unchecked")
		private String getInitParameter(String paramName) {
			Map<String, String> map = this.filterDef.getParameterMap();

			if (map == null)
				return null;
			else
				return map.get(paramName);
		}

		@Override
		public String getName() {
			return this.filterMap.getFilterName();
		}

		@Override
		public String getRule() {
			return getInitParameter(INDEX);
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
		public String getClassName() {
			return this.className;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getRule() {
			return this.rule;
		}
	}
}
