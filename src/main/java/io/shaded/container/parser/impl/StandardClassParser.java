package io.shaded.container.parser.impl;

import io.shaded.container.injection.InjectionNode;
import io.shaded.container.parser.ClassParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of @{{@link ClassParser}}
 */
public final class StandardClassParser implements ClassParser {

	/**
	 * Description of @{{@link io.shaded.container.annotation.Target}}.
	 */
	private static final String TARGET_ANNOTATION_DESC = "Lio/shaded/container/annotation/Target;";
	/**
	 * Description of @{{@link io.shaded.container.annotation.Inject}}
	 */
	private static final String INJECT_ANNOTATION_DESC = "Lio/shaded/container/annotation/Inject;";

	/**
	 * Method parameter for @{{@link io.shaded.container.annotation.Inject}}
	 */
	private static final String INJECT_METHOD_PARAM_NAME = "method";

	/**
	 * Method parameter for @{{@link io.shaded.container.annotation.Inject}}
	 */
	private static final String INJECT_METHOD_PARAM_DESCRIPTION = "description";

	/**
	 * Searches for the target class if not found return null.
	 */
	@Override
	public ClassNode getTargetClassNode(final Class<?> klass) {
		try {
			ClassReader classReader = new ClassReader(klass.getCanonicalName());
			ClassNode cn = new ClassNode();
			classReader.accept(cn, 0);
			if (cn.visibleAnnotations != null) {
				for (AnnotationNode an : cn.visibleAnnotations) {
					if (an.desc.equals(TARGET_ANNOTATION_DESC)) {
						for (Object obj : an.values) {
							if (obj instanceof Type) {
								Type type = ((Type) obj);
								ClassReader cr = new ClassReader(type.getInternalName());
								ClassNode classNode = new ClassNode();
								cr.accept(classNode, 0);
								return classNode;
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Finds all injection nodes in the class node.
	 *
	 * @param classNode Class node of the the @{{@link io.shaded.container.annotation.Target}} class.
	 * @return All found injection nodes.
	 */
	@Override
	public List<InjectionNode> getInjectionNodes(final ClassNode classNode) {
		List<InjectionNode> nodes = new ArrayList<>();
		for (MethodNode method : classNode.methods) {
			if (method.visibleAnnotations == null) continue; // - No annotations are found.
			for (AnnotationNode annotation : method.visibleAnnotations) {
				if (annotation == null) continue; // Sanity
				if (!annotation.desc.equals(INJECT_ANNOTATION_DESC)) continue; // This is not the right annotation.
				List<String> args = new ArrayList<>();
				for (Object obj : annotation.values) {
					if (obj instanceof String) {
						String str = ((String) obj);
						if (str.equals(INJECT_METHOD_PARAM_DESCRIPTION) || str.equals(INJECT_METHOD_PARAM_NAME)) {
							continue;
						} // Don't parse the parameters.
						args.add(str);
					}
				}
				InjectionNode injectionNode = new InjectionNode(args.get(0), args.get(1), method); // We know it will always be two values returned.
				nodes.add(injectionNode);
			}
		}
		return nodes;
	}

}
