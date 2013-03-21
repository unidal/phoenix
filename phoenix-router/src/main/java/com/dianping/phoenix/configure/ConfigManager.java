package com.dianping.phoenix.configure;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;

import com.dianping.phoenix.router.model.entity.SerializedRule;
import com.dianping.phoenix.router.model.entity.SerializedRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {

	private String configFile = "/data/appdatas/phoenix/router.xml";
	private SerializedRules serializedRules;

	@Override
	public void initialize() throws InitializationException {
		try {
			File file = new File(configFile);

			if (file.isFile()) {
				String content = Files.forIO().readFrom(file, "utf-8");
				serializedRules = DefaultSaxParser.parse(content);
			} else {
				serializedRules = new SerializedRules();
			}
		} catch (Exception e) {
			throw new InitializationException(String.format("Unable to load configuration file(%s)!", configFile), e);
		}
	}

	public List<SerializedRule> getSerializedRules() {
		return serializedRules.getSerializedRules();
	}

}
