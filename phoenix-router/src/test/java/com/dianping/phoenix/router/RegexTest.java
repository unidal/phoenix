package com.dianping.phoenix.router;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest {

	@Test
	public void testOccurance() {
		Assert.assertTrue(Pattern.matches("^/shop/(\\d+)/dish-(.+)", "/shop/123/dish-XX"));

		Assert.assertTrue(Pattern.matches("^/shop/(\\d+)(/{0,1})$", "/shop/123"));
		Assert.assertTrue(Pattern.matches("^/shop/(\\d+)(/{0,1})$", "/shop/123/"));
		Assert.assertFalse(Pattern.matches("^/shop/(\\d+)(/{0,1})$", "/shop/123//"));
	}

	@Test
	public void testOr() {
		Assert.assertTrue(Pattern.matches("^/shop/(\\d{1,})/(good|power)", "/shop/123/good"));
		Assert.assertTrue(Pattern.matches("^/shop/(\\d{1,})/(good|power)", "/shop/123/power"));
		Assert.assertFalse(Pattern.matches("^/shop/(\\d{1,})/(good|power)", "/shop/123/XX"));
	}

	@Test
	public void testNoStartEnd() {
		Assert.assertFalse(Pattern.matches("abc", "abc2"));
	}

	@Test
	public void testQuestionMarkEscape() {
		try {
			Pattern.matches("?", "?");
			Assert.fail();
		} catch (Exception e) {
		}
		Assert.assertTrue(Pattern.matches("\\?", "?"));
	}

}
