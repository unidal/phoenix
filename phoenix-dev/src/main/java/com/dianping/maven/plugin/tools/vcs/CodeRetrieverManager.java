package com.dianping.maven.plugin.tools.vcs;

import org.unidal.lookup.ContainerHolder;

public class CodeRetrieverManager extends ContainerHolder {

	public ICodeRetriever getCodeRetriever(CodeRetrieveConfig config) {
		ICodeRetriever retriever = lookup(ICodeRetriever.class, config.getType());
		retriever.setConfig(config);
		return retriever;
	}
}
