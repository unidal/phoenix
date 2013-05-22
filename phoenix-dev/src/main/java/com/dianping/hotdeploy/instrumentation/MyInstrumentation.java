package com.dianping.hotdeploy.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class MyInstrumentation implements ClassFileTransformer {

	public static void premain(String arg, final Instrumentation ins) {
		System.out.println(ins);
		try {
			Class<?> clazz = Class.forName("com.dianping.hotdeploy.server.InstrumentationHolder");
			clazz.getDeclaredMethod("setInstrumentation", Instrumentation.class).invoke(clazz, ins);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		return classfileBuffer;
	}
}
