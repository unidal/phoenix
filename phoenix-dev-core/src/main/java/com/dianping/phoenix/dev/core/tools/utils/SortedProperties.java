package com.dianping.phoenix.dev.core.tools.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

@SuppressWarnings("serial")
public class SortedProperties extends Properties {

	/**
	 * Overrides, called by the store() method.
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

	/**
	 * Overrides, called by the storeToXML() method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<Object> keySet() {
		Set origKeySet = super.keySet();
		ArrayList<String> keyList = new ArrayList<String>(origKeySet);
		Collections.sort(keyList);
		return new LinkedHashSet<Object>(keyList);
	}

}
