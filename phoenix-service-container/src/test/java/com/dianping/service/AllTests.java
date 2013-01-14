package com.dianping.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.service.logging.Log4jLoggerTest;
import com.dianping.service.logging.PlexusLoggerTest;
import com.dianping.service.mock.MockServiceTest;

@RunWith(Suite.class)
@SuiteClasses({

PlexusLoggerTest.class,

Log4jLoggerTest.class,

MockServiceTest.class

})
public class AllTests {

}
