package com.dianping.phoenix.agent.util.url;

import java.io.IOException;

public interface UrlContentFetcher {

	String fetchUrlContent(String url, int timeout) throws IOException;
	
}
