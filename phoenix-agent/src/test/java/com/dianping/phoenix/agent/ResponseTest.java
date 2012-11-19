package com.dianping.phoenix.agent;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.transform.DefaultJsonBuilder;
import com.dianping.phoenix.agent.response.transform.DefaultJsonParser;
import com.dianping.phoenix.agent.response.transform.DefaultSaxParser;

public class ResponseTest {
	@Test
	public void testModel() throws SAXException, IOException {
		Response response = DefaultSaxParser.parse(getClass().getResourceAsStream("response.xml"));

		System.out.println(response);

		String json = new DefaultJsonBuilder().buildJson(response);

		System.out.println(json);

		System.out.println(DefaultJsonParser.parse(json));
	}
}
