package io.shaded.container.impl;

import io.shaded.container.Container;
import io.shaded.container.agent.RedefineClassAgent;
import io.shaded.container.injection.InjectionClassNode;
import io.shaded.container.parser.ClassParser;
import io.shaded.container.parser.impl.StandardClassParser;
import io.shaded.container.transformer.ClassTransformer;
import io.shaded.container.transformer.impl.StandardClassTransformer;

import java.util.HashMap;
import java.util.Map;
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

	/**
	 * Map of injection class to there node.
	 */
	private final Map<Class<?>, InjectionClassNode> injections;

	private final ClassParser parser;
	private final ClassTransformer transformer;

	public ContainerImpl() {
		this.parser = new StandardClassParser();
		this.transformer = new StandardClassTransformer();
		this.injections = new HashMap<>();
	}

	@Override
	public void inject(final Class<?>... classes) {
		try {
			for (Class<?> klass : classes) {
				InjectionClassNode injectionClassNode = new InjectionClassNode(klass);
				Class<?> reload = Class.forName(injectionClassNode.getTargetClassNode().name.replace("/", "."));
				this.injections.put(reload, injectionClassNode);
				RedefineClassAgent.redefineClasses(reload);
			}
		} catch (Exception exception) {
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

	public Map<Class<?>, InjectionClassNode> getInjections() {
		return injections;
	}
}
