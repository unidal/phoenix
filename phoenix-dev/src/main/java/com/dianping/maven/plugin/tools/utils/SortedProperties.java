package com.dianping.maven.plugin.tools.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

@SuppressWarnings("serial")
public class SortedProperties extends Properties {
	
	/**
	 * Overrides, called by the store method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector keyList = new Vector();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList);
		return keyList.elements();
	}
}
