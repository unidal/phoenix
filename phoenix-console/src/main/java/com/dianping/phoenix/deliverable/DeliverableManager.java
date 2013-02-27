package com.dianping.phoenix.deliverable;

import java.util.List;

import com.dianping.phoenix.console.dal.deploy.Deliverable;

public interface DeliverableManager {
	public boolean createDeliverable(String type, String version, String description) throws Exception;

	public boolean removeDeliverable(int id) throws Exception;

	public Deliverable getDeliverable(String type, DeliverableStatus status) throws Exception;

	public List<Deliverable> getAllDeliverables(String type, DeliverableStatus status) throws Exception;
}
