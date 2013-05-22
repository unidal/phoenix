package com.dianping.maven.plugin.tools.vcs;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class CodeRetrieveServiceTest extends ComponentTestCase {
	private CodeRetrieverManager codeRetrieverManager;

	@Before
	public void before() throws Exception {
		codeRetrieverManager = lookup(CodeRetrieverManager.class);
	}

	@Test
	//if the test case fails, please change to the your own repository URL
	public void testSVNCodeRetriever() {
		String dir = "/tmp/test_svn";
		CodeRetrieveConfig config = new SVNCodeRetrieveConfig("http://192.168.8.45:81/svn/dianping/platform/data-analysis/trunk/log-system",
		      dir, System.out, -1);
		codeRetrieverManager.getCodeRetriever(config).retrieveCode();
		assertFalse(isDirectoryEmpty(dir));

	}

	@Test
	//if the test case fails, please change to the your own repository URL
	public void testGitCodeRetriever() {
		String dir = "/tmp/test_git";
		GitCodeRetrieveConfig config = new GitCodeRetrieveConfig("git@github.com:yixzhang/Taurus.git",
		      dir, System.out, "develop");
		codeRetrieverManager.getCodeRetriever(config).retrieveCode();
		assertFalse(isDirectoryEmpty(dir));
	}

	private boolean isDirectoryEmpty(String dir) {
		Collection<File> files = FileUtils.listFiles(new File(dir), null, true);
		return files.isEmpty();
	}
}
