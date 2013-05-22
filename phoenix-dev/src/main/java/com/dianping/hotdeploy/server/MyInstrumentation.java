package com.dianping.hotdeploy.server;

import java.lang.instrument.Instrumentation;

public class MyInstrumentation {
	private static Instrumentation m_ins;

	public static void setInstrumentation(Instrumentation ins) {
		m_ins = ins;
	}

	public static Instrumentation getInstrumentation() {
		return m_ins;
	}
}
