package com.dianping.maven.plugin.tools.vcs;

import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.ContainerHolder;

public class CodeRetrieverManager extends ContainerHolder {
	private Map<String, ICodeRetriever> m_retrievers = new HashMap<String, ICodeRetriever>();

	public ICodeRetriever getCodeRetriever(CodeRetrieveConfig config) {
		String type = config.getType();
		ICodeRetriever retriever = m_retrievers.get(type);

		if (retriever == null) {
			retriever = lookup(ICodeRetriever.class, type);
			retriever.setConfig(config);
			m_retrievers.put(type, retriever);
		}
		return retriever;
	}
}
