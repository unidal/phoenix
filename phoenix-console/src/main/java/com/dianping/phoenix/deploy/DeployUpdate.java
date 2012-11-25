package com.dianping.phoenix.deploy;

import java.io.IOException;
import java.io.OutputStream;

public class DeployUpdate {
	private boolean m_done;

	public DeployUpdate(boolean done) {
		m_done = done;
	}

	public boolean isDone() {
		return m_done;
	}

	public DeployUpdate setDone(boolean done) {
		m_done = done;
		return this;
	}

	public void writeTo(OutputStream out) throws IOException {
		if (!m_done) {
			// TODO
		}
	}
}
