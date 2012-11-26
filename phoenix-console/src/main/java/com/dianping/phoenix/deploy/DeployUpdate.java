package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.io.OutputStream;

public class DeployUpdate {
	private boolean m_done;

	private String m_content;

	public DeployUpdate() {
	}

	public DeployUpdate(boolean done) {
		m_done = done;
	}

	public boolean isDone() {
		return m_done;
	}

	public void setContent(String content) {
		m_content = content;
	}

	public void setDone(boolean done) {
		m_done = done;
	}

	public void writeTo(OutputStream out) throws IOException {
		if (!m_done && m_content != null) {
			out.write(m_content.getBytes("utf-8"));
		}
	}
}
