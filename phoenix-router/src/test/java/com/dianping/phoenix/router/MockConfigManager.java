package com.dianping.phoenix.router;

import java.io.InputStream;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.model.entity.RouterRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class MockConfigManager extends ConfigManager {

	private RouterRules routerRules;

	@Override
	public void initialize() throws InitializationException {
		String mockConfigPath = "model/router-rules.xml";
		try {

			InputStream in = this.getClass().getResourceAsStream(mockConfigPath);
			if (in != null) {
				String content = Files.forIO().readFrom(in, "utf-8");
				routerRules = DefaultSaxParser.parse(content);
			} else {
				routerRules = new RouterRules();
			}
		} catch (Exception e) {
			throw new InitializationException(String.format("Unable to load configuration file(%s)!", mockConfigPath),
					e);
		}
	}

	@Override
	public RouterRules getRouterRules() {
		return routerRules;
	}

}
