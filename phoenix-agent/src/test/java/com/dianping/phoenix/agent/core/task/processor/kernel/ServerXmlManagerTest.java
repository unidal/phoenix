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
import org.unidal.lookup.ComponentTestCase;

public class ServerXmlManagerTest extends ComponentTestCase {
	private File serverXmlFile;
	private String loaderClass = "phoenix-loader";
	private File kernelDocBase = new File("phoenix-kernel-docBase");
	private ServerXmlManager m_serverXmlManager;

	@Before
	public void before() throws Exception {
		serverXmlFile = File.createTempFile("phoenix", "xml");
		serverXmlFile.deleteOnExit();
		InputStream in = this.getClass().getResourceAsStream("server.xml");
		OutputStream out = new FileOutputStream(serverXmlFile);
		IOUtils.copy(in, out);
		in.close();
	}
	
	@Before
	public void init() throws Exception {
		m_serverXmlManager = lookup(ServerXmlManager.class);
	}
	
	@Test
	public void testIsDocBaseMatch() throws Exception {
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/a/b"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/a/b/"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b/", "/a/b"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b/", "/a/b/"));

		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/b"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/b/"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b/", "/b"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b/", "/b/"));

		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b//", "/b/"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b//", "/b"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/b//"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/b//"));

		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b\\", "/b"));
		Assert.assertTrue(m_serverXmlManager.isDocBaseMatch("/a/b", "/b\\\\"));

		Assert.assertFalse(m_serverXmlManager.isDocBaseMatch("/a/b", "/a"));
		Assert.assertFalse(m_serverXmlManager.isDocBaseMatch("/a/b", "/a/"));

		Assert.assertFalse(m_serverXmlManager.isDocBaseMatch("/a/b2", "b"));
		Assert.assertFalse(m_serverXmlManager.isDocBaseMatch("/a/b2", "/b"));
		Assert.assertFalse(m_serverXmlManager.isDocBaseMatch("/a/b2", "/a/b"));
	}

	@Test
	public void testInjectPhoenixLoader() throws Exception {
		m_serverXmlManager.attachPhoenixContextLoader(serverXmlFile, "sample-app/current", loaderClass, kernelDocBase);
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

		m_serverXmlManager.attachPhoenixContextLoader(serverXmlFile, "sample-app2/current", loaderClass, kernelDocBase);
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
		m_serverXmlManager.attachPhoenixContextLoader(serverXmlFile, "sample-app3/current", loaderClass, kernelDocBase);
		String content = IOUtils.toString(new FileInputStream(serverXmlFile));
		Assert.assertTrue(content.indexOf("<Loader") < 0);
	}

	@Test
	public void testRemovePhoenixLoader() throws Exception {
		String docBasePattern = "sample-app/current";
		String docBasePattern2 = "sample-app2/current";
		m_serverXmlManager.attachPhoenixContextLoader(serverXmlFile, docBasePattern, loaderClass, kernelDocBase);
		m_serverXmlManager.attachPhoenixContextLoader(serverXmlFile, docBasePattern2, loaderClass, kernelDocBase);
		m_serverXmlManager.detachPhoenixContextLoader(serverXmlFile, docBasePattern2);

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
		
		m_serverXmlManager.attachPhoenixContextLoader(serverXmlFile, docBasePattern, loaderClass, kernelDocBase);
		m_serverXmlManager.detachPhoenixContextLoader(serverXmlFile, docBasePatternNotFound);

		String content = IOUtils.toString(new FileInputStream(serverXmlFile));
		
		Assert.assertTrue(content.indexOf("<Loader") > 0);
	}
}
