package io.shaded.container.injection;

import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about the class we are
 * trying to transform.
 */
public final class InjectionClassNode {

	/**
	 * All injection nodes.
	 */
	private List<InjectionNode> injectionTargets;
	/**
	 * Target class node.
	 */
	private ClassNode targetClassNode;

	public InjectionClassNode(Class<?> injectionClass) {
		this.injectionTargets = new ArrayList<>();
	}



}
