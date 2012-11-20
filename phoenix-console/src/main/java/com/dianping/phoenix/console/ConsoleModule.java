package com.dianping.phoenix.console;

import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "console", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.phoenix.console.page.home.Handler.class,

com.dianping.phoenix.console.page.deploy.Handler.class,

com.dianping.phoenix.console.page.version.Handler.class
})
public class ConsoleModule extends AbstractModule {

}
