package com.dianping.phoenix.agent.core.event;

public interface EventTracker {

	void onEvent(Event event);
	
	public static EventTracker DUMMY_TRACKER = new EventTracker() {
		@Override
		public void onEvent(Event event) {
		}
	};
	
}
