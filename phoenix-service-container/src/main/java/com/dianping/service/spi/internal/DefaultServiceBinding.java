package com.dianping.service.spi.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dianping.service.spi.ServiceBinding;

public class DefaultServiceBinding implements ServiceBinding {
	private Map<String, String> m_propreties;

	private String m_configuration;

	public DefaultServiceBinding(String configuration) {
		this(new LinkedHashMap<String, String>(), configuration);
	}

	public DefaultServiceBinding(Map<String, String> properties, String configuration) {
		m_propreties = properties;
		m_configuration = configuration;
	}

	@Override
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String value = getStringProperty(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			return "true".equals(value);
		}
	}

	@Override
	public Class<?> getClassProperty(String key, Class<?> defaultValue) {
		String value = getStringProperty(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Class.forName(value);
			} catch (ClassNotFoundException e) {
				// try next
			}

			try {
				return Thread.currentThread().getContextClassLoader().loadClass(value);
			} catch (ClassNotFoundException e2) {
				return defaultValue;
			}
		}
	}

	@Override
	public String getConfiguration() {
		return m_configuration;
	}

	@Override
	public Date getDateProperty(String key, Date defaultValue) {
		String value = getStringProperty(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				return format.parse(value);
			} catch (ParseException e) {
				// try next
			}

			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

				return format.parse(value);
			} catch (ParseException e) {
				return defaultValue;
			}
		}
	}

	@Override
	public double getDoubleProperty(String key, double defaultValue) {
		String value = getStringProperty(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	@Override
	public int getIntProperty(String key, int defaultValue) {
		String value = getStringProperty(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	@Override
	public long getLongProperty(String key, long defaultValue) {
		String value = getStringProperty(key, null);

		if (value == null) {
			return defaultValue;
		} else {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	@Override
	public Map<String, String> getProperties() {
		return m_propreties;
	}

	@Override
	public String getStringProperty(String key, String defaultValue) {
		String value = m_propreties.get(key);

		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

}
