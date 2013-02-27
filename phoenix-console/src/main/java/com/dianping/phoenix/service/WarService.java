package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;

public interface WarService {
	public String getWarUrl(String type, String version);

	public void downloadAndExtractTo(String type, String version, File target) throws IOException;
}
