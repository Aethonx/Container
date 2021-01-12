package io.shaded.container.transformer.impl;

import io.shaded.container.injection.InjectionClassNode;
import io.shaded.container.injection.InjectionNode;
import io.shaded.container.parser.ClassParser;
import io.shaded.container.transformer.ClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassDefinition;
import java.util.List;
import java.util.logging.Logger;

public final class StandardClassTransformer implements ClassTransformer {

	/**
	 * Java logger to cut down the size of the build.
	 */
	private static Logger LOGGER = Logger.getLogger(ClassParser.class.getSimpleName());

	@Override
	/*
	 * Transforms the class to redefine.
	 */
	public ClassDefinition transform(InjectionClassNode injectionClassNode) {
		try {
			ClassNode targetClassNode = injectionClassNode.getTargetClassNode();
			List<InjectionNode> nodes = injectionClassNode.getInjectionTargets();

			for (InjectionNode injectionNode : nodes) {
				MethodNode methodNode = this.getMethodNode(targetClassNode, injectionNode);
				if (methodNode == null) {
					LOGGER.severe("Couldn't find method with the name of " + injectionNode.getMethodName() + ", and the description " + injectionNode.getDescription() + " in the class " + targetClassNode.name + ".");
					continue;
				}
				int methodIndex = targetClassNode.methods.indexOf(methodNode); // Get the index of the method.
				methodNode.instructions = injectionNode.getMethod().instructions;
				targetClassNode.methods.set(methodIndex, methodNode); // Apply the swap.
			}

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			targetClassNode.accept(classWriter);

			byte[] classBytes = classWriter.toByteArray();
			Class<?> klass = Class.forName(targetClassNode.name.replaceAll("/", "."));

			return new ClassDefinition(klass, classBytes);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Get's the method node by the @{{@link InjectionNode}}
	 */
	private MethodNode getMethodNode(ClassNode classNode, InjectionNode injectionNode) {
		for (MethodNode mn : classNode.methods) {
			if (mn.name.equals(injectionNode.getMethodName()) && mn.desc.equals(injectionNode.getDescription())) {
				return mn;
			}
		}
		return null;
	}

}
