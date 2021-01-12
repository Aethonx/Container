package io.shaded.container.injection;

import org.objectweb.asm.tree.MethodNode;

/**
 * Links back to the {@link InjectionClassNode} and holds information about
 * a method.
 */
public final class InjectionNode {

	/**
	 * Holds the name of the method we are
	 * trying to find.
	 */
	private final String methodName;

	/**
	 * Holds the method description.
	 */
	private final String description;

	/**
	 * Holds the annotated method from the @{{@link io.shaded.container.annotation.Target}}
	 */
	private final MethodNode method;

	public InjectionNode(final String methodName, final String description, final MethodNode method) {
		this.methodName = methodName;
		this.description = description;
		this.method = method;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getDescription() {
		return description;
	}

	public MethodNode getMethod() {
		return method;
	}

}
