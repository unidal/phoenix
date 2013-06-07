package com.dianping.phoenix.configure;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.phoenix.router.model.entity.RouterRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {

	private final static Logger log = Logger.getLogger(ConfigManager.class);
	private final static String classpathRouterConfigFile = "/url-rules.xml";
	private RouterRules routerRules;

	@Override
	public void initialize() throws InitializationException {
		loadClasspathConfigFile();
	}

	private void loadClasspathConfigFile() throws InitializationException {
		InputStream in = this.getClass().getResourceAsStream(classpathRouterConfigFile);
		try {
			if (in != null) {
				log.info(String.format("reading url-rules.xml from classpath %s", classpathRouterConfigFile));
				String content = Files.forIO().readFrom(in, "utf-8");
				routerRules = DefaultSaxParser.parse(content);
			} else {
				String msg = String.format("%s not found on classpath", classpathRouterConfigFile);
				log.error(msg);
				throw new RuntimeException(msg);
			}
		} catch (Exception e) {
			throw new InitializationException(String.format("Unable to load configuration file(%s) from classpath!",
					classpathRouterConfigFile), e);
		}
	}

	public RouterRules getRouterRules() {
		return routerRules;
	}

}
