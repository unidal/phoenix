package com.dianping.kernel.console;

import com.site.web.mvc.AbstractModule;
import com.site.web.mvc.annotation.ModuleMeta;
import com.site.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "console", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.kernel.console.page.home.Handler.class,

com.dianping.kernel.console.page.classpath.Handler.class
})
public class ConsoleModule extends AbstractModule {

}
