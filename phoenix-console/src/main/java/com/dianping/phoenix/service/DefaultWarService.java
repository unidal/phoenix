package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.console.page.version.VersionContext;

public class DefaultWarService implements WarService {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private StatusReporter m_reporter;

	@Override
	public void downloadAndExtractTo(VersionContext context, File target)	
			throws IOException {
		
		String version = context.getVersion();
		
		m_reporter.categoryLog(DefaultStatusReporter.VERSION_LOG, version,
				String.format("Downloading war for version(%s) ... ", version));

		String url = m_configManager.getWarUrl(version);
		InputStream in = new URL(url).openStream();

		try {
			Files.forZip().copyDir(new ZipInputStream(in), target);
			m_reporter.categoryLog(DefaultStatusReporter.VERSION_LOG, version,
					String.format("Downloading war for version(%s) ... DONE.",
							version));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore it
			}
		}
	}

}
