package com.dianping.phoenix.dev.core.tools.vcs;

import java.io.OutputStream;

import org.eclipse.jgit.util.StringUtils;

public class GitCodeRetrieveConfig extends CodeRetrieveConfig{
	
	private String branchName;
	
	public GitCodeRetrieveConfig() {
		super();
	}

	public GitCodeRetrieveConfig(String repoUrl, String localPath, OutputStream logOutput, String branchName) {
		super(repoUrl, localPath, logOutput);
		this.branchName = branchName;
	}


	String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	@Override
	public void validate(){
		super.validate();
		if(StringUtils.isEmptyOrNull(branchName)){
			throw new ConfigIncompleteException("branchName:" + branchName + " is invalid!");  
		}
	}

	@Override
   public String getType() {
	   return CodeRetrieverContext.GIT;
   }
}
