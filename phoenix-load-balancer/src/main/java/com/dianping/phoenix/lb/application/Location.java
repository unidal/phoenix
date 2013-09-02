package com.dianping.phoenix.lb.application;

import com.dianping.phoenix.lb.domain.model.dispatch.DispatchAction;

public class Location {

	private DispatchAction action;

	public Location(DispatchAction action) {
		this.action = action;
	}

	public DispatchAction getAction() {
		return action;
	}

}
