package com.dianping.phoenix.deploy.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.DeployType;
import com.dianping.phoenix.deploy.agent.AgentListener;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class RolloutContextTest extends DefaultDeployExecutor{

	@Test
	public void testOpenUrlReconnect() throws IOException {
		String host = "127.0.0.1";
		String warType = "";
		DeployModel model = new DeployModel();
		AgentListener listener = mock(AgentListener.class);
		ControllerTask controller = mock(ControllerTask.class);
		ConfigManager configManager = mock(ConfigManager.class);
		when(controller.getConfigManager()).thenReturn(configManager );
		RolloutContext ctx = new RolloutContext(controller, listener, model, DeployType.get(warType), host);
		ctx.openUrl("?op=log&");
	}
	
}
