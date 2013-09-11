package com.dianping.phoenix.lb.domain.model;

import java.util.Date;

public abstract class BaseEntity {

	private long id;
	private Date addTime;
	private Date lastUpdateTime;
	private int version;

	public long getId() {
		return id;
	}

	public Date getAddTime() {
		return addTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public int getVersion() {
		return version;
	}
	
}
