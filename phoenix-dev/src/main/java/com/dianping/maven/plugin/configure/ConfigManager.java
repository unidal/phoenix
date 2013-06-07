package com.dianping.maven.plugin.configure;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.maven.plugin.phoenix.phoenix.entity.Phoenix;
import com.dianping.maven.plugin.phoenix.phoenix.transform.DefaultSaxParser;


public class ConfigManager implements Initializable {
	
	private final static String m_configFile = "phoenix.xml";
	
	private Phoenix phoenixConfig;

	@Override
	public void initialize() throws InitializationException {
		try {
			File file = new File(m_configFile);

			if (file.isFile()) {
				String content = Files.forIO().readFrom(file, "utf-8");
				phoenixConfig = DefaultSaxParser.parse(content);
			} else {
				phoenixConfig = new Phoenix();
			}
		} catch (Exception e) {
			throw new InitializationException(String.format("Unable to load configuration file(%s)!", m_configFile), e);
		}
	}
	
	private void check() {
		if (phoenixConfig == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		}
	}

	public Phoenix getPhoenixConfig() {
		check();
		return phoenixConfig;
	}
	
	

}
