package com.dianping.hotdeploy.thread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.hotdeploy.server.MyInstrumentation;
import com.dianping.hotdeploy.utils.StringUtils;

public class ClassFileWatchThread extends Thread {
	private static final Logger logger = Logger.getLogger(ClassFileWatchThread.class);

	private static final String HOME_DIR = System.getenv("HOME") == null ? System.getenv("USERPROFILE") : System
			.getenv("HOME");
	private static final String MVN_REPO_PREFIX = "M2_REPO";
	private static final String MVN_REPO_HOME = HOME_DIR + "/.m2/repository";

	private static int m_threadHolder = 0;

	private static Instrumentation m_ins = MyInstrumentation.getInstrumentation();
	private long m_lastCheck = System.currentTimeMillis();

	private File m_path;// src's path
	private ClassLoader m_cl;// container class loader

	private File m_cPath;// classpath-file's path of src
	private List<URL> m_cURLs;// classpath-file's urls
	private File m_cDir;// class-files dir
	private Set<File> m_holder;// class-files which are watched
	private Set<File> m_modifed;// class-files which are modified at last period

	public ClassLoader getClassloader() {
		return m_cl;
	}

	public ClassFileWatchThread(String srcPath) {
		if (!StringUtils.isBlank(srcPath)) {
			if (checkPath(srcPath)) {
				try {
					parseClasspath();
				} catch (Exception e) {
					throw new RuntimeException("Can not parse classpath file: " + m_cPath);
				}
				if (m_cURLs != null && m_cURLs.size() > 0) {
					m_cl = new URLClassLoader(m_cURLs.toArray(new URL[0]));
				} else {
					throw new RuntimeException(String.format("Classpath file [%s] contains no path.", m_cPath));
				}
				walkPath();
				setDaemon(true);
				setName(String.format("Phoenix-ClassFileWatchThread-%s", m_threadHolder++));
			} else {
				throw new RuntimeException(String.format("App path: %s is not exist.", srcPath));
			}
		} else {
			throw new RuntimeException("Param [ srcPath ] can not be blank.");
		}
	}

	private boolean checkPath(String srcPath) {
		return (m_path = new File(srcPath)).exists() && m_path.isDirectory()
				&& (m_cPath = new File(srcPath + File.separator + ".classpath")).exists() ? true : false;
	}

	private void parseClasspath() throws Exception {
		String[] tagKinds = new String[] { "output", "var" };

		m_cURLs = new ArrayList<URL>();

		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		NodeList cps = db.parse(m_cPath).getElementsByTagName("classpathentry");
		for (int idx = 0; idx < cps.getLength(); idx++) {
			Element ele = (Element) cps.item(idx);
			for (String kind : tagKinds) {
				if (kind.equals(ele.getAttribute("kind"))) {
					String path = ele.getAttribute("path");
					if (path.startsWith(MVN_REPO_PREFIX)) {
						path = MVN_REPO_HOME + path.substring(MVN_REPO_PREFIX.length());
					} else if (kind.equals("output")) {
						path = m_cPath.getParent() + File.separator + path;
						if (!(m_cDir = new File(path)).exists() || !m_cDir.isDirectory()) {
							throw new RuntimeException("Class files dir is not exist.");
						}
					}
					m_cURLs.add(new File(path).toURI().toURL());
				}
			}
		}
	}

	private void walkPath() {
		m_modifed = new HashSet<File>();
		Set<File> newHolder = new HashSet<File>();
		Map<File, Long> mfl = recurseDir(m_cDir, ".*\\.class");
		if (mfl != null && mfl.size() > 0) {
			newHolder.addAll(mfl.keySet());
			for (Map.Entry<File, Long> entry : mfl.entrySet()) {
				if (m_holder != null && m_holder.contains(entry.getKey()) && entry.getValue() > m_lastCheck) {
					m_modifed.add(entry.getKey());
				}
			}
		} else {
			logger.warn(String.format("Path [ %s ] doesn't has any class file.", m_cDir));
		}
		m_lastCheck = System.currentTimeMillis();
		m_holder = newHolder;
	}

	private Map<File, Long> recurseDir(File startDir, String regex) {
		Map<File, Long> ret = new HashMap<File, Long>();
		for (File item : startDir.listFiles()) {
			if (item.isDirectory()) {
				ret.putAll(recurseDir(item, regex));
			} else {
				if (item.getName().matches(regex)) {
					ret.put(item, item.lastModified());
				}
			}
		}
		return ret;
	}

	private void checkAndRedefine() throws IOException, ClassNotFoundException, UnmodifiableClassException {
		walkPath();
		if (m_modifed != null && m_modifed.size() > 0) {
			redefineModified();
		}
	}

	private void redefineModified() throws IOException, ClassNotFoundException, UnmodifiableClassException {
		for (File file : m_modifed) {
			if (file.exists()) {
				System.out.println("File chanaged: " + file);
				FileInputStream fin = new FileInputStream(file);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				int len;
				while ((len = fin.read(buf)) > 0) {
					bout.write(buf, 0, len);
				}
				byte[] theClassFile = bout.toByteArray();
				String className = getClassName(file);
				try {
					ClassDefinition definition = new ClassDefinition(Class.forName(className, true, m_cl), theClassFile);
					m_ins.redefineClasses(definition);
				} catch (Throwable e) {
					System.out.println(String.format("Class [ %s ] redefine failed.", className));
					e.printStackTrace();
				}
			}
		}
	}

	public String getClassName(File file) {
		String c = file.getPath();
		int start = c.indexOf("classes" + File.separator, m_path.getPath().length())
				+ ("classes" + File.separator).length();
		c = c.substring(start).replaceAll(File.separator, ".");
		return c.substring(0, c.lastIndexOf(".class"));
	}

	@Override
	public void run() {
		while (true) {
			try {
				checkAndRedefine();
				Thread.sleep(5000);
			} catch (Throwable e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
	}
}
