package com.dianping.phoenix.agent.util.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

public class DefaultUrlContentFetcher implements UrlContentFetcher {

	@Override
	public String fetchUrlContent(String url, int timeout) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		conn.setConnectTimeout(timeout);
		conn.setReadTimeout(timeout);
		InputStream in = conn.getInputStream();
		return IOUtils.toString(in);
	}

}
