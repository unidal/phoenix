package com.dianping.phoenix.agent.core.task.workflow;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.tx.LogFormatterTest;

public class EngineTest extends ComponentTestCase {

	@SuppressWarnings("unchecked")
	@Test
	public void testStart() throws Exception {
		Engine engine = lookup(Engine.class);
		
		Context ctx  = mock(Context.class);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		when(ctx.getLogOut()).thenReturn(out);
		
		Step step1 = mock(Step.class);
		Step step2 = mock(Step.class);
		Step step3 = mock(Step.class);
		
		// 1->2(OK), 1->3(ERROR)
		when(step1.getNextStep(Step.CODE_OK)).thenReturn(step2);
		when(step1.getNextStep(Step.CODE_ERROR)).thenReturn(step3);
		
		Map<String, String> header1 = new HashMap<String, String>();
		header1.put(Step.HEADER_STEP, "step1");
		when(step1.getLogChunkHeader()).thenReturn(header1);
		
		Map<String, String> header2 = new HashMap<String, String>();
		header2.put(Step.HEADER_STEP, "step2");
		when(step2.getLogChunkHeader()).thenReturn(header2);
		
		Map<String, String> header3 = new HashMap<String, String>();
		header3.put(Step.HEADER_STEP, "step3");
		when(step3.getLogChunkHeader()).thenReturn(header3);
		
		// 1->2
		engine.start(step1, ctx);
		String txLog = out.toString();
		List<Map<String, String>> headerList = new ArrayList<Map<String,String>>();
		headerList.add(header1);
		headerList.add(header2);
		LogFormatterTest.checkLogFormat(IOUtils.readLines(new StringReader(txLog)), headerList , null);
		
		// 1->3
		when(step1.doStep(any(Context.class))).thenReturn(Step.CODE_ERROR);
		out = new ByteArrayOutputStream();
		when(ctx.getLogOut()).thenReturn(out);
		engine.start(step1, ctx);
		txLog = out.toString();
		headerList = new ArrayList<Map<String,String>>();
		headerList.add(header1);
		headerList.add(header3);
		LogFormatterTest.checkLogFormat(IOUtils.readLines(new StringReader(txLog)), headerList , null);
		
	}

}
