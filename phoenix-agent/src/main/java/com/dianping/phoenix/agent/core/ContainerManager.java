package com.dianping.phoenix.agent.core;

import java.io.File;

import com.dianping.phoenix.agent.response.entity.Response;

public interface ContainerManager {
	Response reportContainerStatus() throws Exception;

	void attachPhoenixContextLoader(File serverXml, String docBasePattern, String loaderClass, File kernelDocBase)
			throws Exception;

	void attachContainerLoader(String domain, String version) throws Exception;

	void detachPhoenixContextLoader(File serverXml, String docBasePattern) throws Exception;
}
