package com.dianping.maven.plugin.tools.vcs;

public interface ICodeRetriever {
	
	public void setConfig(CodeRetrieveConfig config);
	
	public void retrieveCode();

}
