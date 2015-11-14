package processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public class BadInheritanceProcessor extends AbstractProcessor<CtClass<?>> {

	private List<CtVariable<?>> variables = new LinkedList<CtVariable<?>>();
	private List<CtInvocation<?>> invocations;
	private Set<CtTypeReference<?>> supItf = new HashSet<CtTypeReference<?>>();
	private Map<String, List<String>> map = new HashMap<String, List<String>>();
	private Map<String, List<String>> mapItf = new HashMap<String, List<String>>();
	private List<String> possibleCorrections;

	@Override
	public void process(CtClass<?> ctClass) {
		variables.addAll(Query.getElements(ctClass, new TypeFilter<CtVariable<?>>(CtVariable.class)));
		// System.out.println("ok");
	}

	@Override
	public void processingDone() {
		int cpt = 0;
		// System.out.println("ok");
		for (CtVariable<?> c : variables) {
			// System.out.println("DEBUG : " + c + " " + c.getType());
			if (c.getType() != null && !c.getType().isPrimitive()) {
				possibleCorrections = new LinkedList<String>();
				supItf = new HashSet<CtTypeReference<?>>();
				mapItf = new HashMap<String, List<String>>();
				// System.out.println("\nVariable : " + c + " " +
				// c.getPosition());
				invocations = c.getParent(CtClass.class).getElements(new InvocationsByVariableFilter(c));
				List<String> listInv = new LinkedList<String>();
				for (CtInvocation<?> i : invocations) {
					listInv.add(i.getExecutable().toString().replace("E", "java.lang.Object").split("#")[1]);
				}
				// map.put(c.getType().getQualifiedName(), listInv);
				// System.out.println(c.getType());
				// System.out.println(c.getType().getSuperInterfaces().size());
				try {
					supItf.addAll(c.getType().getSuperInterfaces());
					boolean allIn = true;
					Set<CtTypeReference<?>> tmpItf = new HashSet<CtTypeReference<?>>();
					tmpItf.addAll(supItf);
					// System.out.println(supItf);
					while (allIn && !supItf.isEmpty()) {
						// System.out.println("OK");
						for (CtTypeReference<?> r : tmpItf) {
							// System.out.println(r);
							if (!r.getSuperInterfaces().isEmpty() && !supItf.containsAll(r.getSuperInterfaces())) {
								supItf.addAll(r.getSuperInterfaces());
								// supItf.add(r.getSuperclass());
								// System.out.println("itf : " + r + " =>
								// super-interfaces : " +
								// r.getSuperInterfaces());
								// System.out.println("super classes : " +
								// r.getSuperclass());
								allIn = true;
							} else
								allIn = false;
						}
						tmpItf.addAll(supItf);
					}
//					System.out.println("variable : " + c);
//					System.out.println("supITf : " + supItf);
					// System.out.println(c.getType().getSuperInterfaces());
					for (CtTypeReference<?> r : supItf) {
						List<String> listItf = new LinkedList<String>();
						// System.out.println(r + "\n" +r.);
						for (CtExecutableReference<?> e : r.getDeclaredExecutables()) {
							listItf.add(e.toString().split("#")[1]);
						}
						mapItf.put(r.getQualifiedName(), listItf);
					}

					// System.out.println(map);
//					System.out.println("mapItf : " + mapItf);
					// for (String s : map.keySet()) {
					// System.out.println("----------FOUND---------------");
					// System.out.println("Source : " + c.getPosition());
					// System.out.println("Variable : " + c.getSignature());
					for (String str : mapItf.keySet()) {
						// System.out.println(str);
						// if(c.getType().getQualifiedName().equals(s) &&
						// !map.get(s).isEmpty() &&
						// mapItf.get(str).containsAll(map.get(s))){
						if (!listInv.isEmpty() && mapItf.get(str).containsAll(listInv)
								&& !c.getType().getQualifiedName().equals(str)) {
							// if(result != "" &&
							// c.getType().getSuperInterfaces().contains(typeResult))
							// {
							// result = "----------FOUND---------------\nSource
							// : "
							// + c.getPosition() + "\nCorrection : "
							// + c.getType().getQualifiedName() + " --> " + str;
							// typeResult = c.getType();
							// }
							possibleCorrections.add(str);
							// System.out.println("Correction : " +
							// c.getType().getQualifiedName() + " --> " + str);
						}
						// }
					}
					if (!possibleCorrections.isEmpty()) {
						cpt++;
						System.out.println("---------------TROUVÉ---------------");
						// System.out.println("DEBUG : " +
						// c.getType().getQualifiedName());
						// System.out.println("DEBUG : " + mapItf + " " +
						// listInv);
						System.out.println("Source : " + c.getPosition());
						System.out.println("Variable : " + c);
						System.out.print("Correction(s) possible(s) : \n");
						for (String s : possibleCorrections)
							System.out.println("	- " + s);
					}
					// System.out.println("result = " + result);
				} catch (SpoonClassNotFoundException e) {
					System.out.println("Cannot load class " + e);
				}
			}
		}
		System.out.println("====================\nAnalyse terminée !");
		System.out.println(cpt + " corrections possibles identifiées !");

	}
}

class InvocationsByVariableFilter implements Filter<CtInvocation<?>> {

	private CtVariable<?> variable;

	public InvocationsByVariableFilter(CtVariable<?> v) {
		this.variable = v;
	}

	public boolean matches(CtInvocation<?> inv) {
		try {
			if (inv.getTarget() != null && this.variable != null
					&& this.variable.getType().getSimpleName().equals(inv.getTarget().getType().getSimpleName()))
				return true;
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}

}
