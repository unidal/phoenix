package com.dianping.phoenix.router.hotdeploy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Set;

import org.apache.log4j.Logger;

public class ClassRedefiner {
	private static final Logger LOGGER = Logger.getLogger(ClassRedefiner.class);

	private static Instrumentation m_ins;

	private ClassLoader m_cl;

	public static void setInstrumentation(Instrumentation ins) {
		m_ins = ins;
	}

	public ClassRedefiner(ClassLoader cl) {
		if (cl == null) {
			throw new IllegalArgumentException("Param [classloader] can not be null.");
		}
		m_cl = cl;
	}

	public void redefineClasses(File classesDir, Set<File> modifiedClasses) {
		if (classesDir.exists() && classesDir.isDirectory() && modifiedClasses != null) {
			for (File f : modifiedClasses) {
				if (f.exists() && f.isFile() && f.getPath().endsWith(".class")) {
					try {
						FileInputStream fin = new FileInputStream(f);
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						byte[] buf = new byte[4096];
						int len;
						while ((len = fin.read(buf)) > 0) {
							bout.write(buf, 0, len);
						}
						byte[] theClassFile = bout.toByteArray();
						String className = getClassNameFromFile(classesDir, f);
						try {
							ClassDefinition definition = new ClassDefinition(Class.forName(className, true, m_cl),
									theClassFile);
							m_ins.redefineClasses(definition);
						} catch (Throwable e) {
							LOGGER.error(String.format("Class [ %s ] redefine failed.", className), e);
						}
					} catch (Exception e) {
						LOGGER.error(String.format("Class file [%s] is not correctly, ignore it.", f), e);
					}
				} else {
					LOGGER.error(String.format("Path [%s] is not a class file, ignore it.", f.getPath()));
				}
			}
		}
	}

	private String getClassNameFromFile(File classesDir, File classFile) {
		String r = classFile.getPath().substring(classesDir.getPath().length()).replaceAll(File.separator, ".");
		return r.substring(r.startsWith(".") ? 1 : 0, r.lastIndexOf(".class"));
	}
}
