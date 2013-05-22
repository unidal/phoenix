package com.dianping.maven.plugin.tools.vcs;

import java.io.OutputStream;


public class SVNCodeRetrieveConfig extends CodeRetrieveConfig{
	
	private long version;
	
	public SVNCodeRetrieveConfig() {
		super();
	}

	public SVNCodeRetrieveConfig(String repoUrl, String localPath,OutputStream logOutput, long version) {
		super(repoUrl, localPath, logOutput);
		this.version = version;
	}



	long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
   public String getType() {
	   return CodeRetrieverContext.SVN;
   }
}
