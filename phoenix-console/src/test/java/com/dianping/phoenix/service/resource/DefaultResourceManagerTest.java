package com.dianping.phoenix.service.resource;

import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.agent.resource.transform.DefaultXmlBuilder;

public class DefaultResourceManagerTest {

	@Test
	public void test() throws Exception {
		File f = new File("/Users/song/Desktop/test.xml");
		if (!f.exists()) {
			f.createNewFile();
		}
		Resource resource = new Resource();
		Product product = new Product();
		product.setName("主站");
		Domain domain = new Domain();
		domain.setName("user-web");
		product.addDomain(domain);
		domain = new Domain();
		domain.setName("shop-web");
		product.addDomain(domain);
		domain = new Domain();
		domain.setName("shop-service");
		product.addDomain(domain);
		resource.addProduct(product);
		product = new Product();
		product.setName("信息产品");
		resource.addProduct(product);
		product = new Product();
		product.setName("团购");
		resource.addProduct(product);
		product = new Product();
		product.setName("基础平台");
		resource.addProduct(product);
		product = new Product();
		product.setName("手机");
		resource.addProduct(product);
		product = new Product();
		product.setName("架构");
		resource.addProduct(product);
		FileWriter writer = new FileWriter(f);
		String str = new DefaultXmlBuilder().buildXml(resource);
		
		writer.write(str);
		writer.close();
	}

}
