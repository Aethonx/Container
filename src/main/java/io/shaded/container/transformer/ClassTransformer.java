package io.shaded.container.transformer;

import io.shaded.container.injection.InjectionClassNode;

import java.lang.instrument.ClassDefinition;

/**
 * Handles the transformation of the class to setup the definition for
 * the @{{@link java.lang.instrument.Instrumentation}}.
 */
public interface ClassTransformer {
	ClassDefinition transform(InjectionClassNode injectionClassNode);
}
