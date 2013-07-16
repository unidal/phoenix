package com.dianping.phoenix.router.hotdeploy.classloader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mortbay.jetty.webapp.WebAppContext;

public class PhoenixClassLoaderTest {

	@Test
	public void test() throws Exception {
		List<URL> list = new ArrayList<URL>();
		list.add(new File("/Users/song/workspace/phoenix/httptest2-1.1.2-snapshot.jar").toURI().toURL());
		list.add(new File("/Users/song/workspace/phoenix/httptest-1.1.1.jar").toURI().toURL());
		list.add(new File("/Users/song/workspace/phoenix/httpclient-1.1.1.jar").toURI().toURL());
		list.add(new File("/Users/song/workspace/phoenix/helloworld-1.1.1.jar").toURI().toURL());
		list.add(new File("/Users/song/workspace/phoenix/httpcore-1.1.2.jar").toURI().toURL());
		list.add(new File("/Users/song/workspace/phoenix/jstl-1.1.1.jar").toURI().toURL());
		list.add(new File("/Users/song/workspace/phoenix/httpcore-1.1.1.jar").toURI().toURL());

		List<String> sequence = new ArrayList<String>();
		sequence.add("httpcore");
		sequence.add("httpclient");
		sequence.add("httptest");
		sequence.add("jstl");
		sequence.add("httptest2");

		List<URL> expectList = new ArrayList<URL>();
		expectList.add(new File("/Users/song/workspace/phoenix/httpcore-1.1.2.jar").toURI().toURL());
		expectList.add(new File("/Users/song/workspace/phoenix/httpcore-1.1.1.jar").toURI().toURL());
		expectList.add(new File("/Users/song/workspace/phoenix/httpclient-1.1.1.jar").toURI().toURL());
		expectList.add(new File("/Users/song/workspace/phoenix/helloworld-1.1.1.jar").toURI().toURL());
		expectList.add(new File("/Users/song/workspace/phoenix/httptest-1.1.1.jar").toURI().toURL());
		expectList.add(new File("/Users/song/workspace/phoenix/jstl-1.1.1.jar").toURI().toURL());
		expectList.add(new File("/Users/song/workspace/phoenix/httptest2-1.1.2-snapshot.jar").toURI().toURL());

		PhoenixClassLoader cl = new PhoenixClassLoader(new File("/Users/song/workspace/phoenix/phoenix-console"),//
				5000, new WebAppContext(), this.getClass().getClassLoader());
		System.out.println(list);
		cl.sortWithSequence(list, sequence);
		System.out.println(list);
		for (int idx = 0; idx < list.size(); idx++) {
			Assert.assertEquals(expectList.get(idx).getFile(), list.get(idx).getFile());
		}
	}
}
