package com.dianping.phoenix.agent.core;

import java.util.ArrayList;
import java.util.List;

public class EventTrackerChain implements EventTracker {

	private List<EventTracker> trackers;

	public EventTrackerChain() {
		trackers = new ArrayList<EventTracker>();
	}
	
	public void add(EventTracker tracker) {
		trackers.add(tracker);
	}

	@Override
	public void onEvent(Event event) {
		for (EventTracker tracker : trackers) {
			tracker.onEvent(event);
		}
	}

}
