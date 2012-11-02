package com.dianping.kernel.inspect;

import com.site.web.mvc.AbstractModule;
import com.site.web.mvc.annotation.ModuleMeta;
import com.site.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "inspect", defaultInboundAction = "home", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.kernel.inspect.page.home.Handler.class,

com.dianping.kernel.inspect.page.classpath.Handler.class,

com.dianping.kernel.inspect.page.descriptor.Handler.class
})
public class InspectModule extends AbstractModule {

}
