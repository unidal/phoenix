package com.dianping.kernel.inspect;

import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "inspect", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.kernel.inspect.page.home.Handler.class,

com.dianping.kernel.inspect.page.classpath.Handler.class,

com.dianping.kernel.inspect.page.description.Handler.class
})
public class InspectModule extends AbstractModule {

}
