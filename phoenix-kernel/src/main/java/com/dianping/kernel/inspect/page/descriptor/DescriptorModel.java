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

	public DescriptorModel(StandardContext ctx) {
		m_ctx = ctx;
	}

	public List<Mapping<FilterDef, FilterMap>> getFilters() {
		List<Mapping<FilterDef, FilterMap>> filters = new ArrayList<Mapping<FilterDef, FilterMap>>();

		for (FilterMap map : m_ctx.findFilterMaps()) {
			String name = map.getFilterName();
			FilterDef def = m_ctx.findFilterDef(name);

			filters.add(new Mapping<FilterDef, FilterMap>(def, map));
		}

		return filters;
	}

	public List<Mapping<String, String>> getListeners() {
		List<Mapping<String, String>> listeners = new ArrayList<Mapping<String, String>>();

		for (String name : m_ctx.findApplicationListeners()) {
			String index = m_ctx.findParameter(name);

			listeners.add(new Mapping<String, String>(name, index));
		}

		return listeners;
	}

	public List<String> getWelcomeFiles() {
		List<String> files = new ArrayList<String>();

		for (String file : m_ctx.getWelcomeFiles()) {
			files.add(file);
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
			      new Mapping<Wrapper, Map<String, String>>(wrapper, initParameters)));
		}

		return servlets;
	}

	public static final class Mapping<S, T> {
		private S m_key;

		private T m_value;

		public Mapping(S key, T value) {
			m_key = key;
			m_value = value;
		}

		public S getKey() {
			return m_key;
		}

		public T getValue() {
			return m_value;
		}
	}
}
