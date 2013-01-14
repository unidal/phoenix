package com.dianping.service.build;

import java.util.ArrayList;
import java.util.List;

import com.dianping.service.editor.EditorModule;

import org.unidal.lookup.configuration.Component;
import org.unidal.web.configuration.AbstractWebComponentsConfigurator;

class WebComponentConfigurator extends AbstractWebComponentsConfigurator {
	@SuppressWarnings("unchecked")
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		defineModuleRegistry(all, EditorModule.class, EditorModule.class);

		return all;
	}
}
