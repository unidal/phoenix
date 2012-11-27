package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public class DeployTaskProcessor extends AbstractSerialTaskProcessor<DeployTask> {

	private final static Logger logger = Logger.getLogger(DeployTaskProcessor.class);

	private final static String TOMCAT_LOADER_CLASS = "com.dianping.phoenix.bootstrap.Tomcat6WebappLoader";
	private final static String JBOSS_LOADER_CLASS = "com.dianping.phoenix.bootstrap.Jboss4WebappLoader";

	@Inject
	private TransactionManager txMgr;
	@Inject
	DeployStepContext ctx;

	enum ContainerType {
		TOMCAT, JBOSS
	}

	private ContainerType containerType;
	private String containerInstallPath;
	private String warRoot = "/data/webapps/";
	private String kernelRootPattern = "/data/webapps/phoenix-kernel/%s";
	private String domainDocBaseFeaturePattern = "/%s/current";

	public DeployTaskProcessor() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("container.properties");
		if (in == null) {
			String msg = "container.properties not found";
			throw new RuntimeException(msg);
		}
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			String msg = "error reading container.properties";
			logger.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		containerInstallPath = props.getProperty("containerInstallPath", "/");
		warRoot = props.getProperty("warRoot", warRoot);
		kernelRootPattern = props.getProperty("kernelRootPattern", kernelRootPattern);
		domainDocBaseFeaturePattern = props.getProperty("domainDocBaseFeaturePattern", domainDocBaseFeaturePattern);

		logger.info("containerInstallPath: " + containerInstallPath);
		logger.info("warRoot: " + warRoot);
		logger.info("kernelRootPattern: " + kernelRootPattern);
		logger.info("domainDocBaseFeaturePattern: " + domainDocBaseFeaturePattern);

		File startupSh = new File(containerInstallPath + "/bin/startup.sh");
		File runSh = new File(containerInstallPath + "/bin/run.sh");
		if (startupSh.exists()) {
			containerType = ContainerType.TOMCAT;
		} else if (runSh.exists()) {
			containerType = ContainerType.JBOSS;
		} else {
			throw new RuntimeException(
					"container_install_path in container.properties does not have a valid tomcat or jboss installation");
		}

		logger.info("containerType: " + containerType);
	}

	@Override
	protected void doProcess(final Transaction tx) throws IOException {
		logger.info("start processing " + tx);
		try {
			tx.setStatus(Status.PROCESSING);
			txMgr.saveTransaction(tx);
			tx.setStatus(innerProcess(tx));
		} catch (Exception e) {
			tx.setStatus(Status.FAILED);
			eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), e.getMessage(), Status.FAILED));
		} finally {
			txMgr.saveTransaction(tx);
			logger.info("end processing " + tx);
		}
	}

	private Status innerProcess(final Transaction tx) throws IOException {

		DeployTask task = (DeployTask) tx.getTask();
		String domain = task.getDomain();

		eventTrackerChain.onEvent(new MessageEvent(tx.getTxId(), String.format("updating %s to version %s", domain,
				task.getKernelVersion())));
		OutputStream stdOut = txMgr.getLogOutputStream(tx.getTxId());
		Status exitStatus = Status.SUCCESS;
		try {
			exitStatus = updateKernel(domain, task.getKernelVersion(), stdOut);
		} catch (Exception e) {
			logger.error("error update kernel", e);
			exitStatus = Status.FAILED;
		} finally {
			IOUtil.close(stdOut);
			eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), "", exitStatus));
		}
		return exitStatus;
	}

	private Status updateKernel(String domain, String kernelVersion, OutputStream stdOut) throws Exception {
		String container = null;
		String loaderClass = null;
		File serverXml = null;
		if (containerType == ContainerType.TOMCAT) {
			container = "tomcat";
			loaderClass = TOMCAT_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/conf/server.xml");
		} else {
			container = "jboss";
			loaderClass = JBOSS_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/server/default/deploy/jboss-web.deployer/server.xml");
		}
		ctx.setContainer(container);
		ctx.setDomain(domain);
		ctx.setDomainDocBaseFeaturePattern(domainDocBaseFeaturePattern);
		ctx.setKernelRootPattern(kernelRootPattern);
		ctx.setLoaderClass(loaderClass);
		ctx.setKernelVersion(kernelVersion);
		ctx.setOut(stdOut);
		ctx.setServerXml(serverXml);
		DeployStep.execute(ctx);
		return ctx.getStatus();
	}

	@Override
	public boolean cancel(TransactionId txId) {
		ctx.kill(txId);
		return true;
	}

	@Override
	public Class<DeployTask> handle() {
		return DeployTask.class;
	}

}
