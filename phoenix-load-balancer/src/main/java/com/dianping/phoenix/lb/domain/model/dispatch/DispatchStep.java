package com.dianping.phoenix.lb.domain.model.dispatch;

import com.dianping.phoenix.lb.domain.model.Visitable;
import com.dianping.phoenix.lb.domain.model.pool.Pool;

public abstract class DispatchStep implements Visitable {

	public Pool getPool() {
		return null;
	}
	
}
