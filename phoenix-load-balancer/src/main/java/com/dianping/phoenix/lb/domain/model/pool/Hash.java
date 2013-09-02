package com.dianping.phoenix.lb.domain.model.pool;

public class Hash implements LoadBalancingMethod {

	public enum Target {
		URI
	}

	public enum Method {
		CRC32
	}

	private Target target;
	private Method method;

	public Hash(Target target, Method method) {
		this.target = target;
		this.method = method;
	}

	public Target getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

}
