package com.dianping.phoenix.dev.tools;

import java.io.OutputStream;


public class SVNCodeRetrieveConfig extends CodeRetrieveConfig{
	
	private long version;
	
	public SVNCodeRetrieveConfig() {
		super();
	}

	public SVNCodeRetrieveConfig(String repoUrl, String localPath,
			String username, String password, OutputStream logOutput, long version) {
		super(repoUrl, localPath, username, password, logOutput);
		this.version = version;
	}



	long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
