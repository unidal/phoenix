package com.dianping.service.spi.lifecycle;

public enum ServiceState {
	CREATED(1, 2, 5),

	CONFIGURED(2, 3, 5),

	STARTED(3, 4, 5),

	SUSPENDED(4, 3, 5),

	STOPPED(5);

	private int m_id;

	private int[] m_nextIds;

	private ServiceState(int id, int... nextIds) {
		m_id = id;
		m_nextIds = nextIds;
	}

	public int getId() {
		return m_id;
	}

	public boolean canMoveTo(ServiceState nextState) {
		int nextId = nextState.getId();

		for (int id : m_nextIds) {
			if (id == nextId) {
				return true;
			}
		}

		return false;
	}
}
