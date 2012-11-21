package com.dianping.phoenix.agent.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventTrackerChain implements EventTracker {

	private List<EventTracker> trackers;

	public EventTrackerChain() {
		trackers = new ArrayList<EventTracker>();
	}
	
	public EventTrackerChain(EventTracker eventTracker) {
		add(eventTracker);
	}

	public void add(EventTracker tracker) {
		if(tracker != null) {
			trackers.add(tracker);
		}
	}

	@Override
	public void onEvent(Event event) {
		for (EventTracker tracker : trackers) {
			try {
				tracker.onEvent(event);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
