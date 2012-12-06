package com.dianping.phoenix.console.page.version;

import java.util.List;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Version;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Version> m_versions;
	
	private String m_creatingVersion;
	
	private String m_logcontent;
	
	private int m_index = -1;

	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public List<Version> getVersions() {
		return m_versions;
	}

	public void setVersions(List<Version> versions) {
		m_versions = versions;
	}

	public String getCreatingVersion() {
		return m_creatingVersion;
	}

	public void setCreatingVersion(String creatingVersion) {
		m_creatingVersion = creatingVersion;
	}

	public void setLogcontent(String logcontent) {
		m_logcontent = logcontent;
	}

	public String getLogcontent() {
		return m_logcontent;
	}

	public void setIndex(int index) {
		m_index = index;
	}

	public int getIndex() {
		return m_index;
	}
}
