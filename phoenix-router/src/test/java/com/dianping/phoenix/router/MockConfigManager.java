package com.dianping.phoenix.router;

import java.io.InputStream;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.model.entity.SerializedRule;
import com.dianping.phoenix.router.model.entity.SerializedRules;
import com.dianping.phoenix.router.model.transform.DefaultSaxParser;

public class MockConfigManager extends ConfigManager {

	private SerializedRules mockSerializedRules;

	@Override
	public void initialize() throws InitializationException {
		InputStream in = this.getClass().getResourceAsStream("serialized-rules.xml");
		try {
			mockSerializedRules = DefaultSaxParser.parse(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<SerializedRule> getSerializedRules() {
		return mockSerializedRules.getSerializedRules();
	}

}
