package com.dianping.phoenix.deploy;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.project.entity.Root;
import com.dianping.phoenix.project.transform.DefaultSaxParser;

public class ProjectModelTest {
	@Test
	public void testModel() throws SAXException, IOException {
		Root deploy = DefaultSaxParser.parse(getClass().getResourceAsStream("project.xml"));

		Assert.assertEquals(3, deploy.getProjects().size());
		Assert.assertEquals(6, deploy.findProject("shop-web").getHosts().size());
	}
}
