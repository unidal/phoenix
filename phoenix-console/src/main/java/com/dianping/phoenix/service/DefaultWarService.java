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

	@Override
	public void downloadAndExtractTo(String type, String version, File target) throws IOException {
		String url = getWarUrl(type, version);
		InputStream in = new URL(url).openStream();

		try {
			Files.forZip().copyDir(new ZipInputStream(in), target);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore it
			}
		}
	}

	@Override
	public String getWarUrl(String type, String version) {
		return m_configManager.getWarUrl(type, version);
	}
}
