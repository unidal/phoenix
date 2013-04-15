package com.dianping.phoenix.deploy.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class AgentReader {
	private static final String SEPARATOR = "--9ed2b78c112fbd17a8511812c554da62941629a8--";

	private static final String TERMINATOR = "--255220d51dc7fb4aacddadedfe252a346da267d4--";

	private Reader m_reader;

	private boolean m_last;

	private StringBuilder m_sb = new StringBuilder(4096);

	public AgentReader(Reader reader) {
		m_reader = reader;
	}

	public boolean hasNext() {
		return !m_last;
	}

	public String next(AgentProgress progress) throws IOException {
		char[] data = new char[2048];
		String segment = null;

		while (true) {
			int len = 0;
			len = m_reader.read(data);

			if (len > 0) {
				m_sb.append(data, 0, len);
			}

			int pos = m_sb.indexOf(SEPARATOR);
			int sl = SEPARATOR.length();

			if (pos > 0 && pos + sl < m_sb.length() && m_sb.charAt(pos + sl) == '\r') {
				sl++;
			}

			if (pos > 0 && pos + sl < m_sb.length() && m_sb.charAt(pos + sl) == '\n') {
				sl++;
			}

			if (pos >= 0) {
				segment = m_sb.substring(0, pos);

				m_sb.delete(0, pos + sl);
				break;
			} else {
				pos = m_sb.indexOf(TERMINATOR);

				if (pos >= 0) {
					m_last = true;
					segment = m_sb.substring(0, pos);
					break;
				}
			}
		}

		if (segment != null) {
			if (segment.length() == 0) {
				return "";
			}

			BufferedReader reader = new BufferedReader(new StringReader(segment), segment.length());
			StringBuilder sb = new StringBuilder();
			boolean header = true;

			while (true) {
				String line = reader.readLine();

				if (line == null) {
					break;
				} else {
					if (header) {
						if (line.length() == 0) { // first blank line
							header = false;
						} else if (line.startsWith("Progress:")) {
							int pos1 = "Progress:".length();
							int pos2 = line.indexOf('/', pos1);

							progress.setCurrent(Integer.parseInt(line.substring(pos1, pos2).trim()));
							progress.setTotal(Integer.parseInt(line.substring(pos2 + 1).trim()));
						} else if (line.startsWith("Status:")) {
							int pos = "Status:".length();

							progress.setStatus(line.substring(pos).trim());
						} else if (line.startsWith("Step:")) {
							int pos = "Step:".length();

							progress.setStep(line.substring(pos).trim());
						}
					} else {
						sb.append(line).append("\r\n");
					}
				}
			}

			return sb.toString();
		}

		throw new IOException(String.format("Invalid protocol! segment: %s\r\n", segment));
	}
}