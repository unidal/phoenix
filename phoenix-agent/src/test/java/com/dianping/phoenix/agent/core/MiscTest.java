package com.dianping.phoenix.agent.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class MiscTest extends ComponentTestCase {

	@Test
	public void testSkipFileInputStreamOffset() throws IOException {
		File f = File.createTempFile("xxx", "xxx");
		f.deleteOnExit();

		FileOutputStream fout = new FileOutputStream(f);
		fout.write("a".getBytes());
		fout.close();

		FileInputStream fin = new FileInputStream(f);

		try {
			fin.skip(1000);
		} catch (Exception e) {
			Assert.fail("can't skip more than length");
		}
		
		Assert.assertEquals(-1, fin.read());

	}

}
