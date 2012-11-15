package com.dianping.phoenix.console.service;

import java.io.File;
import java.io.IOException;

public interface WarService {
	public void downloadAndExtractTo(String version, File target) throws IOException;
}
