package io.shaded.container;

import io.shaded.container.example.InjectionExample;
import io.shaded.container.example.TargetInjectExample;

public class temp {

	public static void main(String[] args) {
		//	TargetInjectExample example = new TargetInjectExample();
		//	example.print();
		TargetInjectExample example = new TargetInjectExample();
		example.print();
		Container.getInstance().inject(InjectionExample.class);
		example.print();
	}

}
