package com.dianping.phoenix.lb.domain.model.dispatch;

import com.dianping.phoenix.lb.domain.model.Visitor;
import com.dianping.phoenix.lb.domain.model.pool.Pool;

public class Forward extends DispatchStep {

	private Pool pool;

	public Forward(Pool pool) {
		this.pool = pool;
	}

	public Pool getPool() {
		return pool;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
		visitor.visit(pool);
	}

}
