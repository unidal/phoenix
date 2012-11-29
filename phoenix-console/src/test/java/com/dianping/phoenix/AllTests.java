package com.dianping.phoenix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.phoenix.configure.ConfigManagerTest;
import com.dianping.phoenix.deploy.DeployModelTest;
import com.dianping.phoenix.deploy.DeployPolicyExecutorTest;
import com.dianping.phoenix.deploy.DeployStateTest;
import com.dianping.phoenix.deploy.ProjectModelTest;

@RunWith(Suite.class)
@SuiteClasses({

ConfigManagerTest.class,

ProjectModelTest.class,

DeployModelTest.class,

DeployStateTest.class,

DeployPolicyExecutorTest.class

})
public class AllTests {

}
