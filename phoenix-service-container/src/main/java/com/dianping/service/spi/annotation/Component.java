package com.dianping.service.spi.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD })
public @interface Component {
	public Class<?> type() default Default.class;

	public String value() default "default";

	public static final class Default {
	}
}
