package com.dianping.phoenix.router.hotdeploy.classloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.phoenix.router.hotdeploy.ClassRedefiner;
import com.dianping.phoenix.utils.version.VersionParser;

public class PhoenixClassLoader extends WebAppClassLoader {
	private static final Logger LOGGER = Logger.getLogger(PhoenixClassLoader.class);

	private static final String HOME_DIR = System.getProperty("user.home");
	private static final String MVN_REPO_PREFIX = "M2_REPO";
	private static final String MVN_REPO_HOME = HOME_DIR + "/.m2/repository";

	private static AtomicInteger s_threadId = new AtomicInteger(0);

	private File m_classesDir;

	private ClassRedefiner m_deployer = new ClassRedefiner(this);

	private File m_projectDir;

	private VersionParser m_versionParser = new VersionParser();

	public PhoenixClassLoader(File projectDir, int checkFreq, WebAppContext ctx, ClassLoader parent,
			List<String> sequence) throws IOException {
		super(parent, ctx);
		init(projectDir, checkFreq, sequence);
	}

	public PhoenixClassLoader(File projectDir, int checkFreq, WebAppContext ctx, ClassLoader parent) throws IOException {
		super(parent, ctx);
		init(projectDir, checkFreq, null);
	}

	private void init(File projectDir, int checkFreq, List<String> sequence) {
		m_projectDir = projectDir;
		parseAndAddClasspath(projectDir, sequence);
		startMonitorThread(checkFreq);
	}

	public File getProjectDir() {
		return m_projectDir;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}

	private PhoenixClassLoader(File classesDir, List<File> extraClasspathEntry, int checkFreq, WebAppContext ctx,
			ClassLoader parent) throws IOException {
		super(parent, ctx);
		m_classesDir = ensureDirExists(classesDir);
		addPaths(m_classesDir, extraClasspathEntry);
		startMonitorThread(checkFreq);
	}

	private void startMonitorThread(int checkFreq) {
		Thread monitorThread = new Thread(new ClassesMonitor(checkFreq));
		monitorThread.setName(String.format("Phoenix-Classes-Watchdog-%d", s_threadId.addAndGet(1)));
		monitorThread.start();
	}

	private void parseAndAddClasspath(File projectDir, List<String> sequence) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("##### Starting scan project dir: [%s].", projectDir.getPath()));
		}
		File cf = new File(projectDir, File.separator + ".classpath");
		if (cf.exists() && cf.isFile()) {
			List<URL> extraDirs = new ArrayList<URL>();
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				NodeList cps = db.parse(cf).getElementsByTagName("classpathentry");
				for (int idx = 0; idx < cps.getLength(); idx++) {
					Element ele = (Element) cps.item(idx);
					String kind = ele.getAttribute("kind");
					String path = ele.getAttribute("path");
					if ("var".equals(kind) || "lib".equals(kind)) {
						if (path.startsWith(MVN_REPO_PREFIX)) {
							path = MVN_REPO_HOME + path.substring(MVN_REPO_PREFIX.length());
						} else {
							path = projectDir.getAbsolutePath() + File.separator + path;
						}
						extraDirs.add(new File(path).toURI().toURL());
					} else if ("output".equals(kind)) {
						path = cf.getParent() + File.separator + path;
						if (!(m_classesDir = new File(path)).exists() || !m_classesDir.isDirectory()) {
							LOGGER.warn(String.format("Classes dir [%s] does not exist.", path));
						} else {
							super.addURL(m_classesDir.toURI().toURL());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(String.format("#####\tAdd URL [%s].", m_classesDir.toURI().toURL()));
							}
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Can not parse classpath file.", e);
			}
			if (m_classesDir == null || !m_classesDir.exists() || !m_classesDir.isDirectory()) {
				throw new RuntimeException("Can not find classes dir in project.");
			}

			sortWithSequence(extraDirs, sequence);

			for (URL var : extraDirs) {
				super.addURL(var);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("#####\tAdd URL [%s].", var));
				}
			}
		} else {
			throw new RuntimeException("Can not find classpath file.");
		}
	}

	void sortWithSequence(List<URL> list, List<String> sequence) {
		if (sequence == null || sequence.size() == 0 || list == null || list.size() == 0) {
			return;
		} else {
			List<Boolean> existArray = new ArrayList<Boolean>();
			Pattern pattern = Pattern.compile("(\\d+((\\.\\d+\\.)+|\\.)\\d+)");
			List<Queue<URL>> sortedArray = new ArrayList<Queue<URL>>();

			for (int idx = 0; idx < sequence.size(); idx++) {
				sortedArray.add(new LinkedList<URL>());
			}

			for (URL url : list) {
				boolean match = false;
				for (int idx = 0; idx < sequence.size(); idx++) {
					if (isMatch(pattern, url, sequence.get(idx))) {
						sortedArray.get(idx).offer(url);
						match = true;
						break;
					}
				}
				existArray.add(match);
			}

			for (int idx = 0; idx < existArray.size(); idx++) {
				if (existArray.get(idx)) {
					for (Queue<URL> queue : sortedArray) {
						if (queue.size() > 0) {
							list.set(idx, queue.poll());
							break;
						}
					}
				}
			}
		}
	}

	private boolean isMatch(Pattern pattern, URL url, String str) {

		try {
			String name = new File(url.toURI()).getName();
			if (name.endsWith(".jar")) {
				name = m_versionParser.parse(name.substring(0, name.length() - ".jar".length()))[0];
				return name.equals(str);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			// ignore it
		}
		return false;
	}

	private void addPaths(File classesDir, List<File> libDirs) {
		try {
			URL classesDirUrl = classesDir.toURI().toURL();
			super.addURL(classesDirUrl);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("##### Add URL [%s].", classesDirUrl));
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("ClassesDir is incorrectly.", e);
		}
		if (libDirs != null) {
			for (File libDir : libDirs) {
				if (libDir.exists()) {
					try {
						URL libDirUrl = libDir.toURI().toURL();
						super.addURL(libDirUrl);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format("##### Add URL [%s].", libDirUrl));
						}
					} catch (Exception e) {
						LOGGER.warn(String.format("Path [%s] is not a correctly entity, ignore it.", libDir));
					}
				} else {
					LOGGER.warn(String.format("Entity [%s] does not exist, ignore it", libDir));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format("##### File [%s] does not exist.", libDir));
					}
				}
			}
		}
	}

	private File ensureDirExists(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			return dir;
		}
		throw new IllegalArgumentException(String.format("Path [%s] is not a dir.", dir.getAbsolutePath()));
	}

	private class ClassesMonitor implements Runnable {
		private int m_freq = 5000;
		private long m_lastCheck = 0;
		private Set<File> m_holder = null;

		public ClassesMonitor(int checkFreq) {
			m_freq = checkFreq;
		}

		public void run() {
			while (true) {
				try {
					m_deployer.redefineClasses(m_classesDir, checkAndGetModifiedClasses(m_classesDir));
					Thread.sleep(m_freq);
				} catch (Throwable e) {
					LOGGER.error(e);
				}
			}
		}

		private Set<File> checkAndGetModifiedClasses(File classesDir) {
			long checkTime = System.currentTimeMillis();
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
				LOGGER.warn(String.format("Path [%s] does not has any class file.", classesDir));
			}
			m_lastCheck = checkTime;
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
