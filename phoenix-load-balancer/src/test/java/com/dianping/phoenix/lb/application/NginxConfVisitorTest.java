package com.dianping.phoenix.lb.application;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.dianping.phoenix.lb.domain.model.Node;
import com.dianping.phoenix.lb.domain.model.VirtualServer;
import com.dianping.phoenix.lb.domain.model.dispatch.DispatchAction;
import com.dianping.phoenix.lb.domain.model.dispatch.DispatchStep;
import com.dianping.phoenix.lb.domain.model.dispatch.Forward;
import com.dianping.phoenix.lb.domain.model.dispatch.UrlMatcher;
import com.dianping.phoenix.lb.domain.model.dispatch.UrlMatcher.MatchType;
import com.dianping.phoenix.lb.domain.model.pool.Hash;
import com.dianping.phoenix.lb.domain.model.pool.Hash.Method;
import com.dianping.phoenix.lb.domain.model.pool.Hash.Target;
import com.dianping.phoenix.lb.domain.model.pool.LoadBalancingMethod;
import com.dianping.phoenix.lb.domain.model.pool.Pool;
import com.dianping.phoenix.lb.domain.model.pool.PoolMember;

public class NginxConfVisitorTest {

	@Test
	public void test() {
		List<PoolMember> members = new ArrayList<PoolMember>();
		Node node = new Node("1.1.1.1", "node1");
		PoolMember member = new PoolMember(node, 80, 1);
		members.add(member);
		LoadBalancingMethod lbMethod = new Hash(Target.URI, Method.CRC32);
		Pool defaultPool = new Pool("Pool.Default", members, lbMethod, 30);

		VirtualServer vs = new VirtualServer("VirtualServer.Beta", 80, defaultPool);
		List<DispatchAction> dispatchActions = new ArrayList<DispatchAction>();
		UrlMatcher urlMatcher = new UrlMatcher(MatchType.RegexCaseInsensitive, ".png");
		List<DispatchStep> steps = new ArrayList<DispatchStep>();
		Pool pool = new Pool("Pool.A", members, lbMethod, 30);
		DispatchStep step = new Forward(pool);
		steps.add(step);
		DispatchAction action = new DispatchAction(urlMatcher, steps);
		dispatchActions.add(action);
		vs.setDispatchActions(dispatchActions);

		NginxConfVisitor visitor = new NginxConfVisitor();
		StringBuilder sb = new StringBuilder();
		vs.accept(visitor);
		visitor.getSb();
	}

}
