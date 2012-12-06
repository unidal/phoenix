package com.dianping.phoenix.agent.core;

import java.lang.reflect.Field;




public class TestUtil {

	public static void replaceFieldByReflection(Object obj, String fieldName, Object newFieldValue) throws Exception {
		Field field = null;
		Class<?> clazz = obj.getClass();
		while(field == null && clazz != null) {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (Exception e) {
			}
			clazz = clazz.getSuperclass();
		}
		field.setAccessible(true);
		field.set(obj, newFieldValue);
	}
	
}
