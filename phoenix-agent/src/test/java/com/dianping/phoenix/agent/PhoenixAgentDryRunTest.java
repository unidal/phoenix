package com.dianping.phoenix.agent;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.transform.DefaultJsonBuilder;

public class PhoenixAgentDryRunTest {

	@Test
	public void testResponseJsonCheck() throws IOException {
		error(null);
		error("");
		error("{}");
		ok(makeResponseJson("127.0.0.1"));
		error(makeResponseJson(""));
	}

	private String makeResponseJson(String ip) {
		Response res = new Response();
		res.setIp(ip);
		return new DefaultJsonBuilder().buildJson(res);
	}

	private void error(String resJson) {
		Assert.assertFalse(PhoenixAgentDryRun.responseJsonOk(resJson));
	}
	
	private void ok(String resJson) throws IOException {
		Assert.assertTrue(PhoenixAgentDryRun.responseJsonOk(resJson));
	}
	
}
