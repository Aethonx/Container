package io.shaded.container.injection;

import io.shaded.container.Container;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Holds information about the class we are
 * trying to transform.
 */
public final class InjectionClassNode {

	/**
	 * All injection nodes.
	 */
	private final List<InjectionNode> injectionTargets;
	/**
	 * Target class node.
	 */
	private final ClassNode targetClassNode;

	public InjectionClassNode(Class<?> injectionClass) {
		Container container = Container.getInstance();
		this.targetClassNode = container.getClassParser().getTargetClassNode(injectionClass);
		ClassNode cn = null;
		try {
			ClassReader cr = new ClassReader(injectionClass.getCanonicalName());
			cn = new ClassNode();
			cr.accept(cn, 0);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		this.injectionTargets = container.getClassParser().getInjectionNodes(cn);
	}

	public List<InjectionNode> getInjectionTargets() {
		return injectionTargets;
	}

	public ClassNode getTargetClassNode() {
		return targetClassNode;
	}

}
