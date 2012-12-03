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
	public void testVisitor() throws SAXException, IOException {
		DeployModel origin = DefaultSaxParser.parse(getClass().getResourceAsStream("deploy.xml"));
		Map<String, Integer> map = new HashMap<String, Integer>();
		StatusModelVisitor visitor = new StatusModelVisitor(map);

		map.put("192.168.66.132", 1);
		map.put("192.168.66.133", 2);

		origin.accept(visitor);

		DeployModel status = visitor.getModel();

		Assert.assertEquals(2, status.getHosts().size());

		HostModel host1 = status.findHost("192.168.66.131");
		Assert.assertEquals(34, host1.getProgress());
		Assert.assertEquals("step1", host1.getCurrentStep());
		Assert.assertEquals(2, host1.getOffset());
		Assert.assertEquals("First segment\r\nSecond segment\r\n", host1.getLog());

		HostModel host2 = status.findHost("192.168.66.132");
		Assert.assertEquals(34, host2.getProgress());
		Assert.assertEquals("step1", host2.getCurrentStep());
		Assert.assertEquals(2, host2.getOffset());
		Assert.assertEquals("Second segment\r\n", host2.getLog());

		HostModel host3 = status.findHost("192.168.66.133");
		Assert.assertNull(host3);
	}
}
