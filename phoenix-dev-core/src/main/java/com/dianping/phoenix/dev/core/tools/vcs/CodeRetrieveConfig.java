package com.dianping.phoenix.dev.core.tools.vcs;

import java.io.File;
import java.io.OutputStream;

import org.eclipse.jgit.util.FileUtils;
import org.eclipse.jgit.util.StringUtils;

public abstract class CodeRetrieveConfig {
	private String repoUrl;
	private String localPath;
	private OutputStream logOutput;

	public CodeRetrieveConfig(){}
	
	public CodeRetrieveConfig(String repoUrl, String localPath,OutputStream logOutput) {
		super();
		this.repoUrl = repoUrl;
		this.localPath = localPath;
		this.logOutput = logOutput;
	}

	public abstract String getType();
	
	String getRepoUrl() {
		return repoUrl;
	}
	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}
	String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	OutputStream getLogOutput() {
		return logOutput;
	}
	public void setLogOutput(OutputStream logOutput) {
		this.logOutput = logOutput;
	}

	void validate(){
		if(StringUtils.isEmptyOrNull(repoUrl)){
			throw new ConfigIncompleteException("repoUrl:" + repoUrl + " is invalid!"); 
		}
		if(StringUtils.isEmptyOrNull(localPath)){
			throw new ConfigIncompleteException("localPath:" + localPath + " is invalid!"); 
		}
		if(logOutput == null){
			throw new ConfigIncompleteException("logOutputStream is empty!");
		}
		//delete directory if exists
		try{
			File localPathDir = new File(localPath);
			if(localPathDir.exists()){
				FileUtils.delete(localPathDir, FileUtils.RECURSIVE);
			}
		}catch(Exception e){
			throw new ConfigIncompleteException("localPath:"+localPath + "can not be deleted", e);
		}
	}
}
