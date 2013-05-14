package com.dianping.maven.plugin.tools.vcs;

import java.io.File;
import java.io.OutputStream;

import org.eclipse.jgit.util.FileUtils;
import org.eclipse.jgit.util.StringUtils;

public abstract class CodeRetrieveConfig {

	private String repoUrl;
	private String localPath;
	private String username;
	private String password;
	private OutputStream logOutput;

	public CodeRetrieveConfig(){}
	
	public CodeRetrieveConfig(String repoUrl, String localPath,
			String username, String password, OutputStream logOutput) {
		super();
		this.repoUrl = repoUrl;
		this.localPath = localPath;
		this.username = username;
		this.password = password;
		this.logOutput = logOutput;
	}

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
	String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	OutputStream getLogOutput() {
		return logOutput;
	}
	public void setLogOutput(OutputStream logOutput) {
		this.logOutput = logOutput;
	}

	void validate(){
		if(StringUtils.isEmptyOrNull(username)){
			throw new ConfigIncompleteException("username:" + username + " is invalid!");
		}
		if(StringUtils.isEmptyOrNull(password)){
			throw new ConfigIncompleteException("password:" + password + " is invalid!"); 
		}
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
