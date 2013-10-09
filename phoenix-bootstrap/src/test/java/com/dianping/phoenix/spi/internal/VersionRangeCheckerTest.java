package com.dianping.phoenix.spi.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionRangeCheckerTest {
	@Test
	public void test() {
		VersionRangeChecker checker = new VersionRangeChecker();

		assertTrue(checker.matches("1.3", "1.3"));
		assertFalse(checker.matches("1.3.2", "1.3"));
		assertFalse(checker.matches("[1.3.2]", "1.3.2"));

		assertTrue(checker.matches("[1.0, 2]", "1.3"));
		assertTrue(checker.matches("[1.0, 2]", "1.0"));
		assertFalse(checker.matches("[1.0, 2]", "0.9"));
		assertTrue(checker.matches("[1.0, 2]", "2"));
		assertFalse(checker.matches("[1.0, 2]", "2.1"));
		assertTrue(checker.matches("[1.0", "1.3"));
		assertTrue(checker.matches("[1.0", "1.0"));
		assertFalse(checker.matches("[1.0", "0.9"));
		assertTrue(checker.matches("2.0]", "1.3"));
		assertTrue(checker.matches("2.0]", "2.0"));
		assertFalse(checker.matches("2.0]", "2.1"));

		assertFalse(checker.matches("(1.0, 2)", "2"));
		assertTrue(checker.matches("(1.0, 2)", "1.3"));
		assertFalse(checker.matches("(1.0, 2)", "1.0"));
		assertTrue(checker.matches("(1.0", "1.3"));
		assertFalse(checker.matches("(1.0", "1.0"));
		assertFalse(checker.matches("2.0)", "2.0"));
		assertTrue(checker.matches("2.0)", "1.3"));
	}

}
