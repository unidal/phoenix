package com.dianping.kernel;

public interface WebappPatcher {
	public void applyKernelWebXml() throws Exception;

	public void mergeWebResources() throws Exception;
}
