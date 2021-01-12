package io.shaded.container.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker for the class we are modifying.
 */
@java.lang.annotation.Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Target {

	/**
	 * Target class for transforming.
	 *
	 * @return class we are targeting.
	 */
	public Class<?> value();

}
