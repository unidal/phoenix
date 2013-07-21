package com.dianping.phoenix.utils;

import java.util.ArrayList;
import java.util.List;

import org.unidal.webres.json.JsonSerializer;

public class StringUtils {
	public static boolean isBlank(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static String getDefaultValueIfBlank(String str, String defaultValue) {
		return isBlank(str) ? defaultValue : str;
	}

	public static void main(String[] args) throws Exception {
		List<String> list = new ArrayList<String>();
		for (int idx = 0; idx < 10; idx++) {
			list.add(String.valueOf(idx));
		}
		System.out.println(JsonSerializer.getInstance().serialize(list));
	}
}
