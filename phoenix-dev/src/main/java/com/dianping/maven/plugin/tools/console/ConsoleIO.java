package com.dianping.maven.plugin.tools.console;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ConsoleIO {

	private String format(List<String> values, int column) {
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		int idx = 0;
		for (String v : values) {
			sb.append(idx);
			sb.append(":");
			sb.append(v);
			if (idx % column == column - 1) {
				sb.append(newLine);
			} else {
				sb.append("\t");
			}
			idx++;
		}
		return sb.toString();
	}

	public List<String> choose(List<String> availableValues, int column, String prompt, PrintWriter out, Reader in)
			throws IOException {
		out.println(prompt);
		out.println(format(availableValues, column));

		List<String> choosedValues = new ArrayList<String>();

		char[] buffer = new char[1024];
		int size = in.read(buffer);
		while (size > 0 && (buffer[size - 1] == '\n' || buffer[size - 1] == '\r')) {
			size--;
		}
		if (size > 0) {
			String input = new String(buffer, 0, size);
			String[] idxes = input.split(",");
			for (int i = 0; i < idxes.length; i++) {
				int idx = -1;
				try {
					idx = Integer.parseInt(idxes[i].trim());
				} catch (Exception e) {
					// ignore it
				}
				if (idx >= 0 && idx < availableValues.size()) {
					choosedValues.add(availableValues.get(idx));
				}
			}
		}

		return choosedValues;
	}

	public List<String> choice(List<String> availableValues, int column, String prompt) throws IOException {
		return choose(availableValues, column, prompt, new PrintWriter(System.out), new InputStreamReader(System.in));
	}

}
