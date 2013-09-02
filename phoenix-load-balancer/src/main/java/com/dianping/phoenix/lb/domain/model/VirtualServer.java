package com.dianping.phoenix.lb.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.lb.domain.model.dispatch.DispatchAction;
import com.dianping.phoenix.lb.domain.model.dispatch.DispatchStep;
import com.dianping.phoenix.lb.domain.model.pool.Pool;

public class VirtualServer extends StatefulBaseEntity implements Visitable {

	private String name;
	private int servicePort;
	private Pool defaultPool;
	private List<DispatchAction> dispatchActions = new ArrayList<DispatchAction>();

	public VirtualServer(String name, int servicePort, Pool defaultPool) {
		this.name = name;
		this.servicePort = servicePort;
		this.defaultPool = defaultPool;
	}

	public void setDispatchActions(List<DispatchAction> dispatchActions) {
		this.dispatchActions = dispatchActions;
	}

	public String getName() {
		return name;
	}

	public int getServicePort() {
		return servicePort;
	}

	public Pool getDefaultPool() {
		return defaultPool;
	}

	public List<DispatchAction> getDispatchActions() {
		return dispatchActions;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		for (DispatchAction action : dispatchActions) {
			action.accept(visitor);
		}
	}

}
