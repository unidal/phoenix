package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class DeployStepContext implements DeployStep.Context {

	@Inject
	private ScriptExecutor scriptExecutor;

	private DeployStep step;
	private Status status;
	private OutputStream out;
	private String domain;
	private String kernelVersion;
	private String container;
	private File serverXml;
	private String loaderClass;
	private String kernelDocBasePattern;
	private String domainDocBaseFeaturePattern;

	@Override
	public int runShellCmd(DeployStep deployStep) throws Exception {
		writeHeader(deployStep, null);
		String script = jointShellCmd(domain, kernelVersion, container, deployStep);
		int exitCode = runShellCmd(script);
		writeLogChunkSeparator();
		return exitCode;
	}

	@Override
	public void setStep(DeployStep step) {
		this.step = step;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	private String jointShellCmd(String domain, String newVersion, String container, DeployStep postStep) {
		return String.format("%s -x \"%s\" -c \"%s\" -d \"%s\" -v \"%s\" -f \"%s\"", getScriptPath(), serverXml,
				container, domain, newVersion, postStep);
	}

	@Override
	public void writeLogChunkSeparator() throws IOException {
		out.write("--9ed2b78c112fbd17a8511812c554da62941629a8--\r\n".getBytes("ascii"));
	}

	@Override
	public void writeLogChunkTerminator() throws IOException {
		out.write("--255220d51dc7fb4aacddadedfe252a346da267d4--\r\n".getBytes("ascii"));
	}

	@Override
	public void writeHeader(DeployStep curStep, String status) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("Progress: ");
		sb.append(curStep.getProgressInfo());
		if (status != null) {
			sb.append("\r\n");
			sb.append("Status: ");
			sb.append(status);
		}

		sb.append("\r\n\r\n");

		out.write(sb.toString().getBytes());
	}

	@Override
	public void injectPhoenixContextLoader() throws Exception {
		if (serverXml == null || !serverXml.exists()) {
			throw new RuntimeException("container server.xml not found");
		}

		File kernelDocBase = new File(String.format(kernelDocBasePattern, domain, kernelVersion));
		String domainDocBasePattern = String.format(domainDocBaseFeaturePattern, domain);
		ServerXmlUtil.attachPhoenixContextLoader(serverXml, domainDocBasePattern, loaderClass, kernelDocBase);
	}

	private String getScriptPath() {
		URL scriptUrl = this.getClass().getClassLoader().getResource("agent.sh");
		if (scriptUrl == null) {
			throw new RuntimeException("agent.sh not found");
		}
		return scriptUrl.getPath();
	}

	public int runShellCmd(String script) throws IOException {
		return scriptExecutor.exec(script, out, out);
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setKernelVersion(String kernelVersion) {
		this.kernelVersion = kernelVersion;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public void setServerXml(File serverXml) {
		this.serverXml = serverXml;
	}

	public void setLoaderClass(String loaderClass) {
		this.loaderClass = loaderClass;
	}

	public void setKernelDocBasePattern(String kernelDocBasePattern) {
		this.kernelDocBasePattern = kernelDocBasePattern;
	}

	public void setDomainDocBaseFeaturePattern(String domainDocBaseFeaturePattern) {
		this.domainDocBaseFeaturePattern = domainDocBaseFeaturePattern;
	}

	@Override
	public void kill(TransactionId txId) {
		scriptExecutor.kill();
	}

	@Override
	public DeployStep getStep() {
		return step;
	}

}
