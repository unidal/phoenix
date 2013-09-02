package com.dianping.phoenix.lb.domain.model;


public abstract class StatefulBaseEntity extends BaseEntity implements Stateful {

	private Stateful stateful = new BaseStateful();

	public State getState() {
		return stateful.getState();
	}

	public Availablity getAvailablity() {
		return stateful.getAvailablity();
	}

	public void enable() {
		stateful.enable();
	}

	public void disable() {
		stateful.disable();
	}

	public void forceOffline() {
		stateful.forceOffline();
	}

	@Override
	public boolean isAvailable() {
		return stateful.isAvailable();
	}

}
