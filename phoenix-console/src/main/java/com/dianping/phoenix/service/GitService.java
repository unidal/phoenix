package com.dianping.phoenix.service;

import java.io.File;
import java.util.Collection;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

public interface GitService {
	public void clearWorkingDir() throws Exception;

	public ObjectId commit(String tag, String description) throws Exception;

	public File getWorkingDir();

	public void pull() throws Exception;

	public void push() throws Exception;

	public void removeTag(String tag) throws Exception;

	public void setup() throws Exception;

	public Collection<Ref> lsRemote() throws Exception;
}
