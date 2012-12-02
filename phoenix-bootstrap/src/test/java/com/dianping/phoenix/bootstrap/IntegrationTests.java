package com.dianping.phoenix.bootstrap;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.phoenix.spi.internal.ClasspathBuilderTest;

/**
 * This integration tests should be run after command "mvn package".
 */
@RunWith(Suite.class)
@SuiteClasses({

ClasspathBuilderTest.class

})
public class IntegrationTests {

}
