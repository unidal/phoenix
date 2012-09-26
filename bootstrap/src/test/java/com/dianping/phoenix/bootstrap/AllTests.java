package com.dianping.phoenix.bootstrap;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.phoenix.bootstrap.tomcat6.TomcatBootstrap;

@RunWith(Suite.class)
@SuiteClasses({
	TomcatBootstrap.class
})
public class AllTests {

}
