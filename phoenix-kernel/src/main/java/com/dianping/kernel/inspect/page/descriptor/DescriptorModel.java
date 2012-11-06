package com.dianping.kernel.inspect.page.descriptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;

public class DescriptorModel {
	private StandardContext m_ctx;
	
	private Model m_model;
	
	public final static int FROM_CONTAINER = 1;
	public final static int FROM_APP = 2;
	public final static int FROM_KERNEL = 3;

	public DescriptorModel(StandardContext ctx) {
		m_ctx = ctx;
	}

	public List<Mapping<FilterDef, FilterMap>> getFilters() {
		List<Mapping<FilterDef, FilterMap>> filters = new ArrayList<Mapping<FilterDef, FilterMap>>();

		for (FilterMap map : m_ctx.findFilterMaps()) {
			String name = map.getFilterName();
			FilterDef def = m_ctx.findFilterDef(name);

			filters.add(new Mapping<FilterDef, FilterMap>(def, map,filterFromWhere(m_model,name)));
		}

		return filters;
	}

	public List<Mapping<String, String>> getListeners() {
		List<Mapping<String, String>> listeners = new ArrayList<Mapping<String, String>>();

		for (String name : m_ctx.findApplicationListeners()) {
			String index = m_ctx.findParameter(name);

			listeners.add(new Mapping<String, String>(name, index, listenerFromWhere(m_model,name)));
		}

		return listeners;
	}

	public List<Mapping<String,String>> getWelcomeFiles() {
		List<Mapping<String,String>> files = new ArrayList<Mapping<String,String>>();

		for (String file : m_ctx.getWelcomeFiles()) {
			files.add(new Mapping<String,String>(file,null,welcomeFromWhere(m_model,file)));
		}

		return files;
	}

	public List<Mapping<String, Mapping<Wrapper, Map<String, String>>>> getServlets() {
		List<Mapping<String, Mapping<Wrapper, Map<String, String>>>> servlets = new ArrayList<Mapping<String, Mapping<Wrapper, Map<String, String>>>>();

		for (String pattern : m_ctx.findServletMappings()) {
			String name = m_ctx.findServletMapping(pattern);
			Wrapper wrapper = (Wrapper) m_ctx.findChild(name);
			Map<String, String> initParameters = new LinkedHashMap<String, String>();

			for (String param : wrapper.findInitParameters()) {
				initParameters.put(param, wrapper.findInitParameter(param));
			}

			servlets.add(new Mapping<String, Mapping<Wrapper, Map<String, String>>>(pattern,
			      new Mapping<Wrapper, Map<String, String>>(wrapper, initParameters, servletFromWhere(m_model,name))
			      ,FROM_CONTAINER));
		}

		return servlets;
	}
	
	private int listenerFromWhere(Model model,String listenerClassName){
		if(model.getDefaultModel().containsListener(listenerClassName)){
			return FROM_CONTAINER;
		}else if(model.getAppModel().containsListener(listenerClassName)){
			return FROM_APP;
		}else if(model.getKernelModel().containsListener(listenerClassName)){
			return FROM_KERNEL;
		}
		return FROM_CONTAINER;
	}
	
	private int filterFromWhere(Model model,String name){
		if(model.getDefaultModel().containsFilter(name)){
			return FROM_CONTAINER;
		}else if(model.getAppModel().containsFilter(name)){
			return FROM_APP;
		}else if(model.getKernelModel().containsFilter(name)){
			return FROM_KERNEL;
		}
		return FROM_CONTAINER;
	}
	
	private int servletFromWhere(Model model,String name){
		if(model.getDefaultModel().containsServlet(name)){
			return FROM_CONTAINER;
		}else if(model.getAppModel().containsServlet(name)){
			return FROM_APP;
		}else if(model.getKernelModel().containsServlet(name)){
			return FROM_KERNEL;
		}
		return FROM_CONTAINER;
	}
	private int welcomeFromWhere(Model model,String file){
		if(model.getDefaultModel().containsWelcome(file)){
			return FROM_CONTAINER;
		}else if(model.getAppModel().containsWelcome(file)){
			return FROM_APP;
		}else if(model.getKernelModel().containsWelcome(file)){
			return FROM_KERNEL;
		}
		return FROM_CONTAINER;
	}
	
	boolean containsListener(String listenerClassName){
		String[] listeners = m_ctx.findApplicationListeners();
		for(String listener : listeners){
			if(listener.equals(listenerClassName)){
				return true;
			}
		}
		return false;
	}
	
	boolean containsWelcome(String file){
		
		String[] welcomeFiles = m_ctx.findWelcomeFiles();
		for(String welcomeFile : welcomeFiles){
			if(welcomeFile.equals(file)){
				return true;
			}
		}
		return false;
	}
	
	boolean containsFilter(String filterName){
		return m_ctx.findFilterDef(filterName) != null;
	}
	
	boolean containsServlet(String servletName){
		return m_ctx.findChild(servletName) != null;
	}

	public void setModel(Model model) {
		this.m_model = model;
	}

	public static final class Mapping<S, T> {
		private S m_key;

		private T m_value;
		
		private int m_fromWhere = FROM_CONTAINER;

		public Mapping(S key, T value, int fromWhere) {
			m_key = key;
			m_value = value;
			m_fromWhere = fromWhere;
		}

		public S getKey() {
			return m_key;
		}

		public T getValue() {
			return m_value;
		}

		public int getFromWhere() {
			return m_fromWhere;
		}
		
	}
}
