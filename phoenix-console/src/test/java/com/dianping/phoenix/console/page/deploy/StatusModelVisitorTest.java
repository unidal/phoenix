package com.dianping.phoenix.console.page.deploy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.transform.DefaultSaxParser;

public class StatusModelVisitorTest {
	@Test
	public void testVisit() throws SAXException, IOException {
		DeployModel origin = DefaultSaxParser.parse(getClass().getResourceAsStream("status.xml"));
		Map<String, Integer> map = new HashMap<String, Integer>();
		StatusModelVisitor visitor = new StatusModelVisitor(map);

		map.put("192.168.66.132", 1);
		map.put("192.168.66.133", 3);

		origin.accept(visitor);

		DeployModel model = visitor.getModel();

		// System.out.println(model);
		Assert.assertEquals(4, model.getHosts().size());

		HostModel summary = model.findHost("summary");

		Assert.assertEquals(50, summary.getProgress());
		Assert.assertEquals("deploying", summary.getStatus());

		HostModel host1 = model.findHost("192.168.66.131");
		Assert.assertEquals(12, host1.getProgress());
		Assert.assertEquals("step1", host1.getCurrentStep());
		Assert.assertEquals(1, host1.getOffset());
		Assert.assertEquals("First segment<br>", host1.getLog());

		HostModel host2 = model.findHost("192.168.66.132");
		Assert.assertEquals(34, host2.getProgress());
		Assert.assertEquals("step1", host2.getCurrentStep());
		Assert.assertEquals(2, host2.getOffset());
		Assert.assertEquals("Second segment<br>", host2.getLog());

		HostModel host3 = model.findHost("192.168.66.133");
		Assert.assertNull(host3);

		HostModel host4 = model.findHost("192.168.66.134");
		Assert.assertEquals("failed", host4.getStatus());
	}
}
