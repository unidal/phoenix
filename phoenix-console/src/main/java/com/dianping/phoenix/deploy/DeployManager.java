package com.dianping.phoenix.deploy;

import java.util.List;

public interface DeployManager {
	public int deploy(String name, List<String> hosts, DeployPlan plan, String logUri) throws Exception;

	public boolean pauseDeploy(int deployId);

	public boolean continueDeploy(int deployId);

	public boolean cancelRestRollout(int deployId);
}
