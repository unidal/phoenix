package com.dianping.service.spi.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unidal.converter.ConverterManager;
import org.unidal.lookup.ContainerHolder;
import org.unidal.tuple.Pair;

import com.dianping.service.spi.ServiceConfigurator;
import com.dianping.service.spi.ServiceManager;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.annotation.Component;
import com.dianping.service.spi.annotation.Property;
import com.dianping.service.spi.lifecycle.ServiceContext;

public class DefaultServiceConfigurator extends ContainerHolder implements ServiceConfigurator {
	@Override
	public void configure(ServiceContext<?> ctx) throws Exception {
		injectDependencies(ctx.getServiceProvider());
		injectProperties(ctx.getServiceProvider(), ctx.getServiceBinding().getProperties());
	}

	private <T extends Annotation> List<Pair<Field, T>> getAnnotatedFields(ServiceProvider<?> serviceProvider,
	      Class<T> annotationType) {
		Class<?> clazz = serviceProvider.getClass();
		Field[] fields = clazz.getDeclaredFields();
		List<Pair<Field, T>> list = new ArrayList<Pair<Field, T>>();

		for (Field field : fields) {
			T annotation = field.getAnnotation(annotationType);

			if (annotation != null) {
				list.add(new Pair<Field, T>(field, annotation));
			}
		}

		return list;
	}

	private void injectDependencies(ServiceProvider<?> serviceProvider) throws Exception {
		ServiceManager serviceManager = lookup(ServiceManager.class); // late lookup to avoid cyclic dependency
		List<Pair<Field, Component>> list = getAnnotatedFields(serviceProvider, Component.class);

		for (Pair<Field, Component> entry : list) {
			Field field = entry.getKey();
			Component component = entry.getValue();
			Class<?> serviceType = component.type() == Component.Default.class ? field.getType() : component.type();
			String alias = component.value();
			Object childServie = serviceManager.getService(serviceType, alias);

			if (!field.isAccessible()) {
				field.setAccessible(true);
			}

			try {
				field.set(serviceProvider, childServie);
			} catch (Exception e) {
				throw new RuntimeException(String.format("Error when injecting field(%s) of % with %s!", field.getName(),
				      serviceProvider.getClass(), childServie.getClass()), e);
			}
		}
	}

	private void injectProperties(ServiceProvider<?> serviceProvider, Map<String, String> properties) {
		List<Pair<Field, Property>> list = getAnnotatedFields(serviceProvider, Property.class);

		for (Pair<Field, Property> entry : list) {
			Field field = entry.getKey();
			Property property = entry.getValue();
			String name = property.name();

			if (name.equals(Property.NOT_SPECIFIED)) {
				String fieldName = field.getName();

				if (fieldName.startsWith("m_")) {
					name = fieldName.substring(2);
				} else {
					name = fieldName;
				}
			}

			String value = properties.get(name);

			if (value == null) {
				if (!property.defaultValue().equals(Property.NOT_SPECIFIED)) {
					value = property.defaultValue();
				} else if (property.required()) {
					throw new IllegalStateException(String.format(
					      "No value configured for property(%s) of %s while it's required!", name,
					      serviceProvider.getClass()));
				}
			}

			injectProperty(serviceProvider, field, value);
		}
	}

	private void injectProperty(Object instance, Field field, String value) {
		Class<?> type = field.getType();
		Object convertedValue = ConverterManager.getInstance().convert(value, type);

		if (!field.isAccessible()) {
			field.setAccessible(true);
		}

		try {
			field.set(instance, convertedValue);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when injecting field(%s) of % with %s!", field.getName(),
			      instance.getClass(), convertedValue.getClass()), e);
		}
	}
}
