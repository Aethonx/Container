package io.shaded.container.reload;

import io.shaded.container.impl.ContainerImpl;
import io.shaded.container.injection.InjectionClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Used to reload the classes as we are having issues
 * with using the other way with the @{{@link java.lang.instrument.Instrumentation}}
 */
public final class ClassReloadHook implements ClassFileTransformer {

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer)
			throws IllegalClassFormatException {
		/*
		 * To redefine.
		 */
		if (!ContainerImpl.getInstance().getInjections().keySet().contains(classBeingRedefined)) return null;

		/*
		 * We are going to parse this class and find it's injection
		 * class counter parts, and then reverse the logic.
		 *
		 * invoke -> redefine on NMS class -> find the transformer class.
		 */
		InjectionClassNode cn = ContainerImpl.getInstance().getInjections().get(classBeingRedefined);
		return ContainerImpl.getInstance().getClassTransformer().transform(cn);
	}

}
