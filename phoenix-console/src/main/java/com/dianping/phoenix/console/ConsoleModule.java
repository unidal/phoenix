package com.dianping.phoenix.console;

import com.site.web.mvc.AbstractModule;
import com.site.web.mvc.annotation.ModuleMeta;
import com.site.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "phoenix", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.phoenix.console.page.home.Handler.class,

com.dianping.phoenix.console.page.deploy.Handler.class
})
public class ConsoleModule extends AbstractModule {

}
