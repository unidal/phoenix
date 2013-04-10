package com.dianping.phoenix.configure;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.phoenix.router.model.entity.RouterRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {

	private String routerConfigFile = "/data/appdatas/phoenix/router-rules.xml";
	private RouterRules routerRules;

	@Override
	public void initialize() throws InitializationException {
		try {
			File file = new File(routerConfigFile);

			if (file.isFile()) {
				String content = Files.forIO().readFrom(file, "utf-8");
				routerRules = DefaultSaxParser.parse(content);
			} else {
				routerRules = new RouterRules();
			}
		} catch (Exception e) {
			throw new InitializationException(
					String.format("Unable to load configuration file(%s)!", routerConfigFile), e);
		}
	}

	public RouterRules getRouterRules() {
		return routerRules;
	}

}
