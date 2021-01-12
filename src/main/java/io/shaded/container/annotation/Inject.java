package io.shaded.container.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to mark which method we are replacing for injection.
 */
@java.lang.annotation.Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

	/**
	 * The injections method name.
	 */
	public String method();

	/**
	 * The injections method description default void method with no arguments.
	 */
	public String description() default "()V";

}
