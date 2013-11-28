package com.dianping.phoenix.session;

public class RequestEvent {
	private String m_userId;

	private String m_urlDigest;

	private String m_refererUrlDigest;

	private String requestId;

	private long m_timestamp;

	private int m_hop;

	public int getHop() {
		return m_hop;
	}

	public String getRefererUrlDigest() {
		return m_refererUrlDigest;
	}

	public String getRequestId() {
		return requestId;
	}

	public long getTimestamp() {
		return m_timestamp;
	}

	public String getUrlDigest() {
		return m_urlDigest;
	}

	public String getUserId() {
		return m_userId;
	}

	public void setHop(int hop) {
		m_hop = hop;
	}

	public void setRefererUrlDigest(String refererUrlDigest) {
		m_refererUrlDigest = refererUrlDigest;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setTimestamp(long timestamp) {
		m_timestamp = timestamp;
	}

	public void setUrlDigest(String urlDigest) {
		m_urlDigest = urlDigest;
	}

	public void setUserId(String userId) {
		m_userId = userId;
	}
}
