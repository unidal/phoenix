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

public class Tomcat6WebappRegistry{
	
	private static final String INDEX = "_INDEX_";
	
	private Tomcat6WebappLoader loader;
	private StandardContext context;
	
	public void init(Tomcat6WebappLoader loader){
		this.loader = loader;
		Container container = loader.getContainer();
		if(container instanceof StandardContext){
			this.context = (StandardContext)container;
		}
	}
	
	public void registerWebXml() throws Exception {
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

	
	public void reorderWebappElements(){
		FilterMap[] filterMaps = this.context.findFilterMaps();
    	List<FilterSortElement> filterMapList = new ArrayList<FilterSortElement>();
    	for(FilterMap fm : filterMaps){
    		filterMapList.add(new FilterSortElement(fm));
    	}
    	
    	SortTool sortTool = new SortTool();
    	//Sort FilterMap
    	List<SortElement> elementList = sortTool.sort(filterMapList);
    	//Delete does not meet the conditions of the Filter 
    	for(FilterSortElement fse : filterMapList){
    		if(!elementList.contains(fse)){
    			this.context.removeFilterMap(fse.getFilterMap());
    			this.context.removeFilterDef(fse.getFilterDef());
    		}
    	}
    	
    	filterMaps = this.context.findFilterMaps();
    	//Set sorted result back
    	for(int i=0;i<filterMaps.length;i++){
    		filterMaps[i] = ((FilterSortElement)elementList.get(i)).getFilterMap();
    	}
	}
	
	public class FilterSortElement implements SortElement{

		private FilterMap filterMap;
		private FilterDef filterDef;
		
		public FilterSortElement(FilterMap filterMap){
			this.filterMap = filterMap;
			this.filterDef = context.findFilterDef(this.filterMap.getFilterName());
		}
		@Override
		public String getRule() {
			return getInitParameter(INDEX);
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return this.filterMap.getFilterName();
		}

		@Override
		public String getClassName() {
			return this.filterDef.getFilterClass();
		}
		
		public FilterMap getFilterMap(){
			return this.filterMap;
		}
		
		public FilterDef getFilterDef() {
			return filterDef;
		}
		private String getInitParameter(String paramName){
			
			Map map = this.filterDef.getParameterMap();
	        if (map == null)
	            return (null);
	        else
	            return ((String) map.get(paramName));
		}
		
	}
	
	
	
	
}
