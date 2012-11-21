package com.dianping.phoenix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.phoenix.configure.ConfigManagerTest;
import com.dianping.phoenix.deploy.ModelTest;

@RunWith(Suite.class)
@SuiteClasses({

ConfigManagerTest.class,

ModelTest.class

})
public class AllTests {

}
