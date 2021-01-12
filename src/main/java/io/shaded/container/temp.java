package io.shaded.container;

import io.shaded.container.agent.RedefineClassAgent;
import io.shaded.container.example.InjectionExample;
import io.shaded.container.example.TargetInjectExample;
import io.shaded.container.injection.InjectionNode;
import io.shaded.container.parser.impl.StandardClassParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.List;

public class temp {
	public static void main(String[] args) throws RedefineClassAgent.FailedToLoadAgentException, IOException {
		TargetInjectExample targetInjectExample = new TargetInjectExample();
		targetInjectExample.print();
		Container.getInstance().inject(InjectionExample.class);
		targetInjectExample.print();
	}
}
