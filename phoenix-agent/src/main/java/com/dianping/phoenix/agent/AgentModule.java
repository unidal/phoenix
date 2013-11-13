package com.dianping.phoenix.agent;

import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "agent", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.phoenix.agent.page.home.Handler.class,

com.dianping.phoenix.agent.page.deploy.Handler.class,

com.dianping.phoenix.agent.page.nginx.Handler.class
})
public class AgentModule extends AbstractModule {

}
