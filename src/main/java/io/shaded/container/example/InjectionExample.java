package io.shaded.container.example;

import io.shaded.container.annotation.Inject;
import io.shaded.container.annotation.Shadow;
import io.shaded.container.annotation.Target;

@Target(TargetInjectExample.class)
public class InjectionExample {

	@Shadow
	/*
	 * Java does not care that we ref a mock variable.
	 */
	public int mock;

	@Inject(method = "print", description = "()V")
	/*
	 * We assume the default is a void method with no args "()V".
	 */
	public void inject() {
		System.out.println("injected");
	}

}
