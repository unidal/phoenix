package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class ServerXmlUtilTest {

	private File serverXmlFile;
	private String loaderClass = "phoenix-loader";
	private File kernelDocBase = new File("phoenix-kernel-docBase");

	@Before
	public void before() throws Exception {
		serverXmlFile = File.createTempFile("phoenix", "xml");
		serverXmlFile.deleteOnExit();
		InputStream in = this.getClass().getResourceAsStream("server.xml");
		OutputStream out = new FileOutputStream(serverXmlFile);
		IOUtils.copy(in, out);
		in.close();
	}

	@Test
	public void testIsDocBaseMatch() {
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/a/b"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/a/b/"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b/", "/a/b"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b/", "/a/b/"));

		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/b"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/b/"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b/", "/b"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b/", "/b/"));

		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b//", "/b/"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b//", "/b"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/b//"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/b//"));

		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b\\", "/b"));
		Assert.assertTrue(ServerXmlUtil.isDocBaseMatch("/a/b", "/b\\\\"));

		Assert.assertFalse(ServerXmlUtil.isDocBaseMatch("/a/b", "/a"));
		Assert.assertFalse(ServerXmlUtil.isDocBaseMatch("/a/b", "/a/"));

		Assert.assertFalse(ServerXmlUtil.isDocBaseMatch("/a/b2", "b"));
		Assert.assertFalse(ServerXmlUtil.isDocBaseMatch("/a/b2", "/b"));
		Assert.assertFalse(ServerXmlUtil.isDocBaseMatch("/a/b2", "/a/b"));
	}

	@Test
	public void testInjectPhoenixLoader() throws Exception {
		ServerXmlUtil.attachPhoenixContextLoader(serverXmlFile, "sample-app/current", loaderClass, kernelDocBase);
		String content = IOUtils.toString(new FileInputStream(serverXmlFile));

		// first <Loader> exists
		int fstLoaderIdx = content.indexOf("<Loader");
		Assert.assertTrue(fstLoaderIdx > 0);
		Assert.assertTrue(content.indexOf(String.format("className=\"%s\"", loaderClass), fstLoaderIdx) > 0);
		Assert.assertTrue(content.indexOf(String.format("kernelDocBase=\"%s\"", kernelDocBase.getAbsolutePath()),
				fstLoaderIdx) > 0);

		// no second <Loader>
		int sndLoaderIdx = content.indexOf("<Loader", fstLoaderIdx + 1);
		Assert.assertTrue(sndLoaderIdx < 0);

		ServerXmlUtil.attachPhoenixContextLoader(serverXmlFile, "sample-app2/current", loaderClass, kernelDocBase);
		content = IOUtils.toString(new FileInputStream(serverXmlFile));

		// first <Loader> exists
		fstLoaderIdx = content.indexOf("<Loader");
		Assert.assertTrue(fstLoaderIdx > 0);
		Assert.assertTrue(content.indexOf(String.format("className=\"%s\"", loaderClass), fstLoaderIdx) > 0);
		Assert.assertTrue(content.indexOf(String.format("kernelDocBase=\"%s\"", kernelDocBase.getAbsolutePath()),
				fstLoaderIdx) > 0);

		// second <Loader> exists
		sndLoaderIdx = content.indexOf("<Loader", fstLoaderIdx + 1);
		Assert.assertTrue(sndLoaderIdx > 0);
		Assert.assertTrue(content.indexOf(String.format("className=\"%s\"", loaderClass), sndLoaderIdx) > 0);
		Assert.assertTrue(content.indexOf(String.format("kernelDocBase=\"%s\"", kernelDocBase.getAbsolutePath()),
				sndLoaderIdx) > 0);
	}

	@Test
	public void testInjectPhoenixLoaderNotFound() throws Exception {
		ServerXmlUtil.attachPhoenixContextLoader(serverXmlFile, "sample-app3/current", loaderClass, kernelDocBase);
		String content = IOUtils.toString(new FileInputStream(serverXmlFile));
		Assert.assertTrue(content.indexOf("<Loader") < 0);
	}

	@Test
	public void testRemovePhoenixLoader() throws Exception {
		String docBasePattern = "sample-app/current";
		String docBasePattern2 = "sample-app2/current";
		ServerXmlUtil.attachPhoenixContextLoader(serverXmlFile, docBasePattern, loaderClass, kernelDocBase);
		ServerXmlUtil.attachPhoenixContextLoader(serverXmlFile, docBasePattern2, loaderClass, kernelDocBase);
		ServerXmlUtil.detachPhoenixContextLoader(serverXmlFile, docBasePattern2);

		String content = IOUtils.toString(new FileInputStream(serverXmlFile));

		// first <Loader> exists
		int fstLoaderIdx = content.indexOf("<Loader");
		Assert.assertTrue(fstLoaderIdx > 0);
		Assert.assertTrue(content.indexOf(String.format("className=\"%s\"", loaderClass), fstLoaderIdx) > 0);
		Assert.assertTrue(content.indexOf(String.format("kernelDocBase=\"%s\"", kernelDocBase.getAbsolutePath()),
				fstLoaderIdx) > 0);

		// no second <Loader>
		int sndLoaderIdx = content.indexOf("<Loader", fstLoaderIdx + 1);
		Assert.assertTrue(sndLoaderIdx < 0);
	}
	
	@Test
	public void testRemovePhoenixLoaderNotFound() throws Exception {
		String docBasePattern = "sample-app/current";
		String docBasePatternNotFound = "sample-app3/current";
		
		ServerXmlUtil.attachPhoenixContextLoader(serverXmlFile, docBasePattern, loaderClass, kernelDocBase);
		ServerXmlUtil.detachPhoenixContextLoader(serverXmlFile, docBasePatternNotFound);

		String content = IOUtils.toString(new FileInputStream(serverXmlFile));
		
		Assert.assertTrue(content.indexOf("<Loader") > 0);
	}
}
