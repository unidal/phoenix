package com.dianping.phoenix.lb.application;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import com.dianping.phoenix.lb.domain.model.VirtualServer;
import com.dianping.phoenix.lb.domain.model.Visitor;
import com.dianping.phoenix.lb.domain.model.dispatch.DispatchAction;
import com.dianping.phoenix.lb.domain.model.dispatch.Forward;
import com.dianping.phoenix.lb.domain.model.dispatch.Redirect;
import com.dianping.phoenix.lb.domain.model.dispatch.UrlMatcher;
import com.dianping.phoenix.lb.domain.model.pool.Pool;

public class NginxConfVisitor implements Visitor {

	private StringBuilder sb = new StringBuilder();

	List<Upstream> upstreams = new ArrayList<Upstream>();
	List<Location> locations = new ArrayList<Location>();
	Location curLocation;

	public StringBuilder getSb() {
		VelocityContext ctx = new VelocityContext();
		ctx.put("upstreams", upstreams);
		ctx.put("locations", locations);
		ctx.put("DispatchStepToString", Location.class);
		PrintWriter out = new PrintWriter(System.out);
		VelocityEngineManager.INSTANCE.build("nginx.conf.vm", ctx , out);
		out.flush();
		return sb;
	}

	@Override
	public void visit(VirtualServer vs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Pool pool) {
		upstreams.add(new Upstream(pool));
	}

	@Override
	public void visit(DispatchAction action) {
		curLocation = new Location(action);
		locations.add(curLocation);
	}

	@Override
	public void visit(Forward forward) {
	}

	@Override
	public void visit(Redirect redirect) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(UrlMatcher urlMatcher) {
		// TODO Auto-generated method stub

	}

}
