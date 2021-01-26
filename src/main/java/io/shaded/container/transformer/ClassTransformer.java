package io.shaded.container.transformer;

import io.shaded.container.injection.InjectionClassNode;

/**
 * Handles the transformation of the class to setup the definition for
 * the @{{@link java.lang.instrument.Instrumentation}}.
 */
public interface ClassTransformer {
	byte[] transform(InjectionClassNode injectionClassNode);
}
