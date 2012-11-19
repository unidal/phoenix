package com.dianping.phoenix.console.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

public class DefaultWarService implements WarService {
	@Inject
	private StatusReporter m_reporter;

	private String m_urlPattern = "http://192.168.8.45:8080/artifactory/dianping-snapshots/com/dianping/platform/phoenix-kernel/0.1-SNAPSHOT/phoenix-kernel-%s.war";

	@Override
	public void downloadAndExtractTo(String version, File target)
			throws IOException {
		m_reporter.log(String.format("Downloading war for version(%s) ... ",
				version));

		String url = String.format(m_urlPattern, version);
		InputStream in = new URL(url).openStream();

		try {
			Files.forZip().copyDir(new ZipInputStream(in), target);
			m_reporter.log(String.format(
					"Downloading war for version(%s) ... DONE.", version));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore it
			}
		}
	}

}
