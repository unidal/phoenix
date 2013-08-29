package com.dianping.platform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class CookiesReplaceInstrumentation implements ClassFileTransformer {

	public static void premain(String arg, Instrumentation ins) {
		ins.addTransformer(new CookiesReplaceInstrumentation());
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if ("org/apache/tomcat/util/http/Cookies".equals(className)) {
			String jbossCookiesClassFile = "JbossCookies.clazz";
			InputStream in = loader.getResourceAsStream(jbossCookiesClassFile);
			if (in != null) {
				System.out.println("Found " + jbossCookiesClassFile);
				try {
					return toByteArray(in);
				} catch (IOException e) {
					System.out.println("Error reading " + jbossCookiesClassFile);
				}
			}
		}
		
		if ("org/apache/tomcat/util/http/ServerCookie".equals(className)) {
			String jbossServerCookieClassFile = "JbossServerCookie.clazz";
			InputStream in = loader.getResourceAsStream(jbossServerCookieClassFile);
			if (in != null) {
				System.out.println("Found " + jbossServerCookieClassFile);
				try {
					return toByteArray(in);
				} catch (IOException e) {
					System.out.println("Error reading " + jbossServerCookieClassFile);
				}
			}
		}
		
		return classfileBuffer;
	}

	private byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > 0) {
			bout.write(buf, 0, len);
		}
		in.close();
		bout.close();
		return bout.toByteArray();
	}
}
