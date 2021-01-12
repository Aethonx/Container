package io.shaded.container.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker for the class we are modifying.
 */
@java.lang.annotation.Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Target {

	/**
	 * Target class for transforming.
	 *
	 * @return class we are targeting.
	 */
	public Class<?> value();

}
