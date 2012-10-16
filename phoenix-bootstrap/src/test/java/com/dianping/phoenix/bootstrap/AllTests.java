package com.dianping.phoenix.bootstrap;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.phoenix.spi.internal.VersionComparatorTest;
import com.dianping.phoenix.spi.internal.VersionParserTest;

@RunWith(Suite.class)
@SuiteClasses({

VersionComparatorTest.class,

VersionParserTest.class,

})
public class AllTests {

}
