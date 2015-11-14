package processors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>> {

	private List<CtVariable<?>> variables = new LinkedList<CtVariable<?>>();
	private List<CtInvocation<?>> invocations;
	private Map<String, List<String>> map = new HashMap<String, List<String>>();
	private Map<String, List<String>> mapItf = new HashMap<String, List<String>>();

	@Override
	public void process(CtClass<?> ctClass) {
		variables.addAll(Query.getElements(ctClass,
				new TypeFilter<CtVariable<?>>(CtVariable.class)));
	}

	@Override
	public void processingDone() {
		for (CtVariable<?> c : variables) {
			invocations = c.getParent(CtClass.class).getElements(
					new InvocationsByVariableFilter(c));
			List<String> listInv = new LinkedList<String>();
			for (CtInvocation<?> i : invocations) {
				listInv.add(i.getExecutable().toString()
						.replace("E", "java.lang.Object").split("#")[1]);
			}
			map.put(c.getType().getQualifiedName(), listInv);
			for (CtTypeReference<?> r : c.getType().getSuperInterfaces()) {
				List<String> listItf = new LinkedList<String>();

				for (CtExecutableReference<?> e : r.getDeclaredExecutables()) {
					listItf.add(e.toString().split("#")[1]);
					
				}
				mapItf.put(r.getQualifiedName(), listItf);
			}

//			System.out.println(map);
//			System.out.println(mapItf);
			
			for (String s : map.keySet()) {
				for (String str : mapItf.keySet()) {
					 if(c.getType().getQualifiedName().equals(s) && !map.get(s).isEmpty() && mapItf.get(str).containsAll(map.get(s))){
						 System.out.println("----------FOUND---------------");
						System.out.println("Source : " + c.getPosition());
						System.out.println("Variable : " + c.getSignature());
						System.out.println("Correction : " + s + " --> " + str);
					 }
				}
			}
		}
	}
}

class InvocationsByVariableFilter implements Filter<CtInvocation<?>> {

	private CtVariable<?> variable;

	public InvocationsByVariableFilter(CtVariable<?> v) {
		this.variable = v;
	}

	public boolean matches(CtInvocation<?> inv) {
		if (inv.getTarget() != null
				&& this.variable != null
				&& this.variable.getType().getSimpleName()
						.equals(inv.getTarget().getType().getSimpleName()))
			return true;
		return false;
	}

}
