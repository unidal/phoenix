package com.dianping.phoenix.agent.core.event;

public abstract class AbstractEventTracker implements EventTracker {

	@Override
	public final void onEvent(Event event) {
		if (event instanceof MessageEvent) {
			onMessageEvent((MessageEvent) event);
		} else if (event instanceof LifecycleEvent) {
			onLifecycleEvent((LifecycleEvent)event);
		} else {
			onOtherEvent(event);
		}
	}

	protected void onOtherEvent(Event event) {
	}

	protected void onLifecycleEvent(LifecycleEvent event) {
	}

	protected void onMessageEvent(MessageEvent event) {
	}

}
