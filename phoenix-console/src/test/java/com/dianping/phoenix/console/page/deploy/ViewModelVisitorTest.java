package com.dianping.phoenix.console.page.deploy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.transform.DefaultSaxParser;
import com.site.helper.Files;

public class ViewModelVisitorTest {
	@Test
	public void testVisit() throws SAXException, IOException {
		DeployModel origin = DefaultSaxParser.parse(getClass().getResourceAsStream("view.xml"));
		ViewModelVisitor visitor = new ViewModelVisitor();

		origin.accept(visitor);

		DeployModel model = visitor.getModel();

		Assert.assertEquals(4, model.getHosts().size());
		Assert.assertEquals("summary", model.getHosts().values().iterator().next().getIp());

		HostModel host1 = model.findHost("192.168.66.131");
		Assert.assertEquals(12, host1.getProgress());
		Assert.assertEquals("step1", host1.getCurrentStep());

		HostModel host2 = model.findHost("192.168.66.132");
		Assert.assertEquals(34, host2.getProgress());
		Assert.assertEquals("step1", host2.getCurrentStep());

		HostModel host3 = model.findHost("192.168.66.133");
		Assert.assertEquals(56, host3.getProgress());
		Assert.assertEquals("step2", host3.getCurrentStep());

		String expected = Files.forIO().readFrom(getClass().getResourceAsStream("view-after.xml"), "utf-8");

		Assert.assertEquals(expected.replaceAll("\r",""), model.toString().replaceAll("\r",""));
	}
}
