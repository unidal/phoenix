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
	private long lastModiTime = Long.MIN_VALUE;

	@Override
	public void initialize() throws InitializationException {
		loadConfigFile();
		startReloadThread();
	}

	private void startReloadThread() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(10 * 1000);
						loadConfigFile();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}.start();
	}

	private void loadConfigFile() throws InitializationException {
		File file = new File(routerConfigFile);
		try {
			if (file.isFile()) {
				if (file.lastModified() > lastModiTime) {
					lastModiTime = file.lastModified();
					String content = Files.forIO().readFrom(file, "utf-8");
					routerRules = DefaultSaxParser.parse(content);
				}
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
