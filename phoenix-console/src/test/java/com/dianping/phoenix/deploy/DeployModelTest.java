package com.dianping.phoenix.deploy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.transform.DefaultSaxParser;

public class DeployModelTest {
	@Test
	public void testModel() throws SAXException, IOException {
		DeployModel deploy = DefaultSaxParser.parse(getClass().getResourceAsStream("deploy.xml"));

		Assert.assertEquals(2, deploy.getHosts().size());
		Assert.assertEquals(2, deploy.findHost("192.168.66.133").getSegments().size());
	}
}
