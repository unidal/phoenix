package com.dianping.phoenix.agent.core.shell;

import junit.framework.Assert;

import org.junit.Test;

public class CmdBuilderTest {

	@Test
	public void testSingleParam() {
		String got = CmdBuilder.start("/a/b/c.sh").add("aa", "1").get();
		Assert.assertEquals("/a/b/c.sh --aa=\"1\"", got);
	}
	
	@Test
	public void testMultiParam() {
		String got = CmdBuilder.start("/a/b/c.sh").add("aa", "1").add("bb", " 2 3 ").add("cc", "4 ").get();
		Assert.assertEquals("/a/b/c.sh --aa=\"1\" --bb=\" 2 3 \" --cc=\"4 \"", got);
	}
	
	@Test
	public void testEmptyParamName() {
		try {
			CmdBuilder.start("/a/b/c.sh").add(null, "1").get();
			Assert.fail();
		}catch (Exception e) {
		}
		
		try {
			CmdBuilder.start("/a/b/c.sh").add("", "1").get();
			Assert.fail();
		}catch (Exception e) {
		}
	}
	
	@Test
	public void testNullParamValue() {
		try {
			CmdBuilder.start("/a/b/c.sh").add("a", null).get();
			Assert.fail();
		}catch (Exception e) {
		}
	}
	
}
