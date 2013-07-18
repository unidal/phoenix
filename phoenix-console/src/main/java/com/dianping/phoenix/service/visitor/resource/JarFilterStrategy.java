package com.dianping.phoenix.service.visitor.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.console.page.home.Payload;
import com.dianping.phoenix.service.VersionComparator;

public class JarFilterStrategy extends BaseResourceVisitor implements FilterStrategy {
	private static final VersionComparator m_comparator = new VersionComparator();

	private List<String> m_dependencies;
	private List<String> m_operators;
	private List<String> m_versions;
	private List<String> m_joints;

	private List<Set<String>> m_rules = new ArrayList<Set<String>>();
	private Set<String> m_strategy;
	private Resource m_resource;
	private Host m_currentHost;

	public JarFilterStrategy(Resource resource, Payload payload) {
		m_resource = resource;
		fillListWithoutBlankElements(m_dependencies = new ArrayList<String>(), payload.getDependencies());
		if (m_dependencies.size() > 0) {
			fillListWithoutBlankElements(m_operators = new ArrayList<String>(), payload.getOperators());
			fillListWithoutBlankElements(m_versions = new ArrayList<String>(), payload.getVersions());
			fillListWithoutBlankElements(m_joints = new ArrayList<String>(), payload.getJoints());
			for (int idx = 0; idx < m_dependencies.size(); m_rules.add(new HashSet<String>()), idx++);
			visitResource(m_resource);
			m_strategy = generateStrategy();
		}
	}

	private void fillListWithoutBlankElements(List<String> target, List<String> source) {
		for (String str : source) {
			if (str.trim().length() != 0) {
				target.add(str);
			}
		}
	}

	private boolean isMatchedRule(String operator, String left, String right) {
		int res = m_comparator.compare(left, right);
		if ((operator.equals("<") && res < 0) || (operator.equals("=") && res == 0)
				|| (operator.equals(">") && res > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public void visitHost(Host host) {
		m_currentHost = host;
		super.visitHost(host);
	}

	@Override
	public void visitLib(Lib lib) {
		for (int idx = 0; idx < m_dependencies.size(); idx++) {
			if (lib.getArtifactId().equals(m_dependencies.get(idx))) {
				if (isMatchedRule(m_operators.get(idx), lib.getVersion(), m_versions.get(idx))) {
					if (m_currentHost != null) {
						m_rules.get(idx).add(m_currentHost.getIp());
					}
				}
			}
		}
	}

	@Override
	public void visitResource(Resource resource) {
		if (m_dependencies.size() == 0) {
			m_strategy = new HashSet<String>();
			return;
		}
		super.visitResource(resource);
		m_strategy = generateStrategy();
	}

	private Set<String> generateStrategy() {
		Set<String> set;
		if (m_joints.size() == 0) {
			set = m_rules.get(0);
		} else {
			set = new HashSet<String>();
			Stack<Set<String>> setstack = new Stack<Set<String>>();
			Stack<String> opstack = new Stack<String>();

			setstack.push(m_rules.get(0));
			opstack.push(m_joints.get(0));

			for (int idx = 1; idx < m_rules.size(); idx++) {
				Set<String> newset = m_rules.get(idx);
				String newop = idx == m_joints.size() ? null : m_joints.get(idx);
				renewStack(setstack, opstack, newset, newop);
			}

			if (setstack.size() == 1 && opstack.size() == 0) {
				set = setstack.pop();
			}
		}
		return set;
	}

	private void renewStack(Stack<Set<String>> setstack, Stack<String> opstack, Set<String> newset, String newop) {
		String topop = opstack.size() > 0 ? opstack.peek() : null;
		switch (compareOp(newop, topop)) {
			case 1 :
				opstack.push(newop);
				setstack.push(newset);
				return;
			case -1 :
				newset = compute(newset, opstack.size() > 0 ? opstack.pop() : null,
						setstack.size() > 0 ? setstack.pop() : null);
				renewStack(setstack, opstack, newset, newop);
				break;
			case 0 :
				setstack.push(newset);
				break;
			default :
				break;
		}
	}

	private Set<String> compute(Set<String> left, String op, Set<String> right) {
		return "and".equals(op) ? intersect(left, right) : ("or".equals(op) ? union(left, right) : null);
	}

	private int compareOp(String left, String right) {
		if (left == null && right == null) {
			return 0;
		}
		if (left != null && right != null && left.equals(right)) {
			return 1;
		}
		if (left == null || right == null) {
			return left == null ? -1 : 1;
		}
		return "and".equals(left) ? 1 : -1;
	}

	private Set<String> union(Set<String> left, Set<String> right) {
		left.addAll(right);
		return left;
	}

	private Set<String> intersect(Set<String> left, Set<String> right) {
		left.retainAll(right);
		return left;
	}

	public Set<String> getStrategy() {
		return m_strategy;
	}

	public Resource getResource() {
		return m_resource;
	}
}
