package com.dianping.phoenix.lb.domain.model;

public class BaseStateful implements Stateful {

	private State state = State.DISABLED;
	private Availablity availablity = Availablity.OFFLINE;

	@Override
	public State getState() {
		return state;
	}

	@Override
	public Availablity getAvailablity() {
		return availablity;
	}

	@Override
	public void enable() {
		state = State.ENABLED;
		availablity = Availablity.AVAILABLE;
	}

	@Override
	public void disable() {
		state = State.DISABLED;
		availablity = Availablity.OFFLINE;
	}

	@Override
	public void forceOffline() {
		state = State.FORCED_OFFLINE;
		availablity = Availablity.OFFLINE;
	}

	@Override
	public boolean isAvailable() {
		return availablity == Availablity.AVAILABLE;
	}

}
