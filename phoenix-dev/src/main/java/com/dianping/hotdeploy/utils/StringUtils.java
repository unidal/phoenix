package com.dianping.hotdeploy.utils;


public class StringUtils {
	public static boolean isBlank(String str) {
		if (str != null && str.trim().length() > 0) {
			return false;
		}
		return true;
	}
}
