package com.dianping.phoenix.spi.internal;

import java.util.Comparator;
import java.util.StringTokenizer;

public class VersionComparator implements Comparator<String> {
	@Override
	public int compare(String v1, String v2) {
		if (v1 == null || v2 == null) {
			return 0; // not comparable
		}

		StringTokenizer t1 = new StringTokenizer(v1, ".-", true);
		StringTokenizer t2 = new StringTokenizer(v2, ".-", true);
		boolean patch1 = false;
		boolean patch2 = false;

		while (true) {
			String p1 = t1.hasMoreTokens() ? t1.nextToken() : null;
			String p2 = t2.hasMoreTokens() ? t2.nextToken() : null;

			if (p1 != null && p2 != null) {
				if (patch1 || patch2) {
					if (patch1 == patch2) {
						// SNAPSHOT is always lowest version number
						if (p1.equals("SNAPSHOT") && !p2.equals("SNAPSHOT")) {
							return -1;
						}

						if (p2.equals("SNAPSHOT") && !p1.equals("SNAPSHOT")) {
							return 1;
						}

						return p1.compareToIgnoreCase(p2);
					} else {
						return patch1 ? -1 : 1;
					}
				} else {
					if (!p1.equals(p2)) {
						try {
							int i1 = Integer.parseInt(p1);
							int i2 = Integer.parseInt(p2);

							return i1 - i2;
						} catch (NumberFormatException e) {
							return p1.compareTo(p2);
						}
					} else {
						String n1 = t1.hasMoreTokens() ? t1.nextToken() : null;
						String n2 = t2.hasMoreTokens() ? t2.nextToken() : null;

						if (n1 != null && n1.equals("-")) {
							patch1 = true;
						}

						if (n2 != null && n2.equals("-")) {
							patch2 = true;
						}
					}
				}
			} else if (p1 == null && p2 == null) {
				return 0;
			} else if (p1 == null) {
				return patch2 ? 1 : -1;
			} else if (p2 == null) {
				return patch1 ? -1 : 1;
			}
		}
	}
}