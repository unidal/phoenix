package com.dianping.phoenix.agent.core;

import com.dianping.phoenix.agent.response.entity.Response;

public interface ContainerManager {
    Response reportContainerStatus() throws Exception;

    void attachContainerLoader(String domain, String version) throws Exception;

    void detachContainerLoader(String domain) throws Exception;
}
