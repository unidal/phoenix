package com.dianping.kernel.inspect.page.classpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import com.dianping.phoenix.spi.internal.VersionParser;
import com.site.helper.Scanners;
import com.site.helper.Scanners.IMatcher;

public class ArtifactResolver implements LogEnabled {
	private Logger m_logger;

	private VersionParser m_parser = new VersionParser();

	private Artifact buildArtifactFromPath(File jarFile) throws IOException {
		String name = jarFile.getName();
		String[] parts = m_parser.parse(name.substring(0, name.length() - 4));
		String groupId = null; // No groupId here

		return new Artifact(jarFile.getCanonicalPath(), groupId, parts[0], parts[1]);
	}

	private Artifact buildArtifactFromManifest(File jarFile, String name) throws IOException {
		JarFile jar = new JarFile(jarFile);
		ZipEntry entry = jar.getEntry(name);
		InputStream is = jar.getInputStream(entry);
		Properties p = new Properties();

		p.load(is);
		jar.close();

		String groupId = p.getProperty("Implementation-Vendor-Id");
		String artifactId = "";
		String version = p.getProperty("Implementation-Version");
		
		if(groupId == null){
			groupId = p.getProperty("Implementation-Title");
		}
		
		String fileName = jarFile.getName();
		String[] parts = m_parser.parse(fileName.substring(0, fileName.length() - 4));
		
		if(version != null){
			if(version.equals(parts[1])){
				artifactId = parts[0];
			}else{
				artifactId = fileName.substring(0, fileName.length() - 4);
			}
		}else{
			artifactId = parts[0];
			version = parts[1];
		}

		return new Artifact(jarFile.getPath(), groupId, artifactId, version);
	}
	
	private Artifact buildArtifactFromPom(File jarFile,String name) throws IOException {
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
	
	private String getManifestProperties(File jarFile) {
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
				if (path.equals("META-INF/MANIFEST.MF")) {
					return Direction.MATCHED;
				} else {
					return Direction.NEXT;
				}
			}
		});
	}

	public Artifact resolve(File jarFile) {
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
		String manifestName = getManifestProperties(jarFile);
		if(manifestName != null){
			try {
				return buildArtifactFromManifest(jarFile, manifestName);
			} catch (Exception e) {
				m_logger.warn(String.format("Unable to read entry(%s) out of jar(%s)!", manifestName, jarFile), e);
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
