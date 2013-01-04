package com.dianping.service.internal;

import java.io.File;
import java.util.List;

import org.unidal.helper.Files;
import org.unidal.helper.Splitters;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Server;
import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.annotation.Property;

public class DefaultCatProvider implements ServiceProvider<Cat> {
	@Property
	private String m_servers;

	@Override
	public void destroyService(Cat cat) {
		// do nothing here
	}

	@Override
	public Class<Cat> getServiceType() {
		return Cat.class;
	}

	private ClientConfig makeClientConfig() {
		ClientConfig config = new ClientConfig();
		List<String> servers = Splitters.by(',').noEmptyItem().trim().split(m_servers);

		for (String server : servers) {
			int pos = server.indexOf(':');

			if (pos > 0) {
				String ip = server.substring(0, pos);
				int port = Integer.parseInt(server.substring(pos + 1));

				config.addServer(new Server(ip).setPort(port));
			} else {
				config.addServer(new Server(server));
			}
		}

		return config;
	}

	@Override
	public Cat makeService(ServiceBinding binding) throws Exception {
		String configuration = binding.getConfiguration();

		if (configuration != null) {
			ClientConfig config = makeClientConfig();
			File configFile = File.createTempFile("cat-", ".tmp");

			Files.forIO().writeTo(configFile, config.toString());
			Cat.initialize(configFile);
		}

		return Cat.getInstance();
	}
}
