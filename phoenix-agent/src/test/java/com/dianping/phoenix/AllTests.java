package com.dianping.phoenix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.dianping.phoenix.agent.CharacterReplaceFilterTest;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessorTest;
import com.dianping.phoenix.agent.core.task.processor.kernel.DefaultDeployStepTest;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployWorkflowTest;
import com.dianping.phoenix.agent.core.task.processor.kernel.ServerXmlUtilTest;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.DefaultQaServiceTest;
import com.dianping.phoenix.agent.core.tx.FileBasedTransactionManagerTest;
import com.dianping.phoenix.agent.core.tx.Log4jAppenderTest;
import com.dianping.phoenix.agent.core.tx.LogFormatterTest;

@RunWith(Suite.class)
@SuiteClasses({
	DeployWorkflowTest.class,
	ServerXmlUtilTest.class,
	AbstractSerialTaskProcessorTest.class,
	FileBasedTransactionManagerTest.class,
	CharacterReplaceFilterTest.class,
	DefaultQaServiceTest.class,
	DefaultDeployStepTest.class,
	Log4jAppenderTest.class,
	LogFormatterTest.class
})
public class AllTests {

}
