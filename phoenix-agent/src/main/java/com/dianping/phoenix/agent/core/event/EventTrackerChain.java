package com.dianping.phoenix.agent.core.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class EventTrackerChain implements EventTracker {
	
	private final static Logger logger = Logger.getLogger(EventTrackerChain.class); 

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
				logger.error("error occurred in EventTracker.onEvent", e);
			}
		}
	}

}
