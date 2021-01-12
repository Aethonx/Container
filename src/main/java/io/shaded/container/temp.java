package io.shaded.container;

import io.shaded.container.annotation.Inject;
import io.shaded.container.annotation.Target;
import org.objectweb.asm.Type;

public class temp {
	public static void main(String[] args) {
		System.out.println(Type.getType(Inject.class).toString());
	}
}
