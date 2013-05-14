package com.dianping.maven.plugin.tools.vcs;

public class CodeRetrieveService {
	
	public static CodeRetrieveService instance = new CodeRetrieveService();
	
	private CodeRetrieveService(){}
	
	public static CodeRetrieveService getInstance(){
		return instance;
	}
	
	public void retrieveCode(CodeRetrieveConfig config){
		if(config instanceof GitCodeRetrieveConfig){
			GitCodeRetriever.getInstance().retrieveCode(config); 
		}else if (config instanceof SVNCodeRetrieveConfig){
			SVNCodeRetriever.getInstance().retrieveCode(config); 
		}
	}

}
