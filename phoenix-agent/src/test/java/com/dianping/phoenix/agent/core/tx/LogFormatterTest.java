package com.dianping.phoenix.agent.core.tx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class LogFormatterTest {

	public enum LogState {
		WAITING_HEADER, READING_HEADER, READING_CHUNK, END
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyHeaderLogFormat() throws IOException {
		LogFormatter logFormatter = new LogFormatter();

		ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		Map<String, String> headers = null;
		logFormatter.writeHeader(logOut, headers);
		logFormatter.writeChunkTerminator(logOut);
		logFormatter.writeTerminator(logOut);
		
		List<String> chunks = new ArrayList<String>();
		chunks.add("");
		List<Map<String, String>> headerList = new ArrayList<Map<String, String>>();
		headerList.add(Collections.EMPTY_MAP);
		checkLogFormat(IOUtils.readLines(new ByteArrayInputStream(logOut.toByteArray())), headerList, chunks);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyLogFormat() throws IOException {
		LogFormatter logFormatter = new LogFormatter();

		ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Status", "successful");
		logFormatter.writeHeader(logOut, headers);
		logFormatter.writeChunkTerminator(logOut);
		logFormatter.writeTerminator(logOut);
		
		List<String> chunks = new ArrayList<String>();
		chunks.add("");
		List<Map<String, String>> headerList = new ArrayList<Map<String, String>>();
		headerList.add(headers);
		checkLogFormat(IOUtils.readLines(new ByteArrayInputStream(logOut.toByteArray())), headerList, chunks);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSingleChunkLogFormat() throws IOException {
		LogFormatter logFormatter = new LogFormatter();

		ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Status", "successful");
		logFormatter.writeHeader(logOut, headers);
		logOut.write("chunk".getBytes());
		logFormatter.writeChunkTerminator(logOut);
		logFormatter.writeTerminator(logOut);
		
		List<String> chunks = new ArrayList<String>();
		chunks.add("chunk");
		List<Map<String, String>> headerList = new ArrayList<Map<String, String>>();
		headerList.add(headers);
		checkLogFormat(IOUtils.readLines(new ByteArrayInputStream(logOut.toByteArray())), headerList, chunks);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMultipleChunkLogFormat() throws IOException {
		LogFormatter logFormatter = new LogFormatter();

		ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		// chunk1
		Map<String, String> headers1 = new HashMap<String, String>();
		headers1.put("Status", "successful");
		logFormatter.writeHeader(logOut, headers1);
		logOut.write("chunk".getBytes());
		logFormatter.writeChunkTerminator(logOut);
		//chunk2
		Map<String, String> headers2 = new HashMap<String, String>();
		headers2.put("Status", "successful");
		headers2.put("Progress", "90/100");
		logFormatter.writeHeader(logOut, headers2);
		logOut.write("chunk".getBytes());
		logOut.write("chunk2".getBytes());
		logFormatter.writeChunkTerminator(logOut);
		logFormatter.writeTerminator(logOut);
		
		List<String> chunks = new ArrayList<String>();
		chunks.add("chunk");
		chunks.add("chunkchunk2");
		List<Map<String, String>> headerList = new ArrayList<Map<String, String>>();
		headerList.add(headers1);
		headerList.add(headers2);
		checkLogFormat(IOUtils.readLines(new ByteArrayInputStream(logOut.toByteArray())), headerList, chunks);
	}

	public static void checkLogFormat(List<String> logLines, List<Map<String, String>> headerList, List<String> chunks) {
		LogState state = LogState.WAITING_HEADER;

		int curHeaderIdx = 0;
		int curChunkIdx = 0;

		Map<String, String> curHeader = new HashMap<String, String>();
		StringBuilder curChunk = new StringBuilder();

		for (String line : logLines) {
			
			switch (state) {
			case WAITING_HEADER:
				if(line.trim().length() > 0) {
					if (!line.matches("[^:]+: .+")) {
						Assert.fail(String.format("log header format invalid %s", line));
					}

					String[] parts = line.split(": ");
					Assert.assertEquals(2, parts.length);
					curHeader.put(parts[0], parts[1]);
				}
				state = LogState.READING_HEADER;
				break;
				
			case READING_HEADER:
				if ("".equals(line)) {

					Assert.assertEquals(headerList.get(curHeaderIdx++), curHeader);
					curHeader.clear();

					state = LogState.READING_CHUNK;
				} else if (LogFormatter.LOG_TERMINATOR.trim().equals(line)) {
					state = LogState.END;
				} else if (LogFormatter.CHUNK_TERMINATOR.trim().equals(line)) {
					state = LogState.READING_HEADER;
				} else {

					if (!line.matches("[^:]+: .+")) {
						Assert.fail(String.format("log header format invalid %s", line));
					}

					String[] parts = line.split(": ");
					Assert.assertEquals(2, parts.length);
					curHeader.put(parts[0], parts[1]);

				}
				break;

			case READING_CHUNK:
				if (LogFormatter.CHUNK_TERMINATOR.trim().equals(line)) {
					Assert.assertEquals(chunks.get(curChunkIdx++), curChunk.toString());
					curChunk = new StringBuilder();
					
					state = LogState.WAITING_HEADER;
				} else {
					curChunk.append(line);
				}
				break;

			case END:
				Assert.fail();
				break;

			}
		}

	}

}
