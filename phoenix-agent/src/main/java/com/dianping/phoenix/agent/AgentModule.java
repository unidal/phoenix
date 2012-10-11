package com.dianping.phoenix.agent;

import com.site.web.mvc.AbstractModule;
import com.site.web.mvc.annotation.ModuleMeta;
import com.site.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "phoenix", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.phoenix.agent.page.home.Handler.class,

com.dianping.phoenix.agent.page.deploy.Handler.class
})
public class AgentModule extends AbstractModule {

}
