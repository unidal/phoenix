package com.dianping.kernel.plugin;

public interface WebappPatcher {
	public void applyKernelWebXml() throws Exception;

	public void mergeWebResources() throws Exception;
}
