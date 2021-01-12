package io.shaded.container.parser;

import io.shaded.container.injection.InjectionNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * Used to parse classes to obtain all of
 * the information needed for the redefining.
 */
public interface ClassParser {
	ClassNode getTargetClassNode(Class<?> klass);
	List<InjectionNode> getInjectionNodes(ClassNode classNode);
}
