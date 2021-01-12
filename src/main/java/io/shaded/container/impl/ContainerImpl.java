package io.shaded.container.impl;

import io.shaded.container.Container;
import io.shaded.container.agent.RedefineClassAgent;
import io.shaded.container.injection.InjectionClassNode;
import io.shaded.container.parser.ClassParser;
import io.shaded.container.parser.impl.StandardClassParser;
import io.shaded.container.transformer.ClassTransformer;
import io.shaded.container.transformer.impl.StandardClassTransformer;

import java.lang.instrument.ClassDefinition;
import java.util.logging.Logger;

public final class ContainerImpl implements Container {

	/**
	 * Java logger to cut down the size of the build.
	 */
	private static Logger LOGGER = Logger.getLogger(Container.class.getSimpleName());

	/**
	 * Instance of this class.
	 */
	private static final transient Container instance = new ContainerImpl();

	private final ClassParser parser;
	private final ClassTransformer transformer;

	public ContainerImpl() {
		parser = new StandardClassParser();
		transformer = new StandardClassTransformer();
	}

	@Override
	public void inject(final Class<?>... classes) {
		try {
			for (Class<?> klass : classes) {
				InjectionClassNode injectionClassNode = new InjectionClassNode(klass);
				ClassTransformer transformer = this.getClassTransformer();
				ClassDefinition definition = transformer.transform(injectionClassNode);
				RedefineClassAgent.redefineClasses(definition);
			}
		}catch (Exception exception){
			exception.printStackTrace();
		}
	}

	@Override
	public ClassParser getClassParser() {
		return this.parser;
	}

	@Override
	public ClassTransformer getClassTransformer() {
		return this.transformer;
	}

	public static Container getInstance() {
		return instance;
	}

}
