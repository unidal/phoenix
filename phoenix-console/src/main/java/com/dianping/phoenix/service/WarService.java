package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;

import com.dianping.phoenix.console.page.version.VersionContext;

public interface WarService {
	public void downloadAndExtractTo(VersionContext context, File target) throws IOException;
}
