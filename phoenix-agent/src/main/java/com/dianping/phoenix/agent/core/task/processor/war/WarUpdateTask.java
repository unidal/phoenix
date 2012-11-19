package com.dianping.phoenix.agent.core.task.processor.war;

import com.dianping.phoenix.agent.core.task.AbstractTask;

public class WarUpdateTask extends AbstractTask {

	private Artifact artifactToUpdate;
	private String newVersion;

	public WarUpdateTask(Artifact artifactToUpdate, String newVersion) {
		this.artifactToUpdate = artifactToUpdate;
		this.newVersion = newVersion;
	}

	public Artifact getArtifactToUpdate() {
		return artifactToUpdate;
	}

	public void setArtifactToUpdate(Artifact artifactToUpdate) {
		this.artifactToUpdate = artifactToUpdate;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}

}
