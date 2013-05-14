package com.dianping.maven.plugin.tools.vcs;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.dianping.maven.plugin.tools.vcs.CodeRetrieveConfig;
import com.dianping.maven.plugin.tools.vcs.CodeRetrieveService;
import com.dianping.maven.plugin.tools.vcs.GitCodeRetrieveConfig;
import com.dianping.maven.plugin.tools.vcs.SVNCodeRetrieveConfig;


public class CodeRetrieveServiceTest {
	
	@Test
	//TODO  please input your own repository address, username, password 
	//and enable assertFalse
	public void testSVNCodeRetriever(){
		String dir = "/tmp/test_svn";
		CodeRetrieveConfig config = new SVNCodeRetrieveConfig(
				"", //"http://192.168.8.45:81/svn/dianping/platform/data-analysis/trunk/log-system", 
				dir, 
				"", 
				"", 
				System.out,
				-1);
		CodeRetrieveService.getInstance().retrieveCode(config);
		//assertFalse(isDirectoryEmpty(dir));
		
	}
	
	@Test
	//TODO  please input your own repository address, username, password 
	//and enable assertFalse
	public void testGitCodeRetriever(){
		String dir = "/tmp/test_git";
		GitCodeRetrieveConfig config = new GitCodeRetrieveConfig(
				"", //"https://github.com/yixzhang/Taurus.git",
				dir,
				"", 
				"",
				System.out,
				"develop");
		CodeRetrieveService.getInstance().retrieveCode(config);
		//assertFalse(isDirectoryEmpty(dir));
	}

	private boolean isDirectoryEmpty(String dir){
		Collection<File> files = FileUtils.listFiles(new File(dir), null, true);
		return files.isEmpty();
	}
}
