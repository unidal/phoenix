package com.dianping.hotdeploy.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.hotdeploy.PhoenixDeployer;
import com.dianping.hotdeploy.utils.StringUtils;

public class PhoenixClassLoader extends URLClassLoader {
	private static final Logger LOGGER = Logger.getLogger(PhoenixClassLoader.class);

	private static final String HOME_DIR = System.getProperty("user.home");
	private static final String MVN_REPO_PREFIX = "M2_REPO";
	private static final String MVN_REPO_HOME = HOME_DIR + "/.m2/repository";

	private static AtomicInteger s_threadId = new AtomicInteger(0);

	private File m_classesDir;

	private PhoenixDeployer m_deployer = new PhoenixDeployer(this);

	public PhoenixClassLoader(String projectDir, int checkFreq) {
		super(new URL[] {});
		parseAndAddClasspath(projectDir);
		startMonitorThread(checkFreq);
	}

	public PhoenixClassLoader(String classesDir, List<String> extraClasspathEntry, int checkFreq) {
		super(new URL[] {});
		m_classesDir = getAndCheckDir(classesDir);
		addPaths(m_classesDir, extraClasspathEntry);
		startMonitorThread(checkFreq);
	}

	private void startMonitorThread(int checkFreq) {
		Thread monitorThread = new Thread(new ClassesMonitor(checkFreq));
		monitorThread.setName(String.format("Phoenix-Classes-Watchdog-%d", s_threadId.addAndGet(1)));
		monitorThread.start();
	}

	private void parseAndAddClasspath(String projectDir) {
		File pf = getAndCheckDir(projectDir);
		File cf = new File(pf, File.separator + ".classpath");
		if (cf.exists() && cf.isFile()) {
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				NodeList cps = db.parse(cf).getElementsByTagName("classpathentry");
				for (int idx = 0; idx < cps.getLength(); idx++) {
					Element ele = (Element) cps.item(idx);
					String kind = ele.getAttribute("kind");
					String path = ele.getAttribute("path");
					if ("var".equals(kind)) {
						if (path.startsWith(MVN_REPO_PREFIX)) {
							path = MVN_REPO_HOME + path.substring(MVN_REPO_PREFIX.length());
						} else {
							path = pf.getPath() + File.separator + path;
						}
						super.addURL(new File(path).toURI().toURL());
					} else if ("output".equals(kind)) {
						path = cf.getParent() + File.separator + path;
						if (!(m_classesDir = new File(path)).exists() || !m_classesDir.isDirectory()) {
							throw new RuntimeException("Class files dir is not exist.");
						}
						super.addURL(new File(path).toURI().toURL());
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Can not parse classpath file.", e);
			}
		} else {
			throw new RuntimeException("Can not find classpath file.");
		}
	}

	private void addPaths(File classesDir, List<String> libDirs) {
		try {
			super.addURL(classesDir.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException("ClassesDir is incorrectly.", e);
		}
		if (libDirs != null) {
			for (String libDir : libDirs) {
				try {
					super.addURL(getAndCheckDir(libDir).toURI().toURL());
				} catch (Exception e) {
					LOGGER.warn(String.format("Path [%s] is not a correctly directory, ignore it.", libDir));
				}
			}
		}
	}

	private File getAndCheckDir(String path) {
		if (!StringUtils.isBlank(path)) {
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory()) {
				return dir;
			}
			throw new IllegalArgumentException(String.format("Path [%s] is not a dir.", path));
		}
		throw new IllegalArgumentException("Param [path] can not be blank.");
	}

	private class ClassesMonitor implements Runnable {
		private int m_freq = 5000;
		private long m_lastCheck;
		private Set<File> m_holder;

		public ClassesMonitor(int checkFreq) {
			m_freq = checkFreq;
		}

		public void run() {
			walkPath(m_classesDir);
			m_lastCheck = System.currentTimeMillis();
			while (true) {
				try {
					Thread.sleep(m_freq);
					m_deployer.redefineClasses(m_classesDir, walkPath(m_classesDir));
				} catch (Throwable e) {
					LOGGER.error(e);
				}
			}
		}

		private Set<File> walkPath(File classesDir) {
			Set<File> newHolder = new HashSet<File>();
			Set<File> modified = new HashSet<File>();
			Map<File, Long> mfl = recurseDir(classesDir, ".*\\.class");
			if (mfl != null && mfl.size() > 0) {
				newHolder.addAll(mfl.keySet());
				for (Map.Entry<File, Long> entry : mfl.entrySet()) {
					if (m_holder != null && m_holder.contains(entry.getKey()) && entry.getValue() > m_lastCheck) {
						modified.add(entry.getKey());
					}
				}
			} else {
				LOGGER.warn(String.format("Path [ %s ] doesn't has any class file.", classesDir));
			}
			m_lastCheck = System.currentTimeMillis();
			m_holder = newHolder;
			return modified;
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
	}
}
