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
import java.util.logging.Logger;

/**
 * Implementation of @{{@link ClassParser}}
 */
public final class StandardClassParser implements ClassParser {

	/**
	 * Java logger to cut down the size of the build.
	 */
	private static Logger LOGGER = Logger.getLogger(ClassParser.class.getSimpleName());
	/**
	 * Description of @{{@link io.shaded.container.annotation.Target}}.
	 */
	private static final String TARGET_ANNOTATION_DESC = "Lio/shaded/container/annotation/Target;";
	/**
	 * Description of @{{@link io.shaded.container.annotation.Inject}}
	 */
	private static final String INJECT_ANNOTATION_DESC = "Lio/shaded/container/annotation/Inject;";

	/**
	 * Searches for the target class if not found return null.
	 */
	@Override
	public ClassNode getTargetClassNode(final Class<?> klass) {
		try {
			ClassReader classReader = new ClassReader(klass.getClass().getName());
			ClassNode cn = new ClassNode();
			classReader.accept(cn, 0);

			if (cn.visibleAnnotations != null) {
				for (AnnotationNode an : cn.visibleAnnotations) {
					if (an.desc.equals(TARGET_ANNOTATION_DESC)) {
						for (Object obj : an.values) {
							if (obj instanceof Type) {
								Type type = ((Type) obj);
								classReader = new ClassReader(type.getInternalName());
								cn = new ClassNode();
								classReader.accept(cn, 0);
								return cn;
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

				// TODO PARSE THE INFO.

			}
		}
		return nodes;
	}

}
