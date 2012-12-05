package com.dianping.phoenix.service;

import java.io.File;
import java.util.Collection;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import com.dianping.phoenix.console.page.version.VersionContext;

public interface GitService {
	void clearWorkingDir(VersionContext context) throws Exception;

	public ObjectId commit(VersionContext context) throws Exception;

	public File getWorkingDir();

	public Collection<Ref> lsRemote() throws Exception;

	void pull(VersionContext context) throws Exception;

	void push(VersionContext context) throws Exception;

	public void removeTag(VersionContext context) throws Exception;

	void setup(VersionContext context) throws Exception;
}
