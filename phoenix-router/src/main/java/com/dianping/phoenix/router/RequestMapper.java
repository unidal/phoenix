package com.dianping.phoenix.router;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;

import com.dianping.phoenix.router.filter.request.RequestHolder;

public class RequestMapper extends ContainerHolder {

	private static Logger log = Logger.getLogger(RequestMapper.class);

	public enum REQUEST_TYPE {
		GET, POST
	};

	public HttpRequestBase make(HttpServletRequest oldReq, RequestHolder reqHolder, REQUEST_TYPE type)
			throws IOException {
		HttpRequestBase newReq;
		String newUrl = reqHolder.toUrl();

		switch (type) {
		case GET:
			newReq = new HttpGet(newUrl);
			break;

		case POST:
			HttpPost post = new HttpPost(newUrl);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			IOUtils.copy(oldReq.getInputStream(), bout);
			byte[] body = bout.toByteArray();
			post.setEntity(new InputStreamEntity(new ByteArrayInputStream(body), body.length));
			newReq = post;
			break;

		default:
			throw new RuntimeException("unsupported http request type " + type);
		}

		addHeaders(newReq, reqHolder.getHeaders());

		return newReq;
	}

	private void addHeaders(HttpRequestBase newReq, Map<String, List<String>> headers) {
		for (Map.Entry<String, List<String>> headerEntry : headers.entrySet()) {
			String name = headerEntry.getKey();

			// assemble header value
			for (String value : headerEntry.getValue()) {
				newReq.addHeader(name, value);
			}
		}
	}

	public HttpResponse send(HttpServletRequest oldReq, RequestHolder reqHolder, REQUEST_TYPE type) throws IOException {
		DefaultHttpClient hc = new DefaultHttpClient();
		HttpRequestBase req = make(oldReq, reqHolder, type);
		log.info("sending request to " + req.getURI());
		HttpResponse res = hc.execute(req);
		logHeaders(req.getAllHeaders(), "Request");
		logHeaders(res.getAllHeaders(), "Response");
		return res;
	}

	private void logHeaders(Header[] headers, String from) {
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (Header header : headers) {
				sb.append(header);
				sb.append("\n");
			}
			log.debug(from + " headers : " + sb.toString());
		}
	}

}
