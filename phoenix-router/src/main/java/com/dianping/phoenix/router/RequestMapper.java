package com.dianping.phoenix.router;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;

public class RequestMapper extends ContainerHolder {
	
	private static Logger log = Logger.getLogger(RequestMapper.class);

	public enum REQUEST_TYPE {
		GET, POST
	};

	private Set<String> headerToRemoveSet;

	public RequestMapper() {
		headerToRemoveSet = new HashSet<String>();
		headerToRemoveSet.add("Host");
		headerToRemoveSet.add("Content-Length");
		headerToRemoveSet.add("Referer");
	}

	@SuppressWarnings("unchecked")
	public HttpRequestBase make(HttpServletRequest oldReq, String newUrl, REQUEST_TYPE type) {
		HttpRequestBase newReq;
		switch (type) {
		case GET:
			newReq = new HttpGet(newUrl);
			break;

		case POST:
			newReq = new HttpPost(newUrl);
			break;

		default:
			throw new RuntimeException("unsupported http request type " + type);
		}
		Enumeration<String> headerNameEnum = oldReq.getHeaderNames();
		while (headerNameEnum.hasMoreElements()) {
			String headerName = headerNameEnum.nextElement();
			if (!removeThisHeader(headerName)) {
				Enumeration<String> headerValueEnum = oldReq.getHeaders(headerName);

				// assemble header value
				while (headerValueEnum.hasMoreElements()) {
					String headerValue = headerValueEnum.nextElement();
					newReq.addHeader(headerName, headerValue);
				}
			}
		}
		return newReq;
	}

	private boolean removeThisHeader(String headerName) {
		return headerToRemoveSet.contains(headerName);
	}

	public InputStream send(HttpServletRequest oldReq, String newUrl, REQUEST_TYPE type) throws IOException {
		DefaultHttpClient hc = new DefaultHttpClient();
		HttpRequestBase req = make(oldReq, newUrl, type);
		log.info("sending request to " + req.getURI());
		HttpResponse res = hc.execute(req);
		return res.getEntity().getContent();
	}

}
