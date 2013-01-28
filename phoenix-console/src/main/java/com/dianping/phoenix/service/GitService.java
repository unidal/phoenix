package com.dianping.phoenix.service;

import java.io.File;
import java.util.Collection;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;


public interface GitService {
	void clearWorkingDir(GitContext context) throws Exception;

	public ObjectId commit(GitContext context) throws Exception;

	public File getWorkingDir();

	public Collection<Ref> lsRemote() throws Exception;

	void pull(GitContext context) throws Exception;

	void push(GitContext context) throws Exception;

	public void removeTag(GitContext context) throws Exception;

	void setup(GitContext context) throws Exception;
}
