package com.dianping.phoenix.lb.domain.model;


public interface Stateful {

	public enum State {
		ENABLED, DISABLED, FORCED_OFFLINE;
	}

	public enum Availablity {
		OFFLINE, AVAILABLE;
	}

	public State getState();

	public Availablity getAvailablity();
	
	public boolean isAvailable();

	public void enable();

	public void disable();

	public void forceOffline();

}