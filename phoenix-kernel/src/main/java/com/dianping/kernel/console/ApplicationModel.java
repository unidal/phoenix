package com.dianping.kernel.console;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationModel {
	private static final long MINUTE = 60;

	private static final long HOUR = 60 * MINUTE;

	private static final long DAY = 24 * HOUR;

	private long m_startTime;

	public ApplicationModel() {
		m_startTime = System.currentTimeMillis();
	}

	public String getStartTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(m_startTime));
	}

	public String getRunningFor() {
		StringBuilder sb = new StringBuilder();
		long time = (System.currentTimeMillis() - m_startTime) / 1000;
		long days = time / DAY;
		long hours = (time % DAY) / HOUR;
		long minutes = (time % HOUR) / MINUTE;
		long seconds = (time % MINUTE);
		long val = 0;

		val += days;
		if (val > 0) {
			sb.append(days).append('d');
		}

		val += hours;
		if (val > 0) {
			sb.append(hours).append('h');
		}

		val += minutes;
		if (val > 0) {
			sb.append(minutes).append('m');
		}

		val += seconds;
		if (val > 0) {
			sb.append(seconds).append('s');
		}

		return sb.toString();
	}

	public long getMaxMemory() {
		long maxMemory = Runtime.getRuntime().maxMemory();

		return maxMemory;
	}

	public long getTotalMemory() {
		long totalMemory = Runtime.getRuntime().totalMemory();

		return totalMemory;
	}

	public long getUsedMemory() {
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		return usedMemory;
	}

	public double getUsedMemoryPercentage() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();

		return usedMemory * 1.0 / totalMemory;
	}

	public Map<String, Object> getOs() {
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		Map<String, Object> os = new LinkedHashMap<String, Object>();

		os.put("arch", bean.getArch());
		os.put("name", bean.getName());
		os.put("version", bean.getVersion());
		os.put("processors", bean.getAvailableProcessors());
		os.put("load", bean.getSystemLoadAverage());

		// for Sun JDK
		if (isInstanceOfInterface(bean.getClass(), "com.sun.management.OperatingSystemMXBean")) {
			com.sun.management.OperatingSystemMXBean b = (com.sun.management.OperatingSystemMXBean) bean;

			os.put("totalPhysicalMemory", b.getTotalPhysicalMemorySize());
			os.put("freePhysicalMemory", b.getFreePhysicalMemorySize());
			os.put("totalSwapSpace", b.getTotalSwapSpaceSize());
			os.put("freeSwapSpace", b.getFreeSwapSpaceSize());
			os.put("processCpuTime", b.getProcessCpuTime());
			os.put("commitedVirtualMemory", b.getCommittedVirtualMemorySize());
		}

		return os;
	}

	boolean isInstanceOfInterface(Class<?> clazz, String interfaceName) {
		if (clazz == Object.class) {
			return false;
		} else if (clazz.getName().equals(interfaceName)) {
			return true;
		}

		Class<?>[] interfaceclasses = clazz.getInterfaces();

		for (Class<?> interfaceClass : interfaceclasses) {
			if (isInstanceOfInterface(interfaceClass, interfaceName)) {
				return true;
			}
		}

		return isInstanceOfInterface(clazz.getSuperclass(), interfaceName);
	}
}
