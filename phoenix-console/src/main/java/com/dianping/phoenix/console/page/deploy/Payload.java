package com.dianping.phoenix.console.page.deploy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Splitters;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

import com.dianping.phoenix.console.ConsolePage;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("id")
	private int m_id;

	@FieldMeta("progress")
	private String m_progress;

	private Map<String, Integer> m_progressMap;

	@FieldMeta("hosts")
	private String m_hosts;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getHosts() {
		return m_hosts;
	}

	public int getId() {
		return m_id;
	}

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public Map<String, Integer> getProgressMap() {
		return m_progressMap;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.VIEW);
	}

	public void setHosts(String hosts) {
		this.m_hosts = hosts;
	}

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.DEPLOY);
	}

	public void setProgress(String progress) {
		m_progress = progress;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.VIEW;
		}

		if (m_progress != null) {
			List<String> parts = Splitters.by(',').noEmptyItem().trim().split(m_progress);

			m_progressMap = new HashMap<String, Integer>(parts.size() * 2);

			try {
				for (String part : parts) {
					int pos = part.indexOf(':');

					if (pos > 0) {
						m_progressMap.put(part.substring(0, pos), Integer.parseInt(part.substring(pos + 1)));
					} else {
						m_progressMap.put(part, 0);
					}
				}
			} catch (NumberFormatException e) {
				ctx.addError("payload.progress", new IllegalArgumentException("Invalid progress: " + m_progress, e));
			}
		}
	}
}
