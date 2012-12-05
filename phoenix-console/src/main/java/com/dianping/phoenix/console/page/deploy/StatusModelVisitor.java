package com.dianping.phoenix.console.page.deploy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;
import com.dianping.phoenix.deploy.model.transform.BaseVisitor;

class StatusModelVisitor extends BaseVisitor {
	private DeployModel m_model;

	private Map<String, Integer> m_map;

	public StatusModelVisitor(Map<String, Integer> map) {
		m_map = map != null ? map : Collections.<String, Integer> emptyMap();
	}

	private String escape(String str) {
		int len = str.length();
		StringBuilder sb = new StringBuilder(len + 32);

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\r':
				break;
			case '\n':
				sb.append("<br>");
				break;
			default:
				sb.append(ch);
				break;
			}
		}

		return sb.toString();
	}

	public DeployModel getModel() {
		return m_model;
	}

	@Override
	public void visitDeploy(DeployModel other) {
		m_model = new DeployModel();
		m_model.mergeAttributes(other);

		super.visitDeploy(other);

		HostModel summary = m_model.findHost("summary");
		Set<String> set = new HashSet<String>();
		int size = other.getHosts().size();
		int progress = 0;

		if (summary == null) { // it always has summary
			Integer offset = m_map.get("summary");

			summary = new HostModel("summary");
			summary.setOffset(offset == null ? 0 : offset);
			m_model.addHost(summary);
		}

		for (HostModel host : other.getHosts().values()) {
			if (!host.getIp().equals("summary")) {
				int p = 0;
				String status = null;

				for (SegmentModel segment : host.getSegments()) {
					if (segment.getTotalTicks() > 0) {
						p = segment.getCurrentTicks() * 100 / segment.getTotalTicks();
					}

					if (segment.getStatus() != null) {
						status = segment.getStatus();
					}

				}

				progress += p;

				if (status == null) {
					set.add("doing");
				} else {
					set.add(status);
				}
			}
		}

		String status = null;

		if (set.contains("doing")) {
			status = "doing";
		} else if (set.contains("failed")) {
			status = "failed";
		} else if (set.contains("cancelled")) {
			status = "cancelled";
		} else if (set.contains("successful")) {
			status = "successful";
		} else {
			status = "pending";
		}

		summary.setProgress(progress / (size > 1 ? size - 1 : 1));
		summary.setStatus(status);
		m_model.setStatus(status);
	}

	@Override
	public void visitHost(HostModel other) {
		String ip = other.getIp();
		List<SegmentModel> segments = other.getSegments();
		int size = segments.size();
		Integer index = m_map.get(ip);
		StringBuilder sb = new StringBuilder(1024);
		int progress = 0;
		String step = null;
		String status = null;

		if (index == null) {
			index = 0;
		}

		for (int i = 0; i < index && i < size; i++) {
			SegmentModel segment = segments.get(i);

			if (segment.getStep() != null) {
				step = segment.getStep();
			}
		}

		for (int i = index; i < size; i++) {
			SegmentModel segment = segments.get(i);

			if (segment.getTotalTicks() > 0) {
				progress = segment.getCurrentTicks() * 100 / segment.getTotalTicks();
			}

			if (segment.getStep() != null) {
				step = segment.getStep();
			}

			if (segment.getStatus() != null) {
				status = segment.getStatus();
			}

			String text = segment.getText();

			if (text != null) {
				sb.append(text.trim()).append("\r\n");
			}
		}

		if (sb.length() > 0 || status != null || step != null || progress > 0) {
			HostModel host = new HostModel().setIp(ip);

			host.setProgress(progress);
			host.setCurrentStep(step);
			host.setStatus(status);
			host.setLog(escape(sb.toString()));
			host.setOffset(size);
			m_model.addHost(host);
		}
	}
}