package com.dianping.phoenix.lb.domain.model.pool;

import com.dianping.phoenix.lb.domain.model.Node;
import com.dianping.phoenix.lb.domain.model.StatefulBaseEntity;

public class PoolMember extends StatefulBaseEntity {

	private Node node;
	private int servicePort;
	private int ratio;

	public PoolMember(Node node, int servicePort, int ratio) {
		this.node = node;
		this.servicePort = servicePort;
		this.ratio = ratio;
	}

	public Node getNode() {
		return node;
	}

	public int getServicePort() {
		return servicePort;
	}

	public int getRatio() {
		return ratio;
	}

}
