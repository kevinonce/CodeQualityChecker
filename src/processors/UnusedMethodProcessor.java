package processors;

import java.util.LinkedList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class UnusedMethodProcessor extends AbstractProcessor<CtClass<?>> {

	private List<CtMethod<?>> methods = new LinkedList<CtMethod<?>>();
	private List<CtInvocation<?>> invocations = new LinkedList<CtInvocation<?>>();

	@Override
	public void process(CtClass<?> ctClass) {
		methods.addAll(Query.getElements(ctClass, new TypeFilter<CtMethod<?>>(CtMethod.class)));
		invocations.addAll(Query.getElements(ctClass, new TypeFilter<CtInvocation<?>>(CtInvocation.class)));

	}

	public void processingDone() {
		boolean used = false;
		for (CtMethod<?> m : methods) {
			if (!m.getDeclaringType().getReference().isPrimitive()) {
				for (CtInvocation<?> i : invocations) {
					if (m.getSignature().split(" ")[1]
							.equals(i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1]))
						used = true;
				}
			}
			if (!used) {
				System.out.println(
						"----------TROUVÉ--------\nMéthode : " + m.getSignature() + "\nSource : " + m.getPosition());
			}
		}
	}

}
