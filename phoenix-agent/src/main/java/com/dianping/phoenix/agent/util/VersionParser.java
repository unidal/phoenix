package com.dianping.phoenix.agent.util;

public class VersionParser {
	public String[] parse(String str) {
		String[] result = new String[2];
		int fromIndex = str.length() - 1;

		do {
			int pos = str.lastIndexOf('-', fromIndex);

			if (pos > 0) {
				char ch = str.charAt(pos + 1);

				if (Character.isDigit(ch)) {
					String right = str.substring(pos + 1);

					if (right.indexOf('.') > 0) { // for "a-1.1-1.jar"
						result[0] = str.substring(0, pos);
						result[1] = right;
						break;
					}
				}

				fromIndex = pos - 1;
			} else { // for cases: sqljdbc-4, json-20090211
				// try '-' again
				pos = str.indexOf('-');

				if (pos < 0) {
					result[0] = str;
					result[1] = "";
				} else if (pos + 1 < str.length() && Character.isDigit(str.charAt(pos + 1))) {
					result[0] = str.substring(0, pos);
					result[1] = str.substring(pos + 1);
				} else {
					result[0] = str;
					result[1] = "";
				}

				break;
			}
		} while (fromIndex > 0);

		return result;
	}
}