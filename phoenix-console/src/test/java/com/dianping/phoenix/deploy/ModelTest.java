package com.dianping.phoenix.deploy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.deploy.entity.Deploy;
import com.dianping.phoenix.deploy.transform.DefaultSaxParser;

public class ModelTest {
	@Test
	public void testModel() throws SAXException, IOException {
		Deploy deploy = DefaultSaxParser.parse(getClass().getResourceAsStream("deploy.xml"));

		Assert.assertEquals(2, deploy.getProjects().size());
		Assert.assertEquals(6, deploy.findProject("shop-web").getHosts().size());
	}
}
