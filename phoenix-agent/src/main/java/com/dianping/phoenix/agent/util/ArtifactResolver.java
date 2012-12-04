package com.dianping.phoenix.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.IMatcher;

public class ArtifactResolver implements LogEnabled {
	private Logger m_logger;

	private VersionParser m_parser = new VersionParser();

	private Artifact buildArtifactFromPath(File jarFile) throws IOException {
		String name = jarFile.getName();
		String[] parts = m_parser.parse(name.substring(0, name.length() - 4));
		String groupId = null; // No groupId here

		return new Artifact(jarFile.getCanonicalPath(), groupId, parts[0], parts[1]);
	}

	private Artifact buildArtifactFromPom(File jarFile, String name) throws IOException {
		JarFile jar = new JarFile(jarFile);
		ZipEntry entry = jar.getEntry(name);
		InputStream is = jar.getInputStream(entry);
		Properties p = new Properties();

		p.load(is);
		jar.close();

		String groupId = p.getProperty("groupId");
		String artifactId = p.getProperty("artifactId");
		String version = p.getProperty("version");

		return new Artifact(jarFile.getPath(), groupId, artifactId, version);
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	private String getPomProperties(File jarFile) {
		return Scanners.forJar().scanForOne(jarFile, new IMatcher<File>() {
			@Override
			public boolean isDirEligible() {
				return false;
			}

			@Override
			public boolean isFileElegible() {
				return true;
			}

			@Override
			public Direction matches(File base, String path) {
				if (path.startsWith("META-INF/maven")) {
					if (path.endsWith("/pom.properties")) {
						return Direction.MATCHED;
					} else {
						return Direction.DOWN;
					}
				} else {
					return Direction.NEXT;
				}
			}
		});
	}

	public Artifact resolve(File file) {
		if(file.isDirectory()) {
			return resolveForDir(file);
		} else {
			return resolveForJar(file);
		}
	}
	
	private Artifact resolveForDir(File warRoot) {
		File pom = Scanners.forDir().scanForOne(warRoot, new IMatcher<File>() {
			@Override
			public boolean isDirEligible() {
				return false;
			}

			@Override
			public boolean isFileElegible() {
				return true;
			}
			
			private int depth = 0;

			@Override
			public Direction matches(File base, String path) {
				switch (depth) {
				case 0:
					if("META-INF".equals(path)) {
						depth++;
						return Direction.DOWN;
					} else {
						return Direction.NEXT;
					}
				case 1:
					if("META-INF/maven".equals(path)) {
						depth++;
						return Direction.DOWN;
					} else {
						return Direction.NEXT;
					}
				case 2:
					depth++;
					return Direction.DOWN;
				case 3:
					depth++;
					return Direction.DOWN;
				case 4:
					if(path.endsWith("pom.properties")) {
						return Direction.MATCHED;
					} else {
						return Direction.NEXT;
					}

				default:
					return Direction.NEXT;
				}
			}
		});
		
		if (pom != null) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(pom));
			} catch (IOException e) {
				m_logger.warn(String.format("Unable to read entry out of pom(%s)!", pom.getAbsolutePath()), e);
				return null;
			}

			String groupId = p.getProperty("groupId");
			String artifactId = p.getProperty("artifactId");
			String version = p.getProperty("version");

			return new Artifact(warRoot.getAbsolutePath(), groupId, artifactId, version);
		} else {
			return null;
		}
		
	}
	
	private Artifact resolveForJar(File jarFile) {
		if (!jarFile.isFile()) {
			return null;
		}

		String pomName = getPomProperties(jarFile);

		if (pomName != null) {
			try {
				return buildArtifactFromPom(jarFile, pomName);
			} catch (Exception e) {
				m_logger.warn(String.format("Unable to read entry(%s) out of jar(%s)!", pomName, jarFile), e);
			}
		}

		try {
			return buildArtifactFromPath(jarFile);
		} catch (Exception e) {
			m_logger.warn(String.format("Unable to build artifact from path(%s)!", jarFile), e);
		}

		return null;
	}
}
