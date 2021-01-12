package io.shaded.container;

import io.shaded.container.impl.ContainerImpl;
import io.shaded.container.parser.ClassParser;
import io.shaded.container.transformer.ClassTransformer;

public interface Container {
	void inject(Class<?>... classes);
	ClassParser getClassParser();
	ClassTransformer getClassTransformer();
	static Container getInstance(){ return ContainerImpl.getInstance(); }
}
