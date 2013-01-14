package com.dianping.phoenix.console.page.deploy;

import java.util.HashSet;
import java.util.Set;

import com.dianping.phoenix.deploy.DeployConstant;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;
import com.dianping.phoenix.deploy.model.transform.BaseVisitor;

class ViewModelVisitor extends BaseVisitor {
	private DeployModel m_model;

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

		HostModel summary = other.findHost(DeployConstant.SUMMARY);
		int progress = 0;
		Set<String> set = new HashSet<String>();

		// put it to head
		m_model.addHost(summary);

		for (SegmentModel segment : summary.getSegments()) {
			segment.setEncodedText(escape(segment.getText()));
		}

		for (HostModel host : other.getHosts().values()) {
			if (!host.getIp().equals(DeployConstant.SUMMARY)) {
				visitHost(host);

				HostModel h = m_model.findHost(host.getIp());

				progress += h.getProgress();
				set.add(h.getStatus());
			}
		}

		int size = other.getHosts().size();
		String status = null;

		if (set.contains("deploying")) {
			status = "deploying";
		} else if (set.contains("failed")) {
			if (set.contains("successful")) {
				status = "warning";
			} else {
				status = "failed";
			}
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
		m_model.setPlan(other.getPlan());
	}

	@Override
	public void visitHost(HostModel other) {
		HostModel host = new HostModel(other.getIp());
		int progress = 0;

		host.mergeAttributes(other);

		for (SegmentModel segment : other.getSegments()) {
			if (segment.getTotalTicks() > 0) {
				progress = segment.getCurrentTicks() * 100 / segment.getTotalTicks();
			}

			if (segment.getStep() != null) {
				host.setCurrentStep(segment.getStep());
			}

			if (segment.getStatus() != null) {
				host.setStatus(segment.getStatus());
			}

			if (segment.getText() != null) {
				segment.setEncodedText(escape(segment.getText()));
			}

			host.addSegment(segment);
		}

		if (host.getStatus() == null) {
			host.setStatus("deploying");
		}

		host.setProgress(progress);
		m_model.addHost(host);
	}
}