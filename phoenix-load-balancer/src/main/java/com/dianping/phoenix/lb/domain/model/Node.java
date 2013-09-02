package com.dianping.phoenix.lb.domain.model;

public class Node extends StatefulBaseEntity {

	private String address;
	private String name;

	public Node(String address, String name) {
		this.address = address;
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

}
