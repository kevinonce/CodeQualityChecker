package processors;

import java.util.LinkedList;
import java.util.List;

import filter.InvocationsByVariableFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class UnusedVariableProcessor extends AbstractProcessor<CtClass<?>> {

	private List<CtVariable<?>> variables = new LinkedList<CtVariable<?>>();
	private List<CtInvocation<?>> invocations = new LinkedList<CtInvocation<?>>();

	@Override
	public void process(CtClass<?> ctClass) {
		variables.addAll(Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class)));

	}

	public void processingDone() {
		for (CtVariable<?> v : variables) {
			if (v.getType() != null && v.getVisibility() != null) {
				invocations = v.getParent(CtClass.class).getElements(new InvocationsByVariableFilter(v));
				if (invocations.isEmpty())
					System.out.println("----------TROUVÉ--------\nVariable : " + v + "\nSource : "
							+ v.getPosition());
			}
		}
	}
}
