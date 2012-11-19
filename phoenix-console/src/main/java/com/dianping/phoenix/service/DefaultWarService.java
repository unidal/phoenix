package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class DefaultWarService implements WarService {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private StatusReporter m_reporter;

	@Override
	public void downloadAndExtractTo(String version, File target) throws IOException {
		m_reporter.log(String.format("Downloading war for version(%s) ... ", version));

		String url = m_configManager.getWarUrl(version);
		InputStream in = new URL(url).openStream();

		try {
			Files.forZip().copyDir(new ZipInputStream(in), target);
			m_reporter.log(String.format("Downloading war for version(%s) ... DONE.", version));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore it
			}
		}
	}

}
