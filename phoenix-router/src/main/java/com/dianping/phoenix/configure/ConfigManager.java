package com.dianping.phoenix.configure;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.phoenix.router.model.entity.RouterRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {

	private final static Logger log = Logger.getLogger(ConfigManager.class);
	private final static String glocalRouterConfigFile = "/data/appdatas/phoenix/router-rules.xml";
	private final static String classpathRouterConfigFile = "/router-rules.xml";
	private RouterRules routerRules;
	private long lastModiTime = Long.MIN_VALUE;

	@Override
	public void initialize() throws InitializationException {
		loadClasspathConfigFile();
//		loadGlobalConfigFile();
		// startReloadThread();
	}

	private void startReloadThread() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(10 * 1000);
						loadGlobalConfigFile();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}.start();
	}

	private void loadClasspathConfigFile() throws InitializationException {
		InputStream in = this.getClass().getResourceAsStream(classpathRouterConfigFile);
		try {
			if (in != null) {
				log.info(String.format("reading router-rules.xml from classpath %s", classpathRouterConfigFile));
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

	private void loadGlobalConfigFile() throws InitializationException {
		File file = new File(glocalRouterConfigFile);
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
			throw new InitializationException(String.format("Unable to load configuration file(%s)!",
					glocalRouterConfigFile), e);
		}
	}

	public RouterRules getRouterRules() {
		return routerRules;
	}

}
