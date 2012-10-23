package com.dianping.kernel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.kernel.console.page.classpath.ArtifactResolverTest;

@RunWith(Suite.class)
@SuiteClasses({

SortToolTest.class,

ArtifactResolverTest.class

})
public class AllTests {

}
