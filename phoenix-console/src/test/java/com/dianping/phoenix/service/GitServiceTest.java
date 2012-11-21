package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class GitServiceTest extends ComponentTestCase {
	
	private static final String REFS_TAGS = "refs/tags/";
	
	@Test
	public void test() throws Exception {

		GitService git = lookup(GitService.class);

		File gitDir = git.getWorkingDir();
		String tag = "mock-1.0" + System.currentTimeMillis();

		git.setup();
		git.pull();

		git.clearWorkingDir();
		
		downloadAndExtractTo(tag, gitDir);

		ObjectId objId = git.commit(tag, "mock description");
		git.push();

		Collection<Ref> refs = git.lsRemote();
		boolean tagExist = false;
		boolean commitSuccess = false;
		if (refs != null) {
			for (Ref ref : refs) {
				if (ref.getName().equals(REFS_TAGS+tag)) {
					tagExist = true;
				}
				if (ref.getObjectId().getName().equals(objId.getName())) {
					commitSuccess = true;
				}
			}
		}
		Assert.assertTrue(tagExist);
		Assert.assertTrue(commitSuccess);

		git.removeTag(tag);

		refs = git.lsRemote();
		tagExist = false;
		if (refs != null) {
			for (Ref ref : refs) {
				if (ref.getName().equals(REFS_TAGS+tag)) {
					tagExist = true;
				}
			}
		}
		Assert.assertFalse(tagExist);
		
		

	}
	
	public void downloadAndExtractTo(String version, File target) throws IOException {
		File workingDir = new File("target/git");
		File newFile = new File(workingDir, String.valueOf(System.currentTimeMillis()));

		newFile.createNewFile();
	}

}
