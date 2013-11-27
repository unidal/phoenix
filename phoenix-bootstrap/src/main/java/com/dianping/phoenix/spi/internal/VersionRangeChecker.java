package com.dianping.phoenix.spi.internal;

public class VersionRangeChecker {
	private VersionComparator versionComparator = new VersionComparator();

	public boolean matches(String range, String version) {
		if (range == null || range.length() == 0 || version == null || version.length() == 0) {
			return false;
		}

		char leftBracketChar = range.charAt(0);
		char rightBracketChar = range.charAt(range.length() - 1);

		boolean leftBracket = isLeftBracket(leftBracketChar);
		boolean rightBracket = isRightBracket(rightBracketChar);

		// *
		if ("*".equals(range)) {
			return true;
		}

		// 1.0.snapshot
		if (!leftBracket && !rightBracket && versionComparator.compare(range, version) == 0) {
			return true;
		}

		// [1.0.snapshot, 2.1)
		if (leftBracket && rightBracket) {
			int commaIndex = range.indexOf(',');
			if (commaIndex > 0 && isLeftMatch(leftBracketChar, range.substring(1, commaIndex), version)
					&& isRightMatch(rightBracketChar, range.substring(commaIndex + 1, range.length() - 1), version)) {
				return true;
			}
		}

		// [1.0.snapshot
		if (leftBracket && !rightBracket && isLeftMatch(leftBracketChar, range.substring(1), version)) {
			return true;
		}

		// 1.0.snapshot)
		if (!leftBracket && rightBracket
				&& isRightMatch(rightBracketChar, range.substring(0, range.length() - 1), version)) {
			return true;
		}

		return false;
	}

	private boolean isLeftMatch(char leftBracketChar, String leftVersion, String version) {
		switch (leftBracketChar) {
			case '[' :
				return versionComparator.compare(leftVersion.trim(), version.trim()) <= 0;
			case '(' :
				return versionComparator.compare(leftVersion.trim(), version.trim()) < 0;
			default :
				return false;
		}
	}

	private boolean isRightMatch(char rightBracketChar, String rightVersion, String version) {
		switch (rightBracketChar) {
			case ']' :
				return versionComparator.compare(rightVersion.trim(), version.trim()) >= 0;
			case ')' :
				return versionComparator.compare(rightVersion.trim(), version.trim()) > 0;
			default :
				return false;
		}
	}

	private boolean isLeftBracket(char ch) {
		return ch == '[' || ch == '(';
	}

	private boolean isRightBracket(char ch) {
		return ch == ']' || ch == ')';
	}
}
