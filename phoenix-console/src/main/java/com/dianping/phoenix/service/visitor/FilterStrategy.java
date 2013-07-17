package com.dianping.phoenix.service.visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.dianping.phoenix.agent.resource.IVisitor;
import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Kernel;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.console.page.home.Payload;
import com.dianping.phoenix.console.page.home.VersionComparator;

public class FilterStrategy implements IVisitor {
	private static final VersionComparator m_comparator = new VersionComparator();

	private List<String> m_dependencies;
	private List<String> m_operators;
	private List<String> m_versions;
	private List<String> m_joints;

	private List<Set<String>> m_rules;
	private Set<String> m_strategy = new HashSet<String>();
	private Resource m_resource;
	private Host m_currentHost;
	private boolean m_emptyFilter;

	public FilterStrategy(Resource resource, Payload payload) {
		m_resource = resource;
		fillListWithoutBlankElements(m_dependencies = new ArrayList<String>(), payload.getDependencies());
		if (m_dependencies.size() > 0) {
			m_rules = new ArrayList<Set<String>>();
			fillListWithoutBlankElements(m_operators = new ArrayList<String>(), payload.getOperators());
			fillListWithoutBlankElements(m_versions = new ArrayList<String>(), payload.getVersions());
			fillListWithoutBlankElements(m_joints = new ArrayList<String>(), payload.getJoints());
			for (int idx = 0; idx < m_dependencies.size(); idx++) {
				m_rules.add(new HashSet<String>());
			}
		} else {
			m_emptyFilter = true;
		}

		resource.accept(this);
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
	public void visitApp(App app) {
		if (app.getLibs() != null) {
			for (Lib lib : app.getLibs()) {
				lib.accept(this);
			}
		}
		if (app.getKernel() != null && app.getKernel().getLibs() != null) {
			for (Lib lib : app.getLibs()) {
				lib.accept(this);
			}
		}
	}

	@Override
	public void visitContainer(Container container) {
		// ignore it
	}

	@Override
	public void visitDomain(Domain domain) {
		if (domain.getHosts() != null) {
			for (Host host : domain.getHosts().values()) {
				m_currentHost = host;
				host.accept(this);
			}
		}
	}

	@Override
	public void visitHost(Host host) {
		if (host.getContainer() != null && host.getContainer().getApps() != null) {
			for (App app : host.getContainer().getApps()) {
				app.accept(this);
			}
		}
	}

	@Override
	public void visitKernel(Kernel kernel) {
		// ignore it
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
	public void visitPhoenixAgent(PhoenixAgent phoenixAgent) {
		// ignore it
	}

	@Override
	public void visitProduct(Product product) {
		if (product.getDomains() != null) {
			for (Domain domain : product.getDomains().values()) {
				domain.accept(this);
			}
		}
	}

	@Override
	public void visitResource(Resource resource) {
		if (m_dependencies.size() == 0 || resource.getProducts() == null) {
			m_strategy = new HashSet<String>();
			return;
		}
		for (Product product : resource.getProducts().values()) {
			product.accept(this);
		}
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

	public boolean isEmptyFilter() {
		return m_emptyFilter;
	}
}
