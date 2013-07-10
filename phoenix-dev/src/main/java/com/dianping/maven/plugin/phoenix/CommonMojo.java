package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.unidal.helper.Files;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Server;

public abstract class CommonMojo extends AbstractMojo {

	protected void setupCat() throws IOException {
		String catServer = System.getProperty("cat_server", "192.168.7.70");
		File configFile = File.createTempFile("cat", "xml");

		ClientConfig config = new ClientConfig();

		config.addServer(new Server(catServer));

		Files.forIO().writeTo(configFile, config.toString());
		Cat.initialize(configFile);
	}
	
}
