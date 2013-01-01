package com.dianping.service.spi;

import java.util.Date;
import java.util.Map;

public interface ServiceBinding {
	public boolean getBooleanProperty(String key, boolean defaultValue);

	public Class<?> getClassProperty(String key, Class<?> defaultValue);

	public String getConfiguration();

	public Date getDateProperty(String key, Date defaultValue);

	public double getDoubleProperty(String key, double defaultValue);

	public int getIntProperty(String key, int defaultValue);

	public long getLongProperty(String key, long defaultValue);

	public Map<String, String> getProperties();

	public String getStringProperty(String key, String defaultValue);
}
