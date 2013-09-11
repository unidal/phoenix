package com.dianping.phoenix.lb.application;

import com.dianping.phoenix.lb.domain.model.pool.Pool;

public class Upstream {

	private Pool pool;

	public Upstream(Pool pool) {
		this.pool = pool;
	}

	public Pool getPool() {
		return pool;
	}

}
