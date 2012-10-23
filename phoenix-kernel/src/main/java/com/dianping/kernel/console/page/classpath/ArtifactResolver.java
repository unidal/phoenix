package com.dianping.kernel.console.page.classpath;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import com.site.helper.Scanners;
import com.site.helper.Scanners.IMatcher;

public class ArtifactResolver implements LogEnabled {
	private Logger m_logger;

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	public Artifact resolve(File jarFile) {
		if (!jarFile.isFile()) {
			return null;
		}

		String name = Scanners.forJar().scanForOne(jarFile, new IMatcher<File>() {
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

		if (name != null) {
			try {
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
			} catch (Exception e) {
				m_logger.warn(String.format("Unable to read entry(%s) out of jar(%s)!", name, jarFile), e);
			}
		}

		return null;
	}
}
