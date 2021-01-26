package io.shaded.container;

import io.shaded.container.impl.ContainerImpl;
import io.shaded.container.injection.InjectionClassNode;
import io.shaded.container.parser.ClassParser;
import io.shaded.container.transformer.ClassTransformer;

import java.util.Map;

public interface Container {
	void inject(Class<?>... classes);
	ClassParser getClassParser();
	ClassTransformer getClassTransformer();
	Map<Class<?>, InjectionClassNode> getInjections();
	static Container getInstance(){ return ContainerImpl.getInstance(); }
}
