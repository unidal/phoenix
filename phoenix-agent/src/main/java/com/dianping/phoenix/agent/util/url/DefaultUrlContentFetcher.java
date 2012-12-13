package com.dianping.phoenix.agent.util.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class DefaultUrlContentFetcher implements UrlContentFetcher {
	
	@Inject
	ConfigManager config;

	@Override
	public String fetchUrlContent(String url) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		conn.setConnectTimeout(config.getUrlConnectTimeout());
		conn.setReadTimeout(config.getUrlReadTimeout());
		InputStream in = conn.getInputStream();
		return IOUtils.toString(in);
	}

}
